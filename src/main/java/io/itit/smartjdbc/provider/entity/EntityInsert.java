package io.itit.smartjdbc.provider.entity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.annotations.EntityField;
import io.itit.smartjdbc.enums.ColumnType;
import io.itit.smartjdbc.util.ClassUtils;
import io.itit.smartjdbc.util.SmartJdbcUtils;

/**
 * 
 * @author skydu
 *
 */
public class EntityInsert extends Entity{
	//
	private boolean withGenerateKey;
	private List<EntityInsertField> fieldList;
	//
	public EntityInsert(String tableName) {
		super(tableName);
		this.fieldList=new ArrayList<>();
	}
	//
	public static class EntityInsertField{
		private String column;
		private Object value;
		private ColumnType columnType;
		//
		public EntityInsertField(ColumnType columnType, String column,Object value) {
			this.columnType=columnType;
			this.column=column;
			this.value=value;
		}
		//
		/**
		 * @return the column
		 */
		public String getColumn() {
			return column;
		}
		/**
		 * @param column the column to set
		 */
		public void setColumn(String column) {
			this.column = column;
		}
		/**
		 * @return the value
		 */
		public Object getValue() {
			return value;
		}
		/**
		 * @param value the value to set
		 */
		public void setValue(Object value) {
			this.value = value;
		}
		/**
		 * @return the columnType
		 */
		public ColumnType getColumnType() {
			return columnType;
		}
		/**
		 * @param columnType the columnType to set
		 */
		public void setColumnType(ColumnType columnType) {
			this.columnType = columnType;
		}
		
	}
	//
	public static EntityInsert create(SmartDataSource datasource, Object bean, boolean withGenerateKey, String... excludeFields) {
		EntityInsert insert=new EntityInsert(datasource.getTableName(bean.getClass()));
		insert.withGenerateKey=withGenerateKey;
		Set<String> excludesNames = new TreeSet<String>();
		if(excludeFields!=null) {
			for (String e : excludeFields) {
				excludesNames.add(e);
			}
		}
		Class<?> entityClass=bean.getClass();
		List<Field> list=ClassUtils.getPersistentFields(entityClass);
		for (Field f : list) {
			if (excludesNames.contains(f.getName())) {
				continue;
			}
			if(!ClassUtils.isPersistentField(f)) {
				continue;
			}
			EntityField entityField=f.getAnnotation(EntityField.class);
			if(entityField!=null&&entityField.autoIncrement()) {
				continue;
			}
			if(entityField!=null&&!SmartJdbcUtils.isEmptyWithTrim(entityField.foreignKeyFields())) {
				continue;
			}
			ColumnType columnType=null;
			if(entityField!=null) {
				columnType=entityField.columnType();
			}
			String column = datasource.convertFieldNameToColumnName(entityClass, f.getName());
			try {
				f.setAccessible(true);
				Object fieldValue=f.get(bean);
				insert.fieldList.add(new EntityInsertField(columnType, column, fieldValue));
			} catch (Exception e) {
				throw new SmartJdbcException(e);
			}
		}
		return insert;
	}
	//
	/**
	 * @return the withGenerateKey
	 */
	public boolean isWithGenerateKey() {
		return withGenerateKey;
	}
	/**
	 * @param withGenerateKey the withGenerateKey to set
	 */
	public void setWithGenerateKey(boolean withGenerateKey) {
		this.withGenerateKey = withGenerateKey;
	}
	/**
	 * @return the fieldList
	 */
	public List<EntityInsertField> getFieldList() {
		return fieldList;
	}
	/**
	 * @param fieldList the fieldList to set
	 */
	public void setFieldList(List<EntityInsertField> fieldList) {
		this.fieldList = fieldList;
	}
	
}
