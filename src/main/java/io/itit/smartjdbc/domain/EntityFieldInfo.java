package io.itit.smartjdbc.domain;

import java.lang.reflect.Field;

import io.itit.smartjdbc.annotations.EntityField;
import io.itit.smartjdbc.annotations.LeftJoin;

/**
 * 
 * @author skydu
 *
 */
public class EntityFieldInfo {

	private Field field;
	
	private String name;//不一定等于field.getName()
	
	private EntityField entityField;
	
	private LeftJoin leftJoin;
	
	private String tableAlias;
	
	private String asName;
	
	private boolean distinct;
	
	private String statFunction;
	
	private Class<?> fieldType;
	
	private Class<?> entityClass;
	
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
	 * @return the field
	 */
	public Field getField() {
		return field;
	}

	/**
	 * @param field the field to set
	 */
	public void setField(Field field) {
		this.field = field;
	}

	/**
	 * @return the entityField
	 */
	public EntityField getEntityField() {
		return entityField;
	}

	/**
	 * @param entityField the entityField to set
	 */
	public void setEntityField(EntityField entityField) {
		this.entityField = entityField;
	}

	/**
	 * @return the leftJoin
	 */
	public LeftJoin getLeftJoin() {
		return leftJoin;
	}

	/**
	 * @param leftJoin the leftJoin to set
	 */
	public void setLeftJoin(LeftJoin leftJoin) {
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
	 * @return the distinct
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * @param distinct the distinct to set
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * @return the statFunction
	 */
	public String getStatFunction() {
		return statFunction;
	}

	/**
	 * @param statFunction the statFunction to set
	 */
	public void setStatFunction(String statFunction) {
		this.statFunction = statFunction;
	}

	/**
	 * @return the fieldType
	 */
	public Class<?> getFieldType() {
		return fieldType;
	}

	/**
	 * @param fieldType the fieldType to set
	 */
	public void setFieldType(Class<?> fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * @return the entityClass
	 */
	public Class<?> getEntityClass() {
		return entityClass;
	}

	/**
	 * @param entityClass the entityClass to set
	 */
	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}
	
}
