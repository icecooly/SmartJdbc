package io.itit.smartjdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.connection.TransactionManager;
import io.itit.smartjdbc.enums.DatabaseType;
import io.itit.smartjdbc.util.StringUtil;

/**
 * 
 * @author skydu
 *
 */
public class SmartDataSource {
	//
	private static Logger logger=LoggerFactory.getLogger(SmartDataSource.class);
	//
	public static final String DEFAULT_DATASOURCE_INDEX = "master";
	//
	private DataSource dataSource;
	
	private DatabaseType databaseType;
	
	private List<DAOInterceptor> daoInterceptors;
	
	private List<SqlInterceptor> sqlInterceptors;
	
	private TransactionManager transactionManager;
	
	private boolean fieldCamelCase;
	/**
	 * javaFieldName->dbName
	 */
	private Function<String, String> convertFieldNameFunc = (fieldName) -> {
		return fieldName;
	};
	//
	public SmartDataSource(DataSource dataSource,TransactionManager transactionManager) {
		this.dataSource=dataSource;
		this.transactionManager=transactionManager;
		this.databaseType=getDatabaseType();
		this.sqlInterceptors=new ArrayList<>();
		this.daoInterceptors=new ArrayList<>();
	}
	/**
	 * @return the convertFieldNameFunc
	 */
	public Function<String, String> getConvertFieldNameFunc() {
		return convertFieldNameFunc;
	}

	/**
	 * @param convertFieldNameFunc the convertFieldNameFunc to set
	 */
	public void setConvertFieldNameFunc(Function<String, String> convertFieldNameFunc) {
		this.convertFieldNameFunc = convertFieldNameFunc;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public String convertFieldName(String name) {
		if(fieldCamelCase) {
			return convertFieldNameCamelCase(name);
		}
		return convertFieldNameFunc.apply(name);
	}

	/**
	 * @return the daoInterceptors
	 */
	public List<DAOInterceptor> getDaoInterceptors() {
		return daoInterceptors;
	}

	/**
	 * @param daoInterceptors the daoInterceptors to set
	 */
	public void setDaoInterceptors(List<DAOInterceptor> daoInterceptors) {
		this.daoInterceptors = daoInterceptors;
	}

	/**
	 * 
	 * @param interceptor
	 */
	public void addDAOInterceptor(DAOInterceptor interceptor) {
		daoInterceptors.add(interceptor);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static  String convertFieldNameCamelCase(String name) {
		StringBuffer result = new StringBuffer();
		for (char c : name.toCharArray()) {
			if (Character.isUpperCase(c)) {
				result.append("_");
			}
			result.append(Character.toLowerCase(c));
		}
		return result.toString();
	}
	//

	//
	/**
	 * @return the sqlInterceptors
	 */
	public List<SqlInterceptor> getSqlInterceptors() {
		return sqlInterceptors;
	}

	/**
	 * @param sqlInterceptors the sqlInterceptors to set
	 */
	public void setSqlInterceptors(List<SqlInterceptor> sqlInterceptors) {
		this.sqlInterceptors = sqlInterceptors;
	}

	/**
	 * 
	 * @param sqlInterceptor
	 */
	public void addSqlInterceptor(SqlInterceptor sqlInterceptor) {
		sqlInterceptors.add(sqlInterceptor);
	}

	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @return the databaseType
	 */
	public DatabaseType getDatabaseType() {
		if(databaseType==null) {
			Connection conn=null;
			try {
				conn=dataSource.getConnection();
				String connClassName=conn.getClass().getName();
				if(connClassName.contentEquals("com.mysql.cj.jdbc.ConnectionImpl")) {
					databaseType=DatabaseType.MYSQL;
				}
				logger.info("getDatabaseType {} databaseType:{]",conn.getClass().getName(),databaseType);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}finally {
				if(conn!=null) {
					try {
						conn.close();
					} catch (SQLException e) {
						logger.error(e.getMessage(),e);
					}
				}
			}
		}
		return databaseType;
	}

	/**
	 * @param databaseType the databaseType to set
	 */
	public void setDatabaseType(DatabaseType databaseType) {
		this.databaseType = databaseType;
	}

	/**
	 * @return the transactionManager
	 */
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	/**
	 * @param transactionManager the transactionManager to set
	 */
	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * @return the fieldCamelCase
	 */
	public boolean isFieldCamelCase() {
		return fieldCamelCase;
	}

	/**
	 * @param fieldCamelCase the fieldCamelCase to set
	 */
	public void setFieldCamelCase(boolean fieldCamelCase) {
		this.fieldCamelCase = fieldCamelCase;
	}

	/**
	 * 
	 * @param datasourceIndex
	 * @return
	 */
	public Connection getConnection() {
		if(transactionManager==null) {
			throw new SmartJdbcException("transactionManager is null");
		}
		return transactionManager.getConnection();
	}

	/**
	 * 
	 */
	public void commit() {
		transactionManager.commit();
	}
	
	/**
	 * 
	 */
	public void rollback() {
		transactionManager.rollback();
	}
	
	/**
	 * 
	 * @param entityClass
	 * @return
	 */
	public String getTableName(Class<?> entityClass) {
		Entity entity=entityClass.getAnnotation(Entity.class);
		if (entity != null) {
			if(!StringUtil.isEmpty(entity.tableName())) {//tableName first
				return entity.tableName();
			}
		}
		throw new SmartJdbcException("tableName not found "+entityClass);
	}
	
}
