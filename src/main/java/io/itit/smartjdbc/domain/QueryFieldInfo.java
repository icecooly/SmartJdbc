package io.itit.smartjdbc.domain;

import java.lang.reflect.Field;
import java.util.List;

import io.itit.smartjdbc.annotations.InnerJoin;
import io.itit.smartjdbc.annotations.Joins;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.util.SmartJdbcUtils;

/**
 * 
 * @author skydu
 *
 */
public class QueryFieldInfo {

	private Field field;
	
	private Class<?> fieldType;
	
	private String fieldName;//fullName.field.getName()
	
	private InnerJoin join;
	
	private Joins joins;
	
	private QueryField queryField;
	
	private List<InnerJoin> joinsList;
	
	private String tableAlias;
	
	private Class<?> queryClass;
	
	private Class<?> entityClass;//可能是关联表
	//
	public static QueryFieldInfo create(Class<?> queryClass, String fullName, Field field) {
		QueryFieldInfo info=new QueryFieldInfo();
		info.field=field;
		field.setAccessible(true);
		info.fieldType=field.getType();
		info.queryClass=queryClass;
		if(SmartJdbcUtils.isEmpty(fullName)) {
			info.fieldName=field.getName();
		}else {
			info.fieldName=fullName+"."+field.getName();
		}
		info.queryField=field.getAnnotation(QueryField.class);
		info.join=field.getAnnotation(InnerJoin.class);
		info.joins=field.getAnnotation(Joins.class);
		return info;
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
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}
	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	/**
	 * @return the join
	 */
	public InnerJoin getJoin() {
		return join;
	}
	/**
	 * @param join the join to set
	 */
	public void setJoin(InnerJoin join) {
		this.join = join;
	}
	/**
	 * @return the joins
	 */
	public Joins getJoins() {
		return joins;
	}
	/**
	 * @param joins the joins to set
	 */
	public void setJoins(Joins joins) {
		this.joins = joins;
	}
	/**
	 * @return the queryField
	 */
	public QueryField getQueryField() {
		return queryField;
	}
	/**
	 * @param queryField the queryField to set
	 */
	public void setQueryField(QueryField queryField) {
		this.queryField = queryField;
	}
	/**
	 * @return the joinsList
	 */
	public List<InnerJoin> getJoinsList() {
		return joinsList;
	}
	/**
	 * @param joinsList the joinsList to set
	 */
	public void setJoinsList(List<InnerJoin> joinsList) {
		this.joinsList = joinsList;
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
	 * @return the queryClass
	 */
	public Class<?> getQueryClass() {
		return queryClass;
	}
	/**
	 * @param queryClass the queryClass to set
	 */
	public void setQueryClass(Class<?> queryClass) {
		this.queryClass = queryClass;
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
