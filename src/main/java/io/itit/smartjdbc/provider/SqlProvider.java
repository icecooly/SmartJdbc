package io.itit.smartjdbc.provider;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.provider.entity.SqlBean;

/**
 * 
 * @author skydu
 *
 */
public abstract class SqlProvider {
	//
	public static final String MAIN_TABLE_ALIAS="a";
	//
	protected SmartDataSource smartDataSource;
	//
	public SqlProvider(SmartDataSource smartDataSource) {
		this.smartDataSource=smartDataSource;
	}
	//
	protected static final HashSet<Class<?>> WRAP_TYPES=new HashSet<>();
	static{
		WRAP_TYPES.add(Boolean.class);
		WRAP_TYPES.add(Character.class);
		WRAP_TYPES.add(Byte.class);
		WRAP_TYPES.add(Short.class);
		WRAP_TYPES.add(Integer.class);
		WRAP_TYPES.add(Long.class);
		WRAP_TYPES.add(BigDecimal.class);
		WRAP_TYPES.add(BigInteger.class);
		WRAP_TYPES.add(Double.class);
		WRAP_TYPES.add(Float.class);
		WRAP_TYPES.add(String.class);
		WRAP_TYPES.add(Date.class);
		WRAP_TYPES.add(Timestamp.class);
		WRAP_TYPES.add(java.sql.Date.class);
		WRAP_TYPES.add(Byte[].class);
		WRAP_TYPES.add(byte[].class);
		WRAP_TYPES.add(int.class);
		WRAP_TYPES.add(boolean.class);
		WRAP_TYPES.add(char.class);
		WRAP_TYPES.add(byte.class);
		WRAP_TYPES.add(short.class);
		WRAP_TYPES.add(int.class);
		WRAP_TYPES.add(long.class);
		WRAP_TYPES.add(float.class);
		WRAP_TYPES.add(double.class);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public String convertFieldName(String name) {
		return smartDataSource.convertFieldName(name);
	}
	
	
	/**
	 * 
	 * @param entityClass
	 * @return
	 */
	public String getTableName(Class<?> entityClass) {
		return addIdentifier(smartDataSource.getTableName(entityClass));
	}
	
	/**
	 * 
	 * @param column
	 * @return
	 */
	public String addIdentifier(String name) {
		 return identifier()+name+identifier();
	}
	
	/**
	 * 
	 * @return
	 */
	public abstract SqlBean build();
	
	/**
	 * 
	 * @return
	 */
	public String identifier() {
		return smartDataSource.identifier();
	}
	//
	public String getAlias(String alias) {
		if(alias==null) {
			return null;
		}
		return addIdentifier(alias);
	}

	/**
	 * @return the smartDataSource
	 */
	public SmartDataSource getSmartDataSource() {
		return smartDataSource;
	}


	/**
	 * @param smartDataSource the smartDataSource to set
	 */
	public void setSmartDataSource(SmartDataSource smartDataSource) {
		this.smartDataSource = smartDataSource;
	}
	
}
