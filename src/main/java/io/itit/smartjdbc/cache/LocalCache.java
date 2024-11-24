package io.itit.smartjdbc.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.itit.smartjdbc.domain.EntityInfo;
import io.itit.smartjdbc.domain.QueryInfo;

/**
 * 
 * @author skydu
 *
 */
public class LocalCache{

	private Cache<Class<?>,EntityInfo> entityInfoCache;
	private Cache<Class<?>,QueryInfo> queryInfoCache;
	//
	public LocalCache(long maximumSize) {
		entityInfoCache = CacheBuilder.newBuilder()
			       .maximumSize(maximumSize)
			       .build();
		queryInfoCache= CacheBuilder.newBuilder()
			       .maximumSize(maximumSize)
			       .build();
	}
	
	public EntityInfo getEntityInfo(Class<?> entityClass) {
		EntityInfo info=entityInfoCache.getIfPresent(entityClass);
		if(info!=null) {
			return info;
		}
		info=EntityInfo.create(entityClass);
		entityInfoCache.put(entityClass, info);
		return info;
	}
	
	/**
	 * 
	 * @param queryClass
	 * @return
	 */
	public QueryInfo getQueryInfo(Class<?> queryClass) {
		QueryInfo info=queryInfoCache.getIfPresent(queryClass);
		if(info!=null) {
			return info;
		}
		info=QueryInfo.create(queryClass);
		queryInfoCache.put(queryClass, info);
		return info;
	}

}
