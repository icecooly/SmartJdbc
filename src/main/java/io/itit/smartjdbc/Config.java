package io.itit.smartjdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.sql.DataSource;

import io.itit.smartjdbc.connection.ConnectionManager;
import io.itit.smartjdbc.connection.TransactionManager;

/**
 * 
 * @author skydu
 *
 */
public class Config {
	//
	public static final String DEFAULT_DATASOURCE_INDEX = "master";
	/**
	 * 
	 */
	private static Map<String, DataSource> dataSources = new HashMap<>();
	/**
	 * 
	 */
	private static List<DAOInterceptor> daoInterceptors = new ArrayList<>();
	/**
	 * 
	 */
	private static List<SqlInterceptor> sqlInterceptors = new ArrayList<>();
	//
	/**
	 * javaFieldName->dbName
	 */
	private static Function<String, String> convertFieldNameFunc = (fieldName) -> {
		return fieldName;
	};

	/** 是否开启字段驼峰映射  默认开启*/
	private static boolean fieldCamelCase = true;
	
	/** 列名允许的正则 默认数字+26个字母大小写+下划线+``+点*/
	private static String columnNameRegex="^[A-Za-z0-9_`\\\\.]+$";

	/**
	 * @return the convertFieldNameFunc
	 */
	public static Function<String, String> getConvertFieldNameFunc() {
		return convertFieldNameFunc;
	}

	/**
	 * @param convertFieldNameFunc the convertFieldNameFunc to set
	 */
	public static void setConvertFieldNameFunc(Function<String, String> convertFieldNameFunc) {
		Config.convertFieldNameFunc = convertFieldNameFunc;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static String convertFieldName(String name) {
		if(fieldCamelCase) {
			return convertFieldNameCamelCase(name);
		}
		return convertFieldNameFunc.apply(name);
	}

	/**
	 * @return the daoInterceptors
	 */
	public static List<DAOInterceptor> getDaoInterceptors() {
		return daoInterceptors;
	}

	/**
	 * @param daoInterceptors the daoInterceptors to set
	 */
	public static void setDaoInterceptors(List<DAOInterceptor> daoInterceptors) {
		Config.daoInterceptors = daoInterceptors;
	}

	/**
	 * 
	 * @param interceptor
	 */
	public static void addDAOInterceptor(DAOInterceptor interceptor) {
		daoInterceptors.add(interceptor);
	}

	/**
	 * 
	 * @return
	 */
	public static Map<String, DataSource> getDataSources() {
		return dataSources;
	}

	/**
	 * 
	 * @param dataSource
	 */
	public static void addDataSource(DataSource dataSource) {
		dataSources.put(DEFAULT_DATASOURCE_INDEX, dataSource);
	}

	/**
	 * 
	 * @param dataSourceIndex
	 * @param dataSource
	 */
	public static void addDataSource(String dataSourceIndex, DataSource dataSource) {
		dataSources.put(dataSourceIndex, dataSource);
	}

	/**
	 * 
	 * @param transactionManager
	 */
	public static void setTransactionManager(TransactionManager transactionManager) {
		ConnectionManager.setTransactionManager(transactionManager);
	}

	//
	/**
	 * @return the sqlInterceptors
	 */
	public static List<SqlInterceptor> getSqlInterceptors() {
		return sqlInterceptors;
	}

	/**
	 * @param sqlInterceptors the sqlInterceptors to set
	 */
	public static void setSqlInterceptors(List<SqlInterceptor> sqlInterceptors) {
		Config.sqlInterceptors = sqlInterceptors;
	}

	/**
	 * 
	 * @param sqlInterceptor
	 */
	public static void addSqlInterceptor(SqlInterceptor sqlInterceptor) {
		sqlInterceptors.add(sqlInterceptor);
	}

	/**
	 * 
	 * @return
	 */
	public static boolean isFieldCamelCase() {
		return fieldCamelCase;
	}

	/**
	 * 
	 * @param fieldCamelCase
	 */
	public static void setFieldCamelCase(boolean fieldCamelCase) {
		Config.fieldCamelCase = fieldCamelCase;
	}

	/**
	 * 
	 * @param dataSources
	 */
	public static void setDataSources(Map<String, DataSource> dataSources) {
		Config.dataSources = dataSources;
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

	/**
	 * @return the columnNameRegex
	 */
	public static String getColumnNameRegex() {
		return columnNameRegex;
	}

	/**
	 * @param columnNameRegex the columnNameRegex to set
	 */
	public static void setColumnNameRegex(String columnNameRegex) {
		Config.columnNameRegex = columnNameRegex;
	}
	
}
