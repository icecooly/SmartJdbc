package io.itit.smartjdbc.provider.entity;

/**
 * 
 * @author skydu
 *
 */
public class Entity {
	
	private String tableName;
	
	public Entity(String tableName) {
		this.tableName=tableName;
	}
	//
	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
