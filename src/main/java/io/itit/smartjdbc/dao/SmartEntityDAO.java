package io.itit.smartjdbc.dao;

import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.itit.smartjdbc.DAOInterceptor;
import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.QueryWhere;
import io.itit.smartjdbc.ResultSetHandler;
import io.itit.smartjdbc.SqlBean;
import io.itit.smartjdbc.provider.SelectProvider;
import io.itit.smartjdbc.util.ArrayUtils;

/**
 * 
 * @author skydu
 */
public class SmartEntityDAO<T> extends BaseEntityDAO{
	//
	protected String tableName;
	protected Class<T> entityClass;
	//
	protected Class<T> getTypeClass(){
		ParameterizedType pt=(ParameterizedType) getClass().getGenericSuperclass();
		@SuppressWarnings("unchecked")
		Class<T>type=(Class<T>) pt.getActualTypeArguments()[0];
		return type;
	}
	//
	public String getTableName() {
		if(tableName!=null){
			return tableName;
		}
		Class<T> clazz=getEntityClass();
		tableName=getSmartDataSource().getTableName(clazz);
		return tableName;
	}
	
	public Class<T> getEntityClass() {
		if(entityClass!=null) {
			return entityClass;
		}
		entityClass=getTypeClass();
		return entityClass;
	}
	/**
	 * 
	 * @param o
	 * @param excludeFields
	 * @return
	 */
	public int insert(T o,String... excludeFields){
		return insert(o, true, excludeFields);
	}
	/**
	 * 
	 * @param o
	 * @param withGenerateKey
	 * @param excludeFields
	 * @return
	 */
	public int insert(T o,boolean withGenerateKey,String... excludeFields){
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
	protected void beforeInsert(T o, boolean withGenerateKey, String[] excludeFields) {
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
	protected void afterInsert(int result, T o, boolean withGenerateKey, String[] excludeFields) {
		List<DAOInterceptor> interceptors=getDaoInterceptors();
		if(interceptors!=null) {
			for (DAOInterceptor interceptor : interceptors) {
				interceptor.afterInsert(result,o, withGenerateKey, excludeFields);
			}
		}
	}
	
	/**
	 * 更新（包括Null字段）
	 * @param bean
	 * @return
	 */
	public int updateIncludeNull(T bean){
		return update(bean,false,null);
	}
	
	/**
	 * 更新指定的字段(如果字段值为NULL,则不会更新)
	 * @param bean
	 * @param includeFields
	 * @return
	 */
	public int updateIncludeFields(T bean,String ... includeFields) {
		Set<String> includeFieldSet=null;
		if(includeFields!=null&&includeFields.length>0) {
			includeFieldSet=new LinkedHashSet<>();
			for (String includeField : includeFields) {
				includeFieldSet.add(includeField);
			}
		}
		return update(bean,includeFields);
	}
	
	/**
	 * 更新指定的字段(如果字段值为NULL,也会更新)
	 * @param bean
	 * @param includeFields
	 * @return
	 */
	public int updateIncludeFieldsIncludeNull(T bean,String ... includeFields) {
		Set<String> includeFieldSet=null;
		if(includeFields!=null&&includeFields.length>0) {
			includeFieldSet=new LinkedHashSet<>();
			for (String includeField : includeFields) {
				includeFieldSet.add(includeField);
			}
		}
		return update(bean,false,includeFieldSet);
	}
	
	/**
	 * 
	 * @param bean
	 * @param excludeFields
	 * @return
	 */
	public int updateExcludeFields(T bean,String ... excludeFields) {
		return update(bean,true,null,excludeFields);
	}
	
	/**
	 * 更新（不包括Null字段）
	 * @param bean
	 * @param includeFields
	 * @return
	 */
	public int update(T bean,
			String... includeFields){
		return update(bean,true,ArrayUtils.toSet(includeFields));
	}
	
	/**
	 * 
	 * @param bean
	 * @param includeFields
	 * @param excludeFields
	 * @return
	 */
	public int update(T bean,
			Set<String> includeFields,
			String... excludeFields) {
		return update(bean,true,includeFields,excludeFields);
	}
	//
	/**
	 * 
	 * @param bean
	 * @param excludeNull
	 * @param excludeFields
	 * @return
	 */
	public int update(T bean,
			boolean excludeNull,
			Set<String> includeFields,
			String... excludeFields){
		beforeUpdate(bean,excludeNull,excludeFields);
		SqlBean sqlBean=updateProvider().
				object(bean).
				excludeNull(excludeNull).
				includeFields(includeFields).
				excludeFields(excludeFields)
				.build();
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
	public int update(T bean,
			QueryWhere wq,
			String... excludeFields){
		return update(bean, wq, true, null, excludeFields);
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
	public int update(T bean,
			QueryWhere wq,
			boolean excludeNull,
			Set<String> includeFields,
			String... excludeFields){
		beforeUpdate(bean,excludeNull,excludeFields);
		SqlBean sqlBean=updateProvider().
				object(bean).
				excludeNull(excludeNull).
				includeFields(includeFields).
				excludeFields(excludeFields)
				.build();
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
	protected void beforeUpdate(T bean, boolean excludeNull, String[] excludeFields) {
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
	protected void afterUpdate(int result, T bean, boolean excludeNull, String[] excludeFields) {
		List<DAOInterceptor> interceptors=getDaoInterceptors();
		if(interceptors!=null) {
			for (DAOInterceptor interceptor : interceptors) {
				interceptor.afterUpdate(result,bean, excludeNull, excludeFields);
			}
		}
	}
	
	/**
	 * 
	 * @param qw
	 * @return
	 */
	public int delete(QueryWhere qw){
		Class<T> entityClass=getEntityClass();
		beforeDelete(qw);
		SqlBean sqlBean=deleteProvider().
				entityClass(entityClass).
				queryWhere(qw).
				build();
		int result=executeUpdate(sqlBean.sql,sqlBean.parameters);
		afterDelete(result,qw);
		return result;
	}
	
	/**
	 * 
	 * @param qw
	 */
	protected void beforeDelete(QueryWhere qw) {
		List<DAOInterceptor> interceptors=getDaoInterceptors();
		if(interceptors!=null) {
			for (DAOInterceptor interceptor : interceptors) {
				interceptor.beforeDelete(getEntityClass(), qw);
			}
		}
	}

	/**
	 * 
	 * @param result
	 * @param qt
	 */
	protected void afterDelete(int result, QueryWhere qt) {
		List<DAOInterceptor> interceptors=getDaoInterceptors();
		if(interceptors!=null) {
			for (DAOInterceptor interceptor : interceptors) {
				interceptor.afterDelete(result,getEntityClass(), qt);
			}
		}
	}
	
	/**
	 * 
	 * @param qw
	 * @param excludeFields
	 * @return
	 */
	public T getEntity(QueryWhere qw,String ... excludeFields){
		return getEntity(qw, null,excludeFields);
	}
	
	/**
	 * 
	 * @param qw
	 * @param includeFields
	 * @param excludeFields
	 * @return
	 */
	public T getEntity(QueryWhere qw,Set<String> includeFields,String ... excludeFields){
		Class<T> entityClass=getEntityClass();
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				query(qw).
				includeFields(includeFields).
				excludeFields(excludeFields).
				build();
		return queryObject(sqlBean.sql,sqlBean.parameters);
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
	public T getEntity(Query<T> query,String ... excludeFields){
		beforeQuery(query);
		Class<T> entityClass=getEntityClass();
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				query(query).
				excludeFields(excludeFields).
				build();
		return queryObject(sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param provider
	 * @return
	 */
	public T getEntity(SelectProvider provider){
		SqlBean sqlBean=provider.build();
		return queryObject(sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param qw
	 * @param excludeFields
	 * @return
	 */
	public List<T> getList(QueryWhere qw,String ... excludeFields){
		return getList(qw, null, excludeFields);
	}
	
	/**
	 * 
	 * @param qw
	 * @param includeFields
	 * @param excludeFields
	 * @return
	 */
	public List<T> getList(QueryWhere qw,Set<String> includeFields,String ... excludeFields){
		Class<T> entityClass=(Class<T>) getEntityClass();
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				query(qw).
				includeFields(includeFields).
				excludeFields(excludeFields).
				needPaging(true).
				build();
		return queryList(sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param query
	 * @param excludeFields
	 * @return
	 */
	public List<T> getList(Query<T> query,String ... excludeFields){
		return getList(query, null, excludeFields);
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	public List<T> getList(Query<T> query,Set<String> includeFields,String ... excludeFields){
		beforeQuery(query);
		Class<T> entityClass=(Class<T>) getEntityClass();
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				query(query).
				includeFields(includeFields).
				excludeFields(excludeFields).
				needPaging(true).
				build();
		return queryList(sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param selectProvider
	 * @return
	 */
	public List<T> getList(SelectProvider selectProvider){
		SqlBean sqlBean=selectProvider.needPaging(true).build();
		return queryList(sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param excludeFields
	 * @return
	 */
	public List<T> getAll(String ... excludeFields){
		Class<T> entityClass=(Class<T>) getEntityClass();
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				excludeFields(excludeFields).
				build();
		return queryList(sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param query
	 * @param excludeFields
	 * @return
	 */
	public List<T> getAll(QueryWhere query,String ... excludeFields){
		Class<T> entityClass=(Class<T>) getEntityClass();
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				query(query).
				excludeFields(excludeFields).
				build();
		return queryList(sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param query
	 * @param excludeFields
	 * @return
	 */
	public List<T> getAll(Query<T> query,String ... excludeFields){
		Class<T> entityClass=(Class<T>) getEntityClass();
		query.setPageSize(Integer.MAX_VALUE);
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				query(query).
				excludeFields(excludeFields).
				build();
		return queryList(sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param qw
	 * @return
	 */
	public int getListCount(QueryWhere qw){
		Class<T> entityClass=(Class<T>) getEntityClass();
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
	public int getListCount(Query<T> query){
		beforeQuery(query);
		Class<T> entityClass=(Class<T>) getEntityClass();
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
	 * @param sql
	 * @param rowHandler
	 * @param parameters
	 * @return
	 */
	public List<T> queryList(
			String sql,
			ResultSetHandler<T> rowHandler, 
			Object... parameters) {
		SqlBean sqlBean=parseSql(sql, parameters);
		return queryForList(sqlBean.sql, rowHandler, sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param sql
	 * @param parameters
	 * @return
	 */
	public List<T> queryList(String sql,Object... parameters) {
		Class<T> entityClass=(Class<T>) getEntityClass();
		SqlBean sqlBean=parseSql(sql, parameters);
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
		SqlBean sqlBean=parseSql(sql, parameters);
		return queryForInteger(sqlBean.sql, sqlBean.parameters);
	}
	/**
	 * 
	 * @param sql
	 * @param parameters
	 * @return
	 */
	public T queryObject(String sql,Object... parameters) {
		Class<T> entityClass=(Class<T>) getEntityClass();
		SqlBean sqlBean=parseSql(sql, parameters);
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
	public T queryObject(
			String sql,
			ResultSetHandler<T> rowHandler, 
			Object... parameters) {
		SqlBean sqlBean=parseSql(sql, parameters);
		return queryForObject(sqlBean.sql, rowHandler, sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param <S>
	 * @param query
	 * @param clazz
	 * @param field
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <S extends Number>S sum(Query<T> query,Class<S> clazz,String field){
		Class<T> entityClass=getEntityClass();
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
	 * @param <S>
	 * @param clazz
	 * @param field
	 * @param qt
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <S extends Number>S sum(Class<S> clazz,String field,QueryWhere qt){
		Class<T> entityClass=getEntityClass();
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
