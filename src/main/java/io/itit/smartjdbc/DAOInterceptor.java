package io.itit.smartjdbc;

import io.itit.smartjdbc.provider.where.QueryWhere;

/**
 * 
 * @author skydu
 *
 */
public abstract class DAOInterceptor {

	/**
	 * 
	 * @param bean
	 * @param withGenerateKey
	 * @param excludeProperties
	 */
	public void beforeInsert(Object bean,boolean withGenerateKey,String[] excludeProperties) {
	}
	
	/**
	 * 
	 * @param result
	 * @param bean
	 * @param withGenerateKey
	 * @param excludeProperties
	 */
	public void afterInsert(int result,Object bean,boolean withGenerateKey,String[] excludeProperties) {
	}
	
	/**
	 * 
	 * @param bean
	 * @param excludeNull
	 * @param excludeProperties
	 */
	public void beforeUpdate(Object bean, boolean excludeNull, String[] excludeProperties) {
	}
	
	/**
	 * 
	 * @param result
	 * @param bean
	 * @param excludeNull
	 * @param excludeProperties
	 */
	public void afterUpdate(int result, Object bean, boolean excludeNull, String[] excludeProperties) {
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param qt
	 */
	public void beforeDelete(Class<?> entityClass, QueryWhere qt) {
	}

	/**
	 * 
	 * @param result
	 * @param entityClass
	 * @param qt
	 */
	public void afterDelete(int result, Class<?> entityClass, QueryWhere qt) {
	}
	
	/**
	 * 
	 * @param query
	 */
	public void beforeQuery(Query<?> query) {
		
	}
}
