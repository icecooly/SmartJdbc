package io.itit.smartjdbc.cache;

import io.itit.smartjdbc.domain.EntityInfo;
import io.itit.smartjdbc.domain.QueryInfo;

/**
 * 
 * @author skydu
 *
 */
public class CacheManager {
	//
	public static int MAX_CACHE_SIZE=10000;
	//

	private static LocalCache localCache=new LocalCache(MAX_CACHE_SIZE);
	//
	public static EntityInfo getEntityInfo(Class<?> entityClass) {
		return localCache.getEntityInfo(entityClass);
	}
	//
	public static QueryInfo getQueryInfo(Class<?> queryClass) {
		return localCache.getQueryInfo(queryClass);
	}
}
