package io.itit.smartjdbc.domain;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.annotations.ForeignKey;
import io.itit.smartjdbc.annotations.QueryConditionType;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.cache.CacheManager;
import io.itit.smartjdbc.enums.ConditionType;
import io.itit.smartjdbc.enums.JoinType;
import io.itit.smartjdbc.provider.SqlProvider;
import io.itit.smartjdbc.provider.entity.Join;
import io.itit.smartjdbc.provider.entity.Joins;
import io.itit.smartjdbc.util.ClassUtils;
import io.itit.smartjdbc.util.JSONUtil;
import io.itit.smartjdbc.util.SmartJdbcUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author skydu
 *
 */
@Slf4j
public class QueryInfo {
	
	private Class<?> queryClass;

	private Class<?> entityClass;
	
	private Field field;

	private String fullName;//parent.fullName.field.getName()
	
	private ConditionType conditionType;
	
	private List<QueryFieldInfo> fieldList;
	
	private List<QueryInfo> children;
	
	private Joins joins;//inner join tableName alias on 
	//
	//
	public QueryInfo(String parentFullName,Field field,Class<?> queryClass,Class<?> entityClass,ConditionType conditionType) {
		this.field=field;
		this.queryClass=queryClass;
		this.entityClass=entityClass;
		this.conditionType=conditionType;
		this.children=new ArrayList<>();
		StringBuilder fullName=new StringBuilder();
		if((!SmartJdbcUtils.isEmpty(parentFullName))) {
			fullName.append(parentFullName).append(".");
		}
		if(field!=null) {
			fullName.append(field.getName());
		}
		this.fullName=fullName.toString();
	}
	//
	private static Class<?> getEntityClass(Class<?> queryClass) {
		Type type=queryClass.getGenericSuperclass();
		if(type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) queryClass.getGenericSuperclass();
			return (Class<?>) pt.getActualTypeArguments()[0];
		}else {
			return null;
		}
	}
	//
	public static QueryInfo create(Class<?> queryClass) {
		Class<?> entityClass=getEntityClass(queryClass);
		QueryInfo info=new QueryInfo(null,null,queryClass,entityClass,ConditionType.AND);
		info=create0(info);
		return info;
	}
	//
	private static QueryInfo create0(QueryInfo info) {
		List<Field> fields=ClassUtils.getFieldList(info.queryClass);
		List<QueryFieldInfo> fieldList=new ArrayList<>();
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()) || 
					Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			QueryConditionType fConditionType = field.getAnnotation(QueryConditionType.class);
			if (fConditionType!=null) {
				QueryInfo child=new QueryInfo(info.fullName,field,field.getType(),info.entityClass,fConditionType.value());
				info.children.add(child);
				create0(child);
				continue;
			}
			QueryField queryField = field.getAnnotation(QueryField.class);
			if (queryField== null) {
				continue;
			}
			QueryFieldInfo fieldInfo=QueryFieldInfo.create(info.queryClass, info.fullName, field);
			fieldList.add(fieldInfo);
		}
		info.fieldList=fieldList;
		info.addJoins();
		return info;
	}
	//
	private void addJoins() {
		EntityInfo entityInfo=CacheManager.getEntityInfo(entityClass);
		joins=JSONUtil.clone(entityInfo.getJoins());//
		for (QueryFieldInfo field : fieldList) {
			io.itit.smartjdbc.annotations.InnerJoin innerJoin=field.getJoin();
			io.itit.smartjdbc.annotations.Joins innerJoins=field.getJoins();
			QueryField queryField=field.getQueryField();
			String foreignKeyFields="";
			if(queryField!=null) {
				foreignKeyFields=queryField.foreignKeyFields();
			}
			if(innerJoin==null&&innerJoins==null&&SmartJdbcUtils.isEmpty(foreignKeyFields)) {
				field.setTableAlias(SqlProvider.MAIN_TABLE_ALIAS);
				field.setEntityClass(entityClass);
				continue;
			}
			List<io.itit.smartjdbc.annotations.InnerJoin> innerJoinsList=new ArrayList<>();
			if(isValidJoin(innerJoin)) {
				innerJoinsList.add(innerJoin);
			}
			if(innerJoins!=null&&innerJoins.joins()!=null) {
				for (io.itit.smartjdbc.annotations.InnerJoin join : innerJoins.joins()) {
					if(isValidJoin(join)) {
						innerJoinsList.add(join);
					}
				}
			}
			if(innerJoinsList.size()>0) {//use annotation
				Join join=null;
				Class<?> table1=entityClass;
				String table1Alias=SqlProvider.MAIN_TABLE_ALIAS;
				for (io.itit.smartjdbc.annotations.InnerJoin j: innerJoinsList) {
					join=createJoin(joins,
							JoinType.INNER_JOIN,table1,j.table2(),table1Alias,j.table2Alias(),
							j.table1Fields(),j.table2Fields());
					table1=join.getTable2();
					table1Alias=join.getTable2Alias();
				}
				field.setTableAlias(table1Alias);
				field.setEntityClass(table1);
			}else if(!SmartJdbcUtils.isEmpty(foreignKeyFields)) {
				String[] foreignKeyIds=foreignKeyFields.split(",");
				Class<?> table1=entityClass;
				String table1Alias=SqlProvider.MAIN_TABLE_ALIAS;
				Join join=null;
				for (String id : foreignKeyIds) {
					Field foreignKeyField=null;
					try {
						foreignKeyField=ClassUtils.getExistedField(table1,id);
					} catch (Exception e) {
						log.error(e.getMessage(),e);
						throw new IllegalArgumentException(e.getMessage()+"/"+table1.getSimpleName());
					}
					ForeignKey foreignKey=foreignKeyField.getAnnotation(ForeignKey.class);
					if(foreignKey==null) {
						throw new IllegalArgumentException("@ForeignKey not found in "+
									entityClass.getSimpleName()+"."+foreignKeyField.getName());
					}
					Class<?> table2=foreignKey.entityClass();
					String table2Field=foreignKey.field();
					join=createJoin(joins,
							JoinType.LEFT_JOIN,
							table1, table2,
							table1Alias,null,
							new String[] {id},new String[] {table2Field});
					table1=table2;
					table1Alias=join.getTable2Alias();
				}
				field.setTableAlias(table1Alias);
				field.setEntityClass(table1);
			}
		}
	}
	//
	private Join createJoin(Joins joins, JoinType type, 
			Class<?> table1,Class<?> table2,
			String table1Alias,String table2Alias,
			String[] table1Fields,String[] table2Fields) {
		return joins.addJoin(type, table1, table2,null, null,
				table1Alias, table2Alias, table1Fields, table2Fields);
	}
	//
	private static boolean isValidJoin(io.itit.smartjdbc.annotations.InnerJoin join) {
		if(join==null) {
			return false;
		}
		if(join.table2().equals(void.class)) {
			throw new SmartJdbcException("Join table2 cannot be null");
		}
		if(join.table1Fields().length==0) {
			throw new SmartJdbcException("Join table1Fields cannot be null");
		}
		if(join.table2Fields().length==0) {
			throw new SmartJdbcException("Join table2Fields cannot be null");
		}
		if(join.table1Fields().length!=join.table2Fields().length) {
			throw new SmartJdbcException("Join table1Fields length not equal table2Fields length");
		}
		return true;
	}
	//
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
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}
	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	/**
	 * @return the conditionType
	 */
	public ConditionType getConditionType() {
		return conditionType;
	}
	/**
	 * @param conditionType the conditionType to set
	 */
	public void setConditionType(ConditionType conditionType) {
		this.conditionType = conditionType;
	}
	/**
	 * @return the fieldList
	 */
	public List<QueryFieldInfo> getFieldList() {
		return fieldList;
	}
	/**
	 * @param fieldList the fieldList to set
	 */
	public void setFieldList(List<QueryFieldInfo> fieldList) {
		this.fieldList = fieldList;
	}
	/**
	 * @return the children
	 */
	public List<QueryInfo> getChildren() {
		return children;
	}
	/**
	 * @param children the children to set
	 */
	public void setChildren(List<QueryInfo> children) {
		this.children = children;
	}
	/**
	 * @param entityClass the entityClass to set
	 */
	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
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
