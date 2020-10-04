//package io.itit.smartjdbc.provider;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Modifier;
//import java.lang.reflect.ParameterizedType;
//import java.util.LinkedHashMap;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import io.itit.smartjdbc.SmartDataSource;
//import io.itit.smartjdbc.annotations.ForeignKey;
//import io.itit.smartjdbc.annotations.LeftJoin;
//import io.itit.smartjdbc.cache.Caches;
//import io.itit.smartjdbc.cache.EntityFieldInfo;
//import io.itit.smartjdbc.cache.EntityInfo;
//import io.itit.smartjdbc.provider.entity.Entity;
//import io.itit.smartjdbc.provider.entity.EntityField;
//import io.itit.smartjdbc.provider.entity.EntityField.EntityFieldImpl;
//import io.itit.smartjdbc.provider.entity.Join;
//import io.itit.smartjdbc.util.ClassUtils;
//import io.itit.smartjdbc.util.StringUtil;
//
///**
// * 
// * @author skydu
// *
// */
//public class EntitySerivce{
//	//
//	private static Logger logger=LoggerFactory.getLogger(EntitySerivce.class);
//	//
//	private SmartDataSource smartDataSource;
//	private Class<?> entityClass;
//	private Entity entity;
//	private Set<String> includeFields;
//	private Set<String> excludeFields;//userName not user_name
//	//
//	public EntitySerivce(SmartDataSource smartDataSource) {
//		this.smartDataSource=smartDataSource;
//		this.includeFields=new LinkedHashSet<>();
//		this.excludeFields=new LinkedHashSet<>();
//	}
//	//
//	public String getTableName(Class<?> entityClass) {
//		return smartDataSource.getTableName(entityClass);
//	}
//	//
//	public Entity buildEntity(Class<?> entityClass) {
//		this.entityClass=entityClass;
//		this.entity=new Entity(getTableName(entityClass), null);
//		
//		return entity;
//	}
//	//
//	protected void buildSelectFields(){
//		int index=1;
//		EntityInfo info=Caches.getEntityInfo(entityClass);
//		for (EntityFieldInfo fieldInfo : info.fieldList) {
//			Field field=fieldInfo.field;
//			if (Modifier.isStatic(field.getModifiers())|| Modifier.isFinal(field.getModifiers())) {
//				continue;
//			}
//			if(includeFields!=null&&!includeFields.isEmpty()&&(!includeFields.contains(field.getName()))){
//				continue;
//			}
//			if(excludeFields.contains(field.getName())){
//				continue;
//			}	
//			io.itit.smartjdbc.annotations.EntityField entityField = fieldInfo.entityField;
//			if(entityField!=null&&entityField.ignoreWhenSelect()) {
//				continue;
//			}
//			if(entityField==null) {
//				select(SqlProvider.MAIN_TABLE_ALIAS, field.getName());
//				continue;
//			}
//			String reallyName=field.getName();
//			if(!StringUtil.isEmpty(entityField.field())) {
//				reallyName=entityField.field();
//			}
//			//
//			if(!StringUtil.isEmpty(entityField.foreignKeyFields())) {
//				String foreignKeyId = entityField.foreignKeyFields();
//				String[] foreignKeyIds=foreignKeyId.split(",");
//				Class<?> table1=entityClass;
//				String table1Alias=MAIN_TABLE_ALIAS;
//				Join join=null;
//				for (String id : foreignKeyIds) {
//					Field foreignKeyField=null;
//					try {
//						foreignKeyField=ClassUtils.getExistedField(table1,id);
//					} catch (Exception e) {
//						logger.error(e.getMessage(),e);
//						throw new IllegalArgumentException(e.getMessage()+"/"+table1.getSimpleName());
//					}
//					ForeignKey foreignKey=foreignKeyField.getAnnotation(ForeignKey.class);
//					if(foreignKey==null) {
//						throw new IllegalArgumentException("@ForeignKey not found in "+
//									entityClass.getSimpleName()+"."+foreignKeyField.getName());
//					}
//					Class<?> table2=foreignKey.entityClass();
//					String key=id;
//					if(join==null) {
//						join = map.get(key);
//						if(join==null) {
//							join=createLeftJoin(key,table1Alias,"l"+(index++),table1, table2,new String[] {id});
//							map.put(key, join);
//						}
//					}else {
//						Join childJoin=getJoin(key, join.joins);
//						if(childJoin==null) {
//							childJoin=createLeftJoin(key,table1Alias,"l"+(index++),table1,table2,new String[] {id});
//							join.joins.add(childJoin);
//						}
//						join=childJoin;
//					}
//					table1=table2;
//					table1Alias=join.table2Alias;
//				}
//				if(WRAP_TYPES.contains(field.getType())){
//					addSelect(join.table2Alias, field, entityField);
//				}else if(field.getGenericType() instanceof ParameterizedType){
//					addSelect(join.table2Alias, field, entityField);
//				}else {
//					List<Field> subClassFields=getPersistentFields((Class<?>)field.getGenericType());
//					for (Field subClassField : subClassFields) {
//						select(join.table2Alias,subClassField.getName(),field.getName()+"_",
//								subClassField.getName(),distinct,statFunc);
//					}
//				}
//			}else {
//				addSelect(MAIN_TABLE_ALIAS, field, entityField);
//				continue;
//			}
//		}
//	}
//	//
//	public EntitySerivce select(String field) {
//		return select(null, field);
//	}
//	//
//	public EntitySerivce select(String tableAlias,String field) {
//		return select(tableAlias,field,null);
//	}
//	//
//	public EntitySerivce select(String tableAlias,String field,String asField) {
//		entity.fields.add(createSelectField(tableAlias, field, asField));
//		return this;
//	}
//	//
//	protected EntityField createSelectField(String tableAlias,String field ,String asField) {
//		EntityFieldImpl sf=new EntityFieldImpl(tableAlias, field, asField, null);
//		return sf;
//	}
//}
