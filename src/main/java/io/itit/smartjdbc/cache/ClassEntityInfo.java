//package io.itit.smartjdbc.cache;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Modifier;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import io.itit.smartjdbc.annotations.Entity;
//import io.itit.smartjdbc.annotations.EntityField;
//import io.itit.smartjdbc.annotations.ForeignKey;
//import io.itit.smartjdbc.annotations.LeftJoin;
//import io.itit.smartjdbc.provider.SqlProvider;
//import io.itit.smartjdbc.provider.entity.Join;
//import io.itit.smartjdbc.util.ClassUtils;
//import io.itit.smartjdbc.util.StringUtil;
//
///**
// * 
// * @author skydu
// *
// */
//public class ClassEntityInfo extends EntityInfo{
//	//
//
//	private static Logger logger=LoggerFactory.getLogger(ClassEntityInfo.class);
//
//	//
//	public Class<?> entityClass;
//	
//	public Entity entity;
//	//
//	//
//	public void build(){
//		//add left join
//		addLeftJoins();
//		for (EntityFieldInfo field : fieldList) {
//			if(includeFields!=null&&!includeFields.isEmpty()&&
//					field.name!=null&&(!includeFields.contains(field.name))){
//				continue;
//			}
//			selectFields.add(field);
//		}
//	}
//	//
//	public void addLeftJoins() {
//		int index=1;
//		for (EntityFieldInfo e : fieldList) {
//			ClassEntityFieldInfo fieldInfo=(ClassEntityFieldInfo)e;
//			Field field=fieldInfo.field;
//			if (Modifier.isStatic(field.getModifiers())|| Modifier.isFinal(field.getModifiers())) {
//				continue;
//			}
//			EntityField entityField = fieldInfo.entityField;
//			LeftJoin leftJoin=fieldInfo.leftJoin;
//			if(leftJoin!=null) {
//				Join join = new Join();
//				join.table1Alias=SqlProvider.MAIN_TABLE_ALIAS;
//				join.table2Alias="l"+(index++);
//				join.table1=entityClass.getSimpleName();
//				join.table2 = leftJoin.table2().getSimpleName();
//				join.table1Fields=leftJoin.table1Fields();
//				join.table2Fields=leftJoin.table2Fields();
//				addLeftJoin(join);
//				fieldInfo.tableAlias=join.table2Alias;
//			}else if(entityField!=null&&!StringUtil.isEmpty(entityField.foreignKeyFields())) {
//				String foreignKeyIds = entityField.foreignKeyFields();
//				String[] foreignKeyIdList=foreignKeyIds.split(",");
//				Class<?> table1=entityClass;
//				String table1Alias=SqlProvider.MAIN_TABLE_ALIAS;
//				Join join=null;
//				for (String foreignKeyId : foreignKeyIdList) {
//					Field foreignKeyField=null;
//					try {
//						foreignKeyField=ClassUtils.getExistedField(table1,foreignKeyId);
//					} catch (Exception ex) {
//						logger.error(ex.getMessage(),ex);
//						throw new IllegalArgumentException(ex.getMessage()+"/"+table1.getSimpleName());
//					}
//					ForeignKey foreignKey=foreignKeyField.getAnnotation(ForeignKey.class);
//					if(foreignKey==null) {
//						throw new IllegalArgumentException("@ForeignKey not found in "+
//								table1.getSimpleName()+"."+foreignKeyField.getName());
//					}
//					Class<?> table2=foreignKey.entityClass();
//					join = new Join();
//					join.table1Alias=table1Alias;
//					join.table2Alias="l"+(index++);
//					join.table1=table1.getSimpleName();
//					join.table2 = table2.getSimpleName();
//					join.table1Fields=new String[] {foreignKeyId};
//					join.table2Fields=new String[] {"id"};
//					addLeftJoin(join);
//					//
//					table1=table2;
//					table1Alias=join.table2Alias;
//				}//end for
//				fieldInfo.tableAlias=join.table2Alias;
//			}
//		}
//	}
//	//
//	public void addLeftJoin(Join join) {
//		for (Join j : leftJoins) {
//			if(join.equals(j)) {
//				continue;
//			}
//			leftJoins.add(join);
//		}
//	}
//}
