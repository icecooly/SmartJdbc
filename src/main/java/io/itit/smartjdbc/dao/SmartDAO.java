package io.itit.smartjdbc.dao;

import java.sql.ResultSet;
import java.util.List;
import java.util.Set;

import io.itit.smartjdbc.DAOInterceptor;
import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.ResultSetHandler;
import io.itit.smartjdbc.provider.SelectProvider;
import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.QueryWhere;
import io.itit.smartjdbc.util.ArrayUtils;
import io.itit.smartjdbc.util.ClassUtils;

/**
 * 
 * @author skydu
 */
public class SmartDAO extends BaseEntityDAO{
	//
	/**
	 * 
	 * @param o
	 * @param excludeFields
	 * @return
	 */
	public int insert(Object o,String... excludeFields){
		return insert(o, true, excludeFields);
	}
	/**
	 * 
	 * @param o
	 * @param withGenerateKey
	 * @param excludeFields
	 * @return
	 */
	public int insert(Object o,boolean withGenerateKey,String... excludeFields){
		beforeInsert(o, withGenerateKey, excludeFields);
		SqlBean sqlBean=insertProvider().
				object(o).
				excludeFields(excludeFields).
				build();
		String sql=sqlBean.sql;
		Object[] parameters=sqlBean.parameters;
		int result=0;
		if(withGenerateKey){
			result=executeWithGenKey(sql,parameters);		
		}else{
			executeUpdate(sql,parameters);
		}
		afterInsert(result, o, withGenerateKey, excludeFields);
		return result;
	}

	/**
	 * 
	 * @param o
	 * @param withGenerateKey
	 * @param excludeFields
	 */
	protected void beforeInsert(Object o, boolean withGenerateKey, String[] excludeFields) {
		List<DAOInterceptor> interceptors=getDaoInterceptors();
		if(interceptors!=null) {
			for (DAOInterceptor interceptor : interceptors) {
				interceptor.beforeInsert(o, withGenerateKey, excludeFields);
			}
		}
	}
	
	/**
	 * 
	 * @param result
	 * @param o
	 * @param withGenerateKey
	 * @param excludeFields
	 */
	protected void afterInsert(int result, Object o, boolean withGenerateKey, String[] excludeFields) {
		List<DAOInterceptor> interceptors=getDaoInterceptors();
		if(interceptors!=null) {
			for (DAOInterceptor interceptor : interceptors) {
				interceptor.afterInsert(result,o, withGenerateKey, excludeFields);
			}
		}
	}
	
	/**
	 * 更新
	 * @param bean
	 * @param includeFields
	 * @return
	 */
	public int update(Object bean,
			String... includeFields){
		return update(bean,false,ArrayUtils.toSet(includeFields),null);
	}
	
	/**
	 * 
	 * @param bean
	 * @param queryWhere
	 * @return
	 */
	public int update(Object bean, QueryWhere queryWhere){
		return update(bean,false,null,queryWhere);
	}

	//
	/**
	 * 
	 * @param bean
	 * @param excludeNull 是否更新值为NULL的字段  true:不更新 false:更新
	 * @param includeFields 只更新这些指定的字段
	 * @param queryWhere where条件
	 * @param excludeFields 排除更新这些指定的字段
	 * @return
	 */
	public int update(Object bean,
			boolean excludeNull,
			Set<String> includeFields,
			QueryWhere queryWhere,
			String... excludeFields){
		beforeUpdate(bean,excludeNull,excludeFields);
		SqlBean sqlBean=updateProvider().
				object(bean).
				excludeNull(excludeNull).
				includeFields(includeFields).
				queryWhere(queryWhere).
				excludeFields(excludeFields).
				build();
		int result=executeUpdate(sqlBean.sql,sqlBean.parameters);
		afterUpdate(result,bean,excludeNull,excludeFields);
		return result;
	}
	
	/**
	 * 
	 * @param bean
	 * @param excludeNull
	 * @param excludeFields
	 */
	protected void beforeUpdate(Object bean, boolean excludeNull, String[] excludeFields) {
		List<DAOInterceptor> interceptors=getDaoInterceptors();
		if(interceptors!=null) {
			for (DAOInterceptor interceptor : interceptors) {
				interceptor.beforeUpdate(bean, excludeNull, excludeFields);
			}
		}
	}
	
