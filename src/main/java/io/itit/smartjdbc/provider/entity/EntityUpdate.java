package io.itit.smartjdbc.provider.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.annotations.EntityField;
import io.itit.smartjdbc.annotations.Version;
import io.itit.smartjdbc.enums.ColumnType;
import io.itit.smartjdbc.provider.where.QueryWhere;
import io.itit.smartjdbc.util.ClassUtils;
import io.itit.smartjdbc.util.SmartJdbcUtils;

/**
 * 
 * @author skydu
 *
 */
public class EntityUpdate extends Entity{
	//
	private QueryWhere queryWhere;
	private List<EntityUpdateField> fieldList;
	private boolean excludeNull;
	private VersionField versionField;
	//
	public EntityUpdate(String tableName) {
		super(tableName);
		fieldList=new ArrayList<>();
	}
	//
	public EntityUpdate excludeNull(boolean excludeNull) {
		this.excludeNull=excludeNull;
		return this;
	}
	//
	public EntityUpdate queryWhere(QueryWhere queryWhere) {
		this.queryWhere=queryWhere;
		return this;
	}
	//
	public static class VersionField{
		private Field field;
		private String column;
		private long value;
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
		public long getValue() {
			return value;
		}
		/**
		 * @param value the value to set
		 */
		public void setValue(long value) {
			this.value = value;
		}
		
	}
	//
	public static class EntityUpdateField{
		private String column;
		private Object value;
		private ColumnType columnType;
		//
		public EntityUpdateField(ColumnType columnType, String column,Object value) {
			this.columnType=columnType;
			this.column=column;
			this.value=value;
		}
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
	
	/**
	 * 
	 * @param datasource
	 * @param bean
	 * @param excludeNull
	 * @param includeFields
	 * @param queryWhere 可以为空
	 * @param excludeFields
	 * @return
	 */
	public static EntityUpdate create(SmartDataSource datasource, Object bean,
			boolean excludeNull, Set<String> includeFields, 
			QueryWhere queryWhere, String... excludeFields) {
		Set<String> excludesNames = new TreeSet<String>();
		for (String e : excludeFields) {
			excludesNames.add(e);
		}
		return create(datasource, bean, excludeNull, 
				includeFields, queryWhere, excludesNames);
	}
	//
	@SuppressWarnings("deprecation")
	public static EntityUpdate create(SmartDataSource datasource, Object bean,
			boolean excludeNull, Set<String> includeFields, 
			QueryWhere queryWhere, Set<String> excludeFields) {
		EntityUpdate update=new EntityUpdate(datasource.getTableName(bean.getClass()));
		update.queryWhere=queryWhere;
		update.excludeNull=excludeNull;
		Class<?> entityClass=bean.getClass();
		VersionField versionField=null;
		List<Field> fields=ClassUtils.getFieldList(entityClass);
		for (Field f : fields) {
			try {
				if (Modifier.isStatic(f.getModifiers())) {
					continue;
				}
				if(!ClassUtils.isPersistentField(f)) {
					continue;
				}
				Version version=f.getAnnotation(Version.class);
				if(version!=null) {
					versionField=new VersionField();
					versionField.column=datasource.convertFieldNameToColumnName(entityClass, f.getName());
					versionField.field=f;
					if(f.getType().equals(long.class)) {
						versionField.value=f.getLong(bean);
					}
					if(f.getType().equals(int.class)) {
						versionField.value=f.getInt(bean);
					}
					update.versionField=versionField;
					continue;
				}
				if(includeFields!=null&&!includeFields.isEmpty()&&(!includeFields.contains(f.getName()))){
					continue;
				}
				if (excludeFields!=null&&excludeFields.contains(f.getName())) {
					continue;
				}
				EntityField entityField=f.getAnnotation(EntityField.class);
				if(entityField!=null&&entityField.autoIncrement()) {
					continue;
				}
				if(entityField!=null&&!SmartJdbcUtils.isEmptyWithTrim(entityField.foreignKeyFields())) {
					continue;
				}
				String column = datasource.convertFieldNameToColumnName(entityClass, f.getName());
				ColumnType columnType=null;
				if(entityField!=null) {
					columnType=entityField.columnType();
				}
				if(!f.isAccessible()) {
					f.setAccessible(true);
				}
				Object fieldValue=f.get(bean);
				update.fieldList.add(new EntityUpdateField(columnType, column, fieldValue));
				//
				if(update.queryWhere==null) {//默认where主键
					update.queryWhere=QueryWhere.create();
					List<Field> primaryKey=ClassUtils.getPrimaryKey(bean.getClass());
					for (Field field : primaryKey) {
						if(!field.isAccessible()) {
							field.setAccessible(true);
						}
						update.queryWhere.where(datasource.convertFieldNameToColumnName(bean.getClass(),field.getName()),field.get(bean));
					}
				}
			} catch (Exception e) {
				throw new SmartJdbcException(e);
			}
		}
		//
		return update;
	}
	//
	/**
	 * @return the queryWhere
	 */
	public QueryWhere getQueryWhere() {
		return queryWhere;
	}
	/**
	 * @param queryWhere the queryWhere to set
	 */
	public void setQueryWhere(QueryWhere queryWhere) {
		this.queryWhere = queryWhere;
	}
	/**
	 * @return the fieldList
	 */
	public List<EntityUpdateField> getFieldList() {
		return fieldList;
	}
	/**
	 * @param fieldList the fieldList to set
	 */
	public void setFieldList(List<EntityUpdateField> fieldList) {
		this.fieldList = fieldList;
	}
	/**
	 * @return the excludeNull
	 */
	public boolean isExcludeNull() {
		return excludeNull;
	}
	/**
	 * @param excludeNull the excludeNull to set
	 */
	public void setExcludeNull(boolean excludeNull) {
		this.excludeNull = excludeNull;
	}
	/**
	 * @return the versionField
	 */
	public VersionField getVersionField() {
		return versionField;
	}
	/**
	 * @param versionField the versionField to set
	 */
	public void setVersionField(VersionField versionField) {
		this.versionField = versionField;
	}
	
}
