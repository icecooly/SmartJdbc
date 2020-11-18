package io.itit.smartjdbc.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.enums.ConditionType;
import io.itit.smartjdbc.util.JSONUtil;

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
	public static EntityInfo getEntityInfo(Class<?> entityClass) {
		EntityInfo entity=entityInfoMap.get(entityClass);
		if(entity==null) {
			entity=EntityInfo.create(entityClass);
			entityInfoMap.put(entityClass,entity);
			if(logger.isDebugEnabled()) {
				logger.debug("createEntityInfo entityClass:{}",entity.info());
			}
		}
		return entity;
	}
	//
	//
	public static QueryInfo getQueryInfo(Class<?> clazz) {
		QueryInfo info=queryInfoMap.get(clazz);
		if(info==null) {
			info=new QueryInfo(null,null,clazz,ConditionType.AND);
			info=QueryInfo.create(info);
			queryInfoMap.put(clazz,info);
			if(logger.isDebugEnabled()) {
				logger.debug("createQueryInfo queryClass:{}\n{}",clazz,JSONUtil.toJson(info));
			}
		}
		return info;
	}
}