	/**
	 * 
	 * @param result
	 * @param bean
	 * @param excludeNull
	 * @param excludeFields
	 */
	protected void afterUpdate(int result, Object bean, boolean excludeNull, String[] excludeFields) {
		List<DAOInterceptor> interceptors=getDaoInterceptors();
		if(interceptors!=null) {
			for (DAOInterceptor interceptor : interceptors) {
				interceptor.afterUpdate(result,bean, excludeNull, excludeFields);
			}
		}
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param qw
	 * @return
	 */
	public int delete(Class<?> entityClass,QueryWhere qw){
		beforeDelete(entityClass,qw);
		SqlBean sqlBean=deleteProvider().
				entityClass(entityClass).
				queryWhere(qw).
				build();
		int result=executeUpdate(sqlBean.sql,sqlBean.parameters);
		afterDelete(result,entityClass,qw);
		return result;
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param qw
	 */
	protected void beforeDelete(Class<?> entityClass, QueryWhere qw) {
		List<DAOInterceptor> interceptors=getDaoInterceptors();
		if(interceptors!=null) {
			for (DAOInterceptor interceptor : interceptors) {
				interceptor.beforeDelete(entityClass, qw);
			}
		}
	}

	/**
	 * 
	 * @param result
	 * @param entityClass
	 * @param qt
	 */
	protected void afterDelete(int result, Class<?> entityClass, QueryWhere qt) {
		List<DAOInterceptor> interceptors=getDaoInterceptors();
		if(interceptors!=null) {
			for (DAOInterceptor interceptor : interceptors) {
				interceptor.afterDelete(result,entityClass, qt);
			}
		}
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param qw
	 * @param excludeFields
	 * @return
	 */
	public <T> T getEntity(Class<T> entityClass,QueryWhere qw,String ... excludeFields){
		return getEntity(entityClass, qw, null,excludeFields);
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param qw
	 * @param includeFields
	 * @param excludeFields
	 * @return
	 */
	public <T> T getEntity(Class<T> entityClass,QueryWhere qw,Set<String> includeFields,String ... excludeFields){
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				query(qw).
				includeFields(includeFields).
				excludeFields(excludeFields).
				build();
		return queryObject(entityClass,sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param query
	 */
	protected void beforeQuery(Query<?> query) {
		List<DAOInterceptor> interceptors=getDaoInterceptors();
		if(interceptors!=null) {
			for (DAOInterceptor interceptor : interceptors) {
				interceptor.beforeQuery(query);
			}
		}
	}
	/**
	 * 
	 * @param query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getEntity(Query<?> query,String ... excludeFields){
		beforeQuery(query);
		Class<T> entityClass=(Class<T>) getEntityClass(query);
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				query(query).
				excludeFields(excludeFields).
				build();
		return queryObject(entityClass,sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param provider
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getEntity(SelectProvider provider){
		SqlBean sqlBean=provider.build();
		Class<T> clazz=(Class<T>) provider.getEntityClass();
		return queryObject(clazz,sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param qw
	 * @param excludeFields
	 * @return
	 */
	public <T> List<T> getList(Class<T> entityClass,QueryWhere qw,String ... excludeFields){
		return getList(entityClass, qw, null, excludeFields);
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param qw
	 * @param includeFields
	 * @param excludeFields
	 * @return
	 */
	public <T> List<T> getList(Class<T> entityClass,QueryWhere qw,Set<String> includeFields,String ... excludeFields){
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				query(qw).
				includeFields(includeFields).
				excludeFields(excludeFields).
				needPaging(true).
				build();
		return queryList(entityClass,sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param query
	 * @param excludeFields
	 * @return
	 */
	public <T> List<T> getList(Query<T> query,String ... excludeFields){
		return getList(query, null, excludeFields);
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	public <T> List<T> getList(Query<T> query,Set<String> includeFields,String ... excludeFields){
		beforeQuery(query);
		Class<T> entityClass=getEntityClass(query);
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				query(query).
				includeFields(includeFields).
				excludeFields(excludeFields).
				needPaging(true).
				build();
		return queryList(entityClass,sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param selectProvider
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(SelectProvider selectProvider){
		Class<T> entityClass=(Class<T>) selectProvider.getEntityClass();
		SqlBean sqlBean=selectProvider.needPaging(true).build();
		return queryList(entityClass,sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param entityClass
	 * @return
	 */
	public <T> List<T> getAll(Class<T> entityClass,String ... excludeFields){
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				excludeFields(excludeFields).
				build();
		return queryList(entityClass,sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param query
	 * @param excludeFields
	 * @return
	 */
	public <T> List<T> getAll(Class<T> entityClass,QueryWhere query,String ... excludeFields){
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				query(query).
				excludeFields(excludeFields).
				build();
		return queryList(entityClass,sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param query
	 * @param excludeFields
	 * @return
	 */
	public <T> List<T> getAll(Query<T> query,String ... excludeFields){
		Class<T> entityClass=getEntityClass(query);
		query.setPageSize(Integer.MAX_VALUE);
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				query(query).
				excludeFields(excludeFields).
				build();
		return queryList(entityClass,sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param qw
	 * @return
	 */
	public int getListCount(Class<?> entityClass,QueryWhere qw){
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				selectCount().
				query(qw).
				needOrderBy(false).
				build();
		return queryForInteger(sqlBean.sql, sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	public int getListCount(Query<?> query){
		beforeQuery(query);
		Class<?> entityClass=getEntityClass(query);
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				selectCount().
				query(query).
				needOrderBy(false).
				build();
		return queryForInteger(sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param selectProvider
	 * @return
	 */
	public int getListCount(SelectProvider selectProvider){
		SqlBean sqlBean=selectProvider.selectCount().needOrderBy(false).build();
		return queryForInteger(sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> Class<T> getEntityClass(Query<T> query) {
		return (Class<T>) ClassUtils.getSuperClassGenricType(query.getClass());
	}
	
	/**
	 * 
	 * @param sql
	 * @param rowHandler
	 * @param parameters
	 * @return
	 */
	public <T> List<T> queryList(
			String sql,
			ResultSetHandler<T> rowHandler, 
			Object... parameters) {
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return queryForList(sqlBean.sql, rowHandler, sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param sql
	 * @param parameters
	 * @return
	 */
	public <T> List<T> queryList(
			Class<T> entityClass,
			String sql,
			Object... parameters) {
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return queryForList(sqlBean.sql, new ResultSetHandler<T>() {
			@Override
			public T handleRow(ResultSet row) throws Exception {
				return convertBean(entityClass,row);
			}}, sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param sql
	 * @param parameters
	 * @return
	 */
	public int queryCount(
			String sql,
			Object... parameters) {
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return queryForInteger(sqlBean.sql, sqlBean.parameters);
	}
	/**
	 * 
	 * @param entityClass
	 * @param sql
	 * @param parameters
	 * @return
	 */
	public final <T> T queryObject(
			Class<T> entityClass,
			String sql,
			Object... parameters) {
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return queryForObject(sqlBean.sql, new ResultSetHandler<T>() {
			@Override
			public T handleRow(ResultSet row) throws Exception {
				return convertBean(entityClass,row);
			}}, sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param sql
	 * @param rowHandler
	 * @param parameters
	 * @return
	 */
	public final <T> T queryObject(
			String sql,
			ResultSetHandler<T> rowHandler, 
			Object... parameters) {
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return queryForObject(sqlBean.sql, rowHandler, sqlBean.parameters);
	}
	
	
	@SuppressWarnings("unchecked")
	public <S extends Number>S sum(Query<?> query,Class<S> clazz,String field){
		Class<?> entityClass=getEntityClass(query);
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				query(query).
				sum(field).
				ingoreSelectFileds().needOrderBy(false).build();
		String sql=sqlBean.sql;
		Object[] parameters=sqlBean.parameters;
		if(clazz==long.class||clazz==Long.class){
			return (S) queryForLong(sql,parameters);
		}
		if(clazz==int.class||clazz==Integer.class){
			return (S) queryForInteger(sql,parameters);
		}
		if(clazz==short.class||clazz==Short.class){
			return (S) queryForShort(sql,parameters);
		}
		if(clazz==double.class||clazz==Double.class){
			return (S) queryForDouble(sql,parameters);
		}
		if(clazz==float.class||clazz==Float.class){
			return (S) queryForFloat(sql,parameters);
		}
		throw new IllegalArgumentException(clazz.getSimpleName()+" not supported");
	}
	/**
	 * 
	 * @param entityClass
	 * @param clazz
	 * @param field
	 * @param qt
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <S extends Number>S sum(Class<?> entityClass,Class<S> clazz,String field,QueryWhere qt){
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				sum(field).
				query(qt).
				needOrderBy(false).
				ingoreSelectFileds().build();
		String sql=sqlBean.sql;
		Object[] parameters=sqlBean.parameters;
		if(clazz==long.class||clazz==Long.class){
			return (S) queryForLong(sql,parameters);
		}
		if(clazz==int.class||clazz==Integer.class){
			return (S) queryForInteger(sql,parameters);
		}
		if(clazz==short.class||clazz==Short.class){
			return (S) queryForShort(sql,parameters);
		}
		if(clazz==Double.class||clazz==Double.class){
			return (S) queryForDouble(sql,parameters);
		}
		if(clazz==Float.class||clazz==Float.class){
			return (S) queryForFloat(sql,parameters);
		}
		throw new IllegalArgumentException(clazz.getSimpleName()+" not supported");
	}
}
