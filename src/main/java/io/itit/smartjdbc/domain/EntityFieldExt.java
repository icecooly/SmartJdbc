package io.itit.smartjdbc.domain;

/**
 * 
 * @author skydu
 *
 */
public class EntityFieldExt {

	private String uniqId;
	
	private String name;//不一定等于field.getName()
	
	private EntityFieldSetting entityField;
	
	private JoinSetting leftJoin;
	
	private ForeignKeySetting foreignKey;
	
	private String tableAlias;
	
	private String asName;
	
	//
	public String info() {
		StringBuilder info=new StringBuilder();
		info.append("\nEntityFieldInfo[").append(tableAlias).append(".").append(name);
		if(asName!=null) {
			info.append(" as ").append(asName);
		}
		info.append("]");
		return info.toString();
	}
	//

	/**
	 * @return the entityField
	 */
	public EntityFieldSetting getEntityField() {
		return entityField;
	}

	/**
	 * @param entityField the entityField to set
	 */
	public void setEntityField(EntityFieldSetting entityField) {
		this.entityField = entityField;
	}

	/**
	 * @return the leftJoin
	 */
	public JoinSetting getLeftJoin() {
		return leftJoin;
	}

	/**
	 * @param leftJoin the leftJoin to set
	 */
	public void setLeftJoin(JoinSetting leftJoin) {
		this.leftJoin = leftJoin;
	}

	/**
	 * @return the tableAlias
	 */
	public String getTableAlias() {
		return tableAlias;
	}

	/**
	 * @param tableAlias the tableAlias to set
	 */
	public void setTableAlias(String tableAlias) {
		this.tableAlias = tableAlias;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the asName
	 */
	public String getAsName() {
		return asName;
	}

	/**
	 * @param asName the asName to set
	 */
	public void setAsName(String asName) {
		this.asName = asName;
	}

	/**
	 * @return the uniqId
	 */
	public String getUniqId() {
		return uniqId;
	}

	/**
	 * @param uniqId the uniqId to set
	 */
	public void setUniqId(String uniqId) {
		this.uniqId = uniqId;
	}

	/**
	 * @return the foreignKey
	 */
	public ForeignKeySetting getForeignKey() {
		return foreignKey;
	}

	/**
	 * @param foreignKey the foreignKey to set
	 */
	public void setForeignKey(ForeignKeySetting foreignKey) {
		this.foreignKey = foreignKey;
	}
	
}
