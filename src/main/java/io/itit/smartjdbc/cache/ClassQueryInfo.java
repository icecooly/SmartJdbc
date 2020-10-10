//package io.itit.smartjdbc.cache;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import io.itit.smartjdbc.Query;
//import io.itit.smartjdbc.annotations.ForeignKey;
//import io.itit.smartjdbc.annotations.InnerJoin;
//import io.itit.smartjdbc.annotations.InnerJoins;
//import io.itit.smartjdbc.annotations.QueryField;
//import io.itit.smartjdbc.enums.ConditionType;
//import io.itit.smartjdbc.provider.entity.Join;
//import io.itit.smartjdbc.util.ClassUtils;
//import io.itit.smartjdbc.util.StringUtil;
//
///**
// * 
// * @author skydu
// *
// */
//public class ClassQueryInfo extends QueryInfo {
//	//
//	private static Logger logger=LoggerFactory.getLogger(ClassQueryInfo.class);
//
//	public Class<?> clazz;
//
//	public Field field;
//
//	public ClassQueryInfo(String parentFullName, Field field, Class<?> clazz, ConditionType conditionType) {
//		super(parentFullName, conditionType);
//		this.clazz=clazz;
//		this.field=field;
//		if(field!=null) {
//			fullName+=field.getName();
//		}
//	}
//	//
//	protected String getInnerJoinKey(InnerJoin innerJoin) {
//		StringBuilder key = new StringBuilder();
//		for (String table1Field : innerJoin.table1Fields()) {
//			key.append(table1Field).append("-");
//		}
//		key.append(innerJoin.table2().getName()).append("-");
//		for (String table2Field : innerJoin.table2Fields()) {
//			key.append(table2Field).append("-");
//		}
//		key.deleteCharAt(key.length() - 1);
//		return key.toString();
//	}
//
//	//
//	protected Map<String, Join> getInnerJoins(Query<?> query) {
//		Map<String, Join> map = new LinkedHashMap<>();
//		if (query == null) {
//			return map;
//		}
//		List<QueryFieldInfo> fieldInfos = new ArrayList<>();
//		QueryInfo info = Caches.getQueryInfo(query.getClass());
//		getQueryFields(fieldInfos, query, info);
//		int index = 1;
//		innerJoinFieldAliasMap = new HashMap<>();
//		for (QueryFieldInfo fieldInfo : fieldInfos) {
//			InnerJoin innerJoin = fieldInfo.innerJoin;
//			InnerJoins innerJoins = fieldInfo.innerJoins;
//			QueryField queryField = fieldInfo.queryField;
//			String foreignKeyFields = "";
//			if (queryField != null) {
//				foreignKeyFields = queryField.foreignKeyFields();
//			}
//			if (innerJoin == null && innerJoins == null && StringUtil.isEmpty(foreignKeyFields)) {
//				continue;
//			}
//			List<InnerJoin> innerJoinsList = new ArrayList<>();
//			if (isValidInnerJoin(innerJoin)) {
//				innerJoinsList.add(innerJoin);
//			}
//			if (innerJoins != null && innerJoins.joins() != null) {
//				for (InnerJoin join : innerJoins.joins()) {
//					if (isValidInnerJoin(join)) {
//						innerJoinsList.add(join);
//					}
//				}
//			}
//			if (innerJoinsList.size() > 0) {// use annotation
//				Join join = null;
//				String table1 = getTableName(entityClass);
//				String table1Alias = MAIN_TABLE_ALIAS;
//				for (InnerJoin j : innerJoinsList) {
//					String key = getInnerJoinKey(j);
//					if (join == null) {
//						join = map.get(key);
//						if (join == null) {
//							String table2Alias = StringUtil.isEmpty(j.table2Alias()) ? "i" + (index++)
//									: j.table2Alias();
//							join = createInnerJoin(key, table1Alias, table2Alias, table1, getTableName(j.table2()),
//									j.table1Fields(), j.table2Fields());
//							map.put(key, join);
//						}
//					} else {
//						Join childJoin = getJoin(key, join.joins);
//						if (childJoin == null) {
//							String table2Alias = StringUtil.isEmpty(j.table2Alias()) ? "i" + (index++)
//									: j.table2Alias();
//							childJoin = createInnerJoin(key, table1Alias, table2Alias, table1, getTableName(j.table2()),
//									j.table1Fields(), j.table2Fields());
//							join.joins.add(childJoin);
//						}
//						join = childJoin;
//					}
//					table1 = join.table2;
//					table1Alias = join.table2Alias;
//				}
//				innerJoinFieldAliasMap.put(fieldInfo.fieldName, table1Alias);
//			} else if (!StringUtil.isEmpty(foreignKeyFields)) {
//				String[] foreignKeyIds = foreignKeyFields.split(",");
//				String table1 = getTableName(entityClass);
//				String table1Alias = MAIN_TABLE_ALIAS;
//				Join join = null;
//				for (String id : foreignKeyIds) {
//					Field foreignKeyField = null;
//					try {
//						foreignKeyField = ClassUtils.getExistedField(table1, id);
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//						throw new IllegalArgumentException(e.getMessage() + "/" + table1.getSimpleName());
//					}
//					ForeignKey foreignKey = foreignKeyField.getAnnotation(ForeignKey.class);
//					if (foreignKey == null) {
//						throw new IllegalArgumentException(
//								"@ForeignKey not found in " + table1.getSimpleName() + "." + foreignKeyField.getName());
//					}
//					String table2 = getTableName(foreignKey.entityClass());
//					String table2Field = foreignKey.field();
//					String key = id + "-" + table2 + "-" + table2Field;
//					if (join == null) {
//						join = map.get(key);
//						if (join == null) {
//							join = createInnerJoin(key, table1Alias, "i" + (index++), table1, table2,
//									new String[] { id }, new String[] { table2Field });
//							map.put(key, join);
//						}
//					} else {
//						Join childJoin = getJoin(key, join.joins);
//						if (childJoin == null) {
//							childJoin = createInnerJoin(key, table1Alias, "i" + (index++), table1, table2,
//									new String[] { id }, new String[] { table2Field });
//							join.joins.add(childJoin);
//						}
//						join = childJoin;
//					}
//					table1 = table2;
//					table1Alias = join.table2Alias;
//				}
//				innerJoinFieldAliasMap.put(fieldInfo.fieldName, table1Alias);
//			} else {
//				continue;
//			}
//		}
//		return map;
//	}
//	
//	//
//	protected void getQueryFields(List<QueryFieldInfo> fieldList,Object obj,QueryInfo queryInfo){
//		for (QueryFieldInfo fieldInfo : queryInfo.fieldList) {
//			try {
//				Field field=fieldInfo.field;
//				if(!field.isAccessible()) {
//					field.setAccessible(true);
//				}
//				Object reallyValue = field.get(obj);
//				if (reallyValue == null) {
//					continue;
//				}
//				fieldList.add(fieldInfo);
//			}catch (Exception e) {
//				logger.error(e.getMessage(),e);
//				throw new IllegalArgumentException(e);
//			}
//		}
//		for (QueryInfo e : queryInfo.children) {
//			try {
//				if(!e.field.isAccessible()) {
//					e.field.setAccessible(true);
//				}
//				Object reallyValue = e.field.get(obj);
//				if (reallyValue == null) {
//					continue;
//				}
//				getQueryFields(fieldList,reallyValue,e);
//			} catch (Exception ex) {
//				logger.error(ex.getMessage(),ex);
//				throw new IllegalArgumentException(ex);
//			}	
//		}
//	}
//
//	//
//	protected Join createInnerJoin(String key, String table1Alias, String table2Alias, String table1, String table2,
//			String[] table1Fields, String[] table2Fields) {
//		Join join = new Join();
//		join.key = key;
//		join.table1Alias = table1Alias;
//		join.table2Alias = table2Alias;
//		join.table1Fields = table1Fields;
//		join.table2Fields = table2Fields;
//		join.table1 = table1;
//		join.table2 = table2;
//		this.innerJoins.add(join);
//		return join;
//	}
//}
