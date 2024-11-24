package io.itit.smartjdbc.dao;

import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.util.List;
import java.util.Set;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.ResultSetHandler;
import io.itit.smartjdbc.provider.SelectProvider;
import io.itit.smartjdbc.provider.entity.EntityDelete;
import io.itit.smartjdbc.provider.entity.EntityInsert;
import io.itit.smartjdbc.provider.entity.EntityUpdate;
import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.QueryWhere;
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
	 * @param bean
	 * @param withGenerateKey
	 * @param excludeFields
	 * @return
	 */
	public int insert(T bean,boolean withGenerateKey,String... excludeFields){
		return insert(EntityInsert.create(
				getSmartDataSource(), 
				bean, 
				withGenerateKey, excludeFields));
	}
	
	/**
	 * 
	 * @param insert
	 * @return
	 */
	public int insert(EntityInsert insert){
		SqlBean sqlBean=insertProvider().
				insert(insert).
				build();
		String sql=sqlBean.sql;
		Object[] parameters=sqlBean.parameters;
		int result=0;
		if(insert.isWithGenerateKey()){
			result=executeWithGenKey(sql,parameters);		
		}else{
			executeUpdate(sql,parameters);
		}
		return result;
	}
	
	/**
	 * 更新（包括Null字段）
	 * @param bean
	 * @param includeFields
	 * @return
	 */
	public int update(T bean, String... includeFields){
		return update(bean, false, ArrayUtils.toSet(includeFields));
	}
	
	/**
	 * 
	 * @param bean
	 * @param includeFields
	 * @return
	 */
	public int update(T bean, Set<String> includeFields) {
		return update(bean,false, includeFields);
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
		return update(EntityUpdate.create(getSmartDataSource(), 
				bean, 
				excludeNull, 
				includeFields, 
				null,
				excludeFields));
	}
	
	public int update(T bean,
			QueryWhere qw,
			boolean excludeNull,
			Set<String> includeFields,
			Set<String> excludeFields){
		return update(EntityUpdate.create(getSmartDataSource(), 
				bean, 
				excludeNull, 
				includeFields, 
				qw, 
				excludeFields));
	}
	
	/**
	 * 
	 * @param update
	 * @return
	 */
	public int update(EntityUpdate update){
		SqlBean sqlBean=updateProvider().
				update(update).
				build();
		int result=executeUpdate(sqlBean.sql,sqlBean.parameters);
		return result;
	}
	
	/**
	 * 
	 * @param qw
	 * @return
	 */
	public int delete(QueryWhere qw){
		SqlBean sqlBean=deleteProvider().
				delete(new EntityDelete(getTableName())).
				queryWhere(qw).
				build();
		int result=executeUpdate(sqlBean.sql,sqlBean.parameters);
		return result;
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
	 * @return
	 */
	public T getEntity(Query<T> query,String ... excludeFields){
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
	
	public int getAllCount(){
		Class<T> entityClass=(Class<T>) getEntityClass();
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				selectCount().
				needOrderBy(false).
				build();
		return queryForInt(sqlBean.sql, sqlBean.parameters);
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
	 * @param includeFields
	 * @param excludeFields
	 * @return
	 */
	public List<T> getAll(QueryWhere query,Set<String> includeFields,String ... excludeFields){
		Class<T> entityClass=(Class<T>) getEntityClass();
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				query(query).
				includeFields(includeFields).
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
	 * @param query
	 * @param includeFields
	 * @param excludeFields
	 * @return
	 */
	public List<T> getAll(Query<T> query,Set<String> includeFields,String ... excludeFields){
		Class<T> entityClass=(Class<T>) getEntityClass();
		query.setPageSize(Integer.MAX_VALUE);
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				query(query).
				includeFields(includeFields).
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
		return queryForInt(sqlBean.sql, sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	public int getListCount(Query<T> query){
		Class<T> entityClass=(Class<T>) getEntityClass();
		SqlBean sqlBean=selectProvider().
				entityClass(entityClass).
				selectCount().
				query(query).
				needOrderBy(false).
				build();
		return queryForInt(sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param selectProvider
	 * @return
	 */
	public int getListCount(SelectProvider selectProvider){
		SqlBean sqlBean=selectProvider.selectCount().needOrderBy(false).build();
		return queryForInt(sqlBean.sql,sqlBean.parameters);
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
		SqlBean sqlBean=SqlBean.build(sql, parameters);
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
		return queryForInt(sqlBean.sql, sqlBean.parameters);
	}
	/**
	 * 
	 * @param sql
	 * @param parameters
	 * @return
	 */
	public T queryObject(String sql,Object... parameters) {
		Class<T> entityClass=(Class<T>) getEntityClass();
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
	public T queryObject(
			String sql,
			ResultSetHandler<T> rowHandler, 
			Object... parameters) {
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return queryForObject(sqlBean.sql, rowHandler, sqlBean.parameters);
	}
}
