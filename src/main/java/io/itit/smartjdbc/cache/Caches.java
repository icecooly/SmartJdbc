package io.itit.smartjdbc.cache;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.annotations.EntityField;
import io.itit.smartjdbc.annotations.InnerJoin;
import io.itit.smartjdbc.annotations.InnerJoins;
import io.itit.smartjdbc.annotations.LeftJoin;
import io.itit.smartjdbc.annotations.QueryConditionType;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.enums.ConditionType;
import io.itit.smartjdbc.util.ClassUtils;
import io.itit.smartjdbc.util.DumpUtil;

/**
 * 
 * @author skydu
 *
 */
public class Caches {
	//
	private static Logger logger=LoggerFactory.getLogger(Caches.class);
	//
	private static Map<Class<?>,EntityInfo> entityInfoMap=new ConcurrentHashMap<>();
	//
	private static Map<Class<?>,QueryInfo> queryInfoMap=new ConcurrentHashMap<>();
	//
	public static EntityInfo getEntityInfo(Class<?> clazz) {
		EntityInfo info=entityInfoMap.get(clazz);
		if(info==null) {
			info=createEntityInfo(clazz);
		}
		return info;
	}
	//
	private static EntityInfo createEntityInfo(Class<?> entityClass) {
		EntityInfo info=new EntityInfo();
		info.clazz=entityClass;
		List<Field> fields=ClassUtils.getFieldList(entityClass);
		List<EntityFieldInfo> fieldList=new ArrayList<>();
		Map<String,Field> fieldMap=new HashMap<>();
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()) || 
					Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			EntityFieldInfo fieldInfo=new EntityFieldInfo();
			fieldInfo.field=field;
			fieldInfo.entityField=field.getAnnotation(EntityField.class);
			fieldInfo.leftJoin=field.getAnnotation(LeftJoin.class);
			fieldList.add(fieldInfo);
			fieldMap.put(field.getName(), field);
		}
		info.fieldList=fieldList;
		info.fieldMap=fieldMap;
		entityInfoMap.put(entityClass,info);
		if(logger.isDebugEnabled()) {
			logger.debug("createEntityInfo entityClass:{}",entityClass);
		}
		return info;
	}
	//
	public static QueryInfo getQueryInfo(Class<?> clazz) {
		QueryInfo info=queryInfoMap.get(clazz);
		if(info==null) {
			info=new QueryInfo(clazz,ConditionType.AND);
			info=createQueryInfo(info);
			queryInfoMap.put(clazz,info);
			if(logger.isDebugEnabled()) {
				logger.debug("createQueryInfo queryClass:{}\n{}",clazz,DumpUtil.dump(info));
			}
		}
		return info;
	}
	//
	private static QueryInfo createQueryInfo(QueryInfo info) {
		List<Field> fields=ClassUtils.getFieldList(info.clazz);
		List<QueryFieldInfo> fieldList=new ArrayList<>();
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()) || 
					Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			QueryConditionType fConditionType = field.getAnnotation(QueryConditionType.class);
			if (fConditionType!=null) {
				QueryInfo child=new QueryInfo(field.getType(),fConditionType.value());
				child.field=field;
				info.children.add(child);
				createQueryInfo(child);
				continue;
			}
			QueryField queryField = field.getAnnotation(QueryField.class);
			if (queryField== null) {
				continue;
			}
			QueryFieldInfo fieldInfo=new QueryFieldInfo();
			fieldInfo.field=field;
			fieldInfo.fieldType=field.getType();
			fieldInfo.fieldName=field.getName();
			fieldInfo.queryField=field.getAnnotation(QueryField.class);
			fieldInfo.innerJoin=field.getAnnotation(InnerJoin.class);
			fieldInfo.innerJoins=field.getAnnotation(InnerJoins.class);
			fieldList.add(fieldInfo);
		}
		info.fieldList=fieldList;
		return info;
	}
}
