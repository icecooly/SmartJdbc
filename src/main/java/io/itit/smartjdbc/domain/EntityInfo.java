package io.itit.smartjdbc.domain;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.itit.smartjdbc.annotations.EntityField;
import io.itit.smartjdbc.annotations.ForeignKey;
import io.itit.smartjdbc.annotations.LeftJoin;
import io.itit.smartjdbc.enums.JoinType;
import io.itit.smartjdbc.provider.SqlProvider;
import io.itit.smartjdbc.provider.entity.Join;
import io.itit.smartjdbc.provider.entity.Joins;
import io.itit.smartjdbc.util.ClassUtils;
import io.itit.smartjdbc.util.SmartJdbcUtils;

/**
 * 
 * @author skydu
 *
 */
public class EntityInfo {

	private Class<?> entityClass;
	
	private List<EntityFieldInfo> fieldList;//=ClassUtils.getFieldList(entityClass);
	
	private Map<String,EntityFieldInfo> fieldMap;
	
	private Joins joins;//left join tableName alias on 
	
	//
	public EntityInfo() {
		joins=new Joins();
		fieldList=new ArrayList<>();
		fieldMap=new HashMap<>();
	}
	//
	public static EntityInfo create(Class<?> entityClass) {
		EntityInfo info=new EntityInfo();
		info.entityClass=entityClass;
		List<Field> fields=ClassUtils.getFieldList(entityClass);
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()) || 
					Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			EntityFieldInfo fieldInfo=createEntityFieldInfo(field);
			info.fieldList.add(fieldInfo);
			info.fieldMap.put(field.getName(), fieldInfo);
		}
		info.addJoins();
		//
		return info;
	}
	//
	private static EntityFieldInfo createEntityFieldInfo(Field field) {
		EntityFieldInfo fieldInfo=new EntityFieldInfo();
		fieldInfo.setField(field);
		fieldInfo.setName(field.getName());
		field.setAccessible(true);
		EntityField entityField=field.getAnnotation(EntityField.class);
		fieldInfo.setEntityField(entityField);
		fieldInfo.setLeftJoin(field.getAnnotation(LeftJoin.class));
		fieldInfo.setFieldType(field.getType());
		if(entityField!=null) {
			fieldInfo.setDistinct(entityField.distinct());
			fieldInfo.setStatFunction(entityField.statFunc());
			if(!SmartJdbcUtils.isEmpty(entityField.field())&&!entityField.field().equals(field.getName())) {
				fieldInfo.setName(entityField.field());
				fieldInfo.setAsName(field.getName());
			}
		}
		return fieldInfo;
	}
	//
	private void addJoins() {
		for (EntityFieldInfo fieldInfo : fieldList) {
			EntityField entityField = fieldInfo.getEntityField();
			LeftJoin leftJoin=fieldInfo.getLeftJoin();
			if(leftJoin!=null) {
				Join join=addJoin(JoinType.LEFT_JOIN,SqlProvider.MAIN_TABLE_ALIAS,
						entityClass,leftJoin.table2(),null,null,
						leftJoin.table1Fields(),leftJoin.table2Fields());
				fieldInfo.setTableAlias(join.getTable2Alias());
				fieldInfo.setEntityClass(join.getTable2());
			}else if(entityField!=null&&!SmartJdbcUtils.isEmpty(entityField.foreignKeyFields())) {
				String foreignKeyId = entityField.foreignKeyFields();
				String[] foreignKeyIds=foreignKeyId.split(",");
				Class<?> table1=entityClass;
				String table1Alias=SqlProvider.MAIN_TABLE_ALIAS;
				Join join=null;
				for (String id : foreignKeyIds) {
					Field foreignKeyField=null;
					try {
						foreignKeyField=ClassUtils.getExistedField(table1,id);
					} catch (Exception e) {
						throw new IllegalArgumentException(e.getMessage()+"/"+table1.getSimpleName());
					}
					ForeignKey foreignKey=foreignKeyField.getAnnotation(ForeignKey.class);
					if(foreignKey==null) {
						throw new IllegalArgumentException("@ForeignKey not found in "+
									entityClass.getSimpleName()+"."+foreignKeyField.getName());
					}
					Class<?> table2=foreignKey.entityClass();
					join=addJoin(JoinType.LEFT_JOIN,table1Alias,table1, table2,null,null,
							new String[] {id},
							new String[]{ClassUtils.getSinglePrimaryKey(table2)});
					table1=table2;
					table1Alias=join.getTable2Alias();
				}
				fieldInfo.setTableAlias(join.getTable2Alias());
				fieldInfo.setEntityClass(join.getTable2());
			}else {
				fieldInfo.setTableAlias(SqlProvider.MAIN_TABLE_ALIAS);//默认主表
				fieldInfo.setEntityClass(entityClass);
			}
		}
	}
	//
	protected Join addJoin(JoinType type,String table1Alias,Class<?> table1,Class<?> table2,
			String table1Name, String table2Name,
			String[] table1Fields, String[] table2Fields) {
		return joins.addJoin(type, table1, table2, table1Name, table2Name,
				table1Alias, null, table1Fields, table2Fields);
	}
	//
	public EntityFieldInfo getField(String fieldName) {
		return fieldMap.get(fieldName);
	}
	//
	public String info() {
		StringBuilder info=new StringBuilder();
		info.append("\nEntityInfo[\n").append(entityClass.getName());
		for (EntityFieldInfo field : fieldList) {
			info.append(field.info());
		}
		info.append(joins.info());
		info.append("\n]");
		return info.toString();
	}
	//
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
	/**
	 * @return the fieldList
	 */
	public List<EntityFieldInfo> getFieldList() {
		return fieldList;
	}
	/**
	 * @param fieldList the fieldList to set
	 */
	public void setFieldList(List<EntityFieldInfo> fieldList) {
		this.fieldList = fieldList;
	}
	/**
	 * @return the fieldMap
	 */
	public Map<String, EntityFieldInfo> getFieldMap() {
		return fieldMap;
	}
	/**
	 * @param fieldMap the fieldMap to set
	 */
	public void setFieldMap(Map<String, EntityFieldInfo> fieldMap) {
		this.fieldMap = fieldMap;
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
	
	
}
