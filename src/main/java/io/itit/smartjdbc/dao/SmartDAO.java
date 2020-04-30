package io.itit.smartjdbc.dao;

import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.itit.smartjdbc.Config;
import io.itit.smartjdbc.DAOInterceptor;
import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.QueryWhere;
import io.itit.smartjdbc.ResultSetHandler;
import io.itit.smartjdbc.SqlBean;
import io.itit.smartjdbc.provider.DeleteProvider;
import io.itit.smartjdbc.provider.InsertProvider;
import io.itit.smartjdbc.provider.SelectProvider;
import io.itit.smartjdbc.provider.UpdateProvider;

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
		SqlBean sqlBean=new InsertProvider(o, excludeFields).build();
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
		List<DAOInterceptor> interceptors=Config.getDaoInterceptors();
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
		List<DAOInterceptor> interceptors=Config.getDaoInterceptors();
		if(interceptors!=null) {
			for (DAOInterceptor interceptor : interceptors) {
				interceptor.afterInsert(result,o, withGenerateKey, excludeFields);
			}
		}
	}
	
	/**
	 * 
	 * @param bean
	 * @return
	 */
	public int updateExcludeNull(Object bean){
		return update(bean,true,null);
	}
	
	/**
	 * 
	 * @param bean
	 * @param includeFields
	 * @return
	 */
	public int updateIncludeFields(Object bean,String ... includeFields) {
		Set<String> includeFieldSet=null;
		if(includeFields!=null&&includeFields.length>0) {
			includeFieldSet=new LinkedHashSet<>();
			for (String includeField : includeFields) {
				includeFieldSet.add(includeField);
			}
		}
		return update(bean,includeFieldSet);
	}
	
	/**
	 * 
	 * @param bean
	 * @param excludeFields
	 * @return
	 */
	public int update(Object bean,
			String... excludeFields){
		return update(bean,false,null,excludeFields);
	}
	
	/**
	 * 
	 * @param bean
	 * @param includeFields
	 * @param excludeFields
	 * @return
	 */
	public int update(Object bean,
			Set<String> includeFields,
			String... excludeFields) {
		return update(bean,false,includeFields,excludeFields);
	}
	//
	/**
	 * 
	 * @param bean
	 * @param excludeNull
	 * @param excludeFields
	 * @return
	 */
	public int update(Object bean,
			boolean excludeNull,
			Set<String> includeFields,
			String... excludeFields){
		beforeUpdate(bean,excludeNull,excludeFields);
		SqlBean sqlBean=new UpdateProvider(bean, excludeNull,includeFields,excludeFields).build();
		int result=executeUpdate(sqlBean.sql,sqlBean.parameters);
		afterUpdate(result,bean,excludeNull,excludeFields);
		return result;
	}
	
	
	/**
	 * 
	 * @param bean
	 * @param wq
	 * @param excludeFields
	 * @return
	 */
	public int update(Object bean,
			QueryWhere wq,
			String... excludeFields){
		return update(bean, wq, false, null, excludeFields);
	}
	
	/**
	 * 
	 * @param bean
	 * @param wq
	 * @param excludeNull
	 * @param includeFields
	 * @param excludeFields
	 * @return
	 */
	public int update(Object bean,
			QueryWhere wq,
			boolean excludeNull,
			Set<String> includeFields,
			String... excludeFields){
		beforeUpdate(bean,excludeNull,excludeFields);
		SqlBean sqlBean=new UpdateProvider(bean, wq,excludeNull,includeFields,excludeFields).build();
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
		List<DAOInterceptor> interceptors=Config.getDaoInterceptors();
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
		List<DAOInterceptor> interceptors=Config.getDaoInterceptors();
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
		SqlBean sqlBean=new DeleteProvider(entityClass, qw).build();
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
		List<DAOInterceptor> interceptors=Config.getDaoInterceptors();
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
		List<DAOInterceptor> interceptors=Config.getDaoInterceptors();
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
		SqlBean sqlBean=new SelectProvider(entityClass).
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
		List<DAOInterceptor> interceptors=Config.getDaoInterceptors();
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
		SqlBean sqlBean=new SelectProvider(entityClass).
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
		SqlBean sqlBean=new SelectProvider(entityClass).query(qw).
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
		SqlBean sqlBean=new SelectProvider(entityClass).
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
		SqlBean sqlBean=new SelectProvider(entityClass).excludeFields(excludeFields).build();
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
		SqlBean sqlBean=new SelectProvider(entityClass).query(query).excludeFields(excludeFields).build();
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
		query.pageSize=Integer.MAX_VALUE;
		SqlBean sqlBean=new SelectProvider(entityClass).query(query).excludeFields(excludeFields).build();
		return queryList(entityClass,sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param qw
	 * @return
	 */
	public int getListCount(Class<?> entityClass,QueryWhere qw){
		SqlBean sqlBean=new SelectProvider(entityClass).
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
		SqlBean sqlBean=new SelectProvider(entityClass).
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
		ParameterizedType pt=(ParameterizedType) query.getClass().getGenericSuperclass();
		Class<T>type=(Class<T>) pt.getActualTypeArguments()[0];
		return type;
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
		SqlBean sqlBean=parseSql(sql, parameters);
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
		SqlBean sqlBean=parseSql(sql, parameters);
		return queryForList(sqlBean.sql, new ResultSetHandler<T>() {
			@Override
			public T handleRow(ResultSet row) throws Exception {
				T o=entityClass.newInstance();
				convertBean(o,row);
				return o;
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
		SqlBean sqlBean=parseSql(sql, parameters);
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
		SqlBean sqlBean=parseSql(sql, parameters);
		return queryForObject(sqlBean.sql, new ResultSetHandler<T>() {
			@Override
			public T handleRow(ResultSet row) throws Exception {
				T o=entityClass.newInstance();
				convertBean(o,row);
				return o;
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
		SqlBean sqlBean=parseSql(sql, parameters);
		return queryForObject(sqlBean.sql, rowHandler, sqlBean.parameters);
	}
	
	
	@SuppressWarnings("unchecked")
	public <S extends Number>S sum(Query<?> query,Class<S> clazz,String field){
		Class<?> entityClass=getEntityClass(query);
		SqlBean sqlBean=new SelectProvider(entityClass).query(query).sum(field).
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
		SqlBean sqlBean=new SelectProvider(entityClass).sum(field).query(qt).needOrderBy(false).
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
	
	//
}
