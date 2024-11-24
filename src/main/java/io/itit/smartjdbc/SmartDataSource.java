package io.itit.smartjdbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.enums.DatabaseType;
import io.itit.smartjdbc.service.DefaultConvertFieldNameService;
import io.itit.smartjdbc.service.IConvertFieldNameService;
import io.itit.smartjdbc.session.SqlSession;
import io.itit.smartjdbc.session.TransactionManager;
import io.itit.smartjdbc.util.SmartJdbcUtils;

/**
 * 
 * @author skydu
 *
 */
public class SmartDataSource {
	//

	private static Logger log=LoggerFactory.getLogger(SmartDataSource.class);
	//
	private DataSource dataSource;
	
	private DatabaseType databaseType;
	
	private List<SqlInterceptor> sqlInterceptors;
	
	private TransactionManager transactionManager;
	
	private Map<String,DatabaseType> driverClassMapping;
	
	private String identifier="";
	
	private IConvertFieldNameService convertFieldNameService;
	//
	public SmartDataSource(DataSource dataSource, TransactionManager transaction) {
		this.dataSource=dataSource;
		this.transactionManager=transaction;
		this.sqlInterceptors=new ArrayList<>();
		this.driverClassMapping=new HashMap<>();
		this.convertFieldNameService=new DefaultConvertFieldNameService();
	}
	//
	private void defaultDriverClassNameMapping() {
		driverClassMapping.put("com.mysql.cj.jdbc.Driver", DatabaseType.MYSQL);
		driverClassMapping.put("com.mysql.jdbc.Driver", DatabaseType.MYSQL);
		driverClassMapping.put("org.postgresql.Driver", DatabaseType.POSTGRESQL);
		driverClassMapping.put("org.opengauss.Driver", DatabaseType.POSTGRESQL);
		driverClassMapping.put("com.kingbase8.Driver", DatabaseType.KINGBASE);
	}
	//
	private void loadingDriverClassNameMapping() {
		defaultDriverClassNameMapping();
		String fileName=System.getProperty("smartjdbc.driverclassmapping.file");
		if(fileName!=null){
			try {
				File bf=new File(fileName);
				try (InputStream input = new FileInputStream(bf)) {
		            Properties prop = new Properties();
		            prop.load(input);
		            Iterator<String> it=prop.stringPropertyNames().iterator();
		            while(it.hasNext()){
		                String driverClassName=it.next();
		                String type=prop.getProperty(driverClassName);
		                if(driverClassName==null||type==null) {
		                	continue;
		                }
		                DatabaseType databaseType=DatabaseType.valueOf(type.trim());
		                if(databaseType==null) {
		                	log.warn("type not support {}",type);
		                	continue;
		                }
		                driverClassMapping.put(driverClassName.trim(), databaseType);
		            }
		        }
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}
	}
	//
	public void init() throws Exception {
		Connection conn=null;
		try {
			loadingDriverClassNameMapping();
			conn=dataSource.getConnection();
			String driverClassName=DriverManager.getDriver(conn.getMetaData().getURL()).getClass().getName();
			databaseType=driverClassMapping.get(driverClassName);
			if(databaseType==null) {
				throw new SmartJdbcException("not support database "+driverClassName);
			}
			identifier=SmartJdbcUtils.identifier(databaseType);
			log.info("init driverClassName:{} databaseType:{} identifier:{}",
					driverClassName,databaseType,identifier);
		} catch (Exception e) {
			throw e;
		}finally {
			if(conn!=null) {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error(e.getMessage(),e);
				}
			}
		}
	}
	//
	public void start() throws Exception{
		
	}
	//
	
	/**
	 * 
	 * @param entityClass
	 * @param fieldName
	 * @return
	 */
	public String convertFieldNameToColumnName(Class<?> entityClass, String fieldName) {
		return convertFieldNameService.convert(entityClass, fieldName);
	}
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
		return databaseType;
	}

	/**
	 * @param databaseType the databaseType to set
	 */
	public void setDatabaseType(DatabaseType databaseType) {
		this.databaseType = databaseType;
	}

	/**
	 * @return the transaction
	 */
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	/**
	 * @param transaction the transaction to set
	 */
	public void setTransaction(TransactionManager transaction) {
		this.transactionManager = transaction;
	}
	/**
	 * 
	 * @return
	 */
	public SqlSession getSession() {
		try {
			if(transactionManager==null) {
				throw new SmartJdbcException("transactionManager is null");
			}
			return transactionManager.getSession();
		} catch (Exception e) {
			throw new SmartJdbcException(e.getMessage(),e);
		}
	}
	
	/**
	 * 
	 * @param entityClass
	 * @return
	 */
	public String getTableName(Class<?> entityClass) {
		Entity entity=entityClass.getAnnotation(Entity.class);
		if (entity != null) {
			if(!SmartJdbcUtils.isEmpty(entity.tableName())) {//tableName first
				return entity.tableName();
			}
		}
		throw new SmartJdbcException("tableName not found "+entityClass);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getIdentifier() {
		return identifier;
	}
	/**
	 * 
	 * @param identifier
	 */
	public void setIdentifier(String identifier) {
		this.identifier=identifier;
	}
	/**
	 * @return the driverClassMapping
	 */
	public Map<String, DatabaseType> getDriverClassMapping() {
		return driverClassMapping;
	}
	/**
	 * @param driverClassMapping the driverClassMapping to set
	 */
	public void setDriverClassMapping(Map<String, DatabaseType> driverClassMapping) {
		this.driverClassMapping = driverClassMapping;
	}
	/**
	 * @return the convertFieldNameService
	 */
	public IConvertFieldNameService getConvertFieldNameService() {
		return convertFieldNameService;
	}
	/**
	 * @param convertFieldNameService the convertFieldNameService to set
	 */
	public void setConvertFieldNameService(IConvertFieldNameService convertFieldNameService) {
		this.convertFieldNameService = convertFieldNameService;
	}
}
