package io.itit.smartjdbc.provider;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.enums.DatabaseType;
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
	 * @param name
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
	
	/**
	 * 
	 * @return
	 */
	public DatabaseType getDatabaseType() {
		return smartDataSource.getDatabaseType();
	}
	
}
