package io.itit.smartjdbc.provider.where;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.itit.smartjdbc.enums.ConditionType;
import io.itit.smartjdbc.enums.SqlOperator;
import io.itit.smartjdbc.provider.SqlProvider;

/**
 * 
 * @author skydu
 *
 */
public class Where {
	//
	public String uuid;
	public ConditionType conditionType;
	public List<Where> children;
	public List<Condition> conditionList;
	//
	public static class Condition{
		public String alias;
		public String key;
		public String keyCast;
		public String keyFunc;
		public Object value;
		public SqlOperator operator;
		public String whereSql;
		public boolean isColumn;
		public JsonConfig jsonConfig;
		public ArrayConfig arrayConfig;
		public boolean formula;//右边是一个函数或一个字段名称 不是一个明确的值
		public boolean ignoreNull;
		//
		public Condition() {
			this.isColumn=true;
		}
	}
	//
	public static class JsonConfig{
		public boolean isJsonb=true;
		public String objectField;
		public boolean isArray;
	}
	//
	public static class ArrayConfig{
		public Class<?> rawType=String.class;//默认String.class
	}
	//
	public Where() {
		this.uuid=UUID.randomUUID().toString();
		this.children = new ArrayList<>(0);
		this.conditionList=new ArrayList<>(0);
	}
	
	public Where(ConditionType conditionType) {
		this();
		this.conditionType=conditionType;
	}
	//
	public Where where(String key, SqlOperator op) {
		return where(key, op, null);
	}
	//
	public Where where(String key, SqlOperator op, Object value) {
		return where(SqlProvider.MAIN_TABLE_ALIAS, key, op, value);
	}
	//
	public Where where(String alias, String key, SqlOperator op, Object value) {
		return where(alias, key, op, value, true, null, null);
	}
	//
	public Where where(Condition condition) {
		conditionList.add(condition);
		return this;
	}
	//
	public Where where(String alias, String key, SqlOperator op, Object value, 
			boolean isColumn, JsonConfig jsonConfig, String keyCast) {
		return where(alias, key, op, value, isColumn, jsonConfig, keyCast, null, false, false);
	}
	//
	public Where where(String alias, String key, SqlOperator op, Object value, 
			boolean isColumn, JsonConfig jsonConfig, String keyCast, String keyFunc, 
			boolean formula, boolean ignoreNull) {
		Condition w = new Condition();
		w.alias = alias;
		w.key = key;
		w.operator = op;
		w.value = value;
		w.isColumn=isColumn;
		w.jsonConfig=jsonConfig;
		w.keyCast=keyCast;
		w.keyFunc=keyFunc;
		w.formula=formula;
		w.ignoreNull=ignoreNull;
		conditionList.add(w);
		return this;
	}
	//
	public Where whereSql(String whereSql, Map<String,Object> values) {
		Condition w = new Condition();
		w.whereSql=whereSql;
		w.value=values;
		conditionList.add(w);
		return this;
	}
	//
	public Where and(Where w) {
		w.conditionType = ConditionType.AND;
		children.add(w);
		return this;
	}
	//
	public Where or(Where w) {
		w.conditionType = ConditionType.OR;
		children.add(w);
		return this;
	}
	//
	//
	/**
	 * key 等于
	 * @param key
	 * @param value
	 * @return
	 */
	public Where eq(String key,Object value){
		return this.where(key,SqlOperator.EQ, value);
	}
	
	/**
	 * alias.key 等于
	 * @param alias 表别名
	 * @param key
	 * @param value
	 * @return
	 */
	public Where eq(String alias,String key,Object value){
		return this.where(alias,key,SqlOperator.EQ, value);
	}
	
	/**
	 * key 不等于
	 * @param key
	 * @param value
	 * @return
	 */
	public Where ne(String key,Object value){
		return this.where(key, SqlOperator.NE, value);
	}
	
	/**
	 * alias.key 不等于
	 * @param alias 表别名
	 * @param key
	 * @param value
	 * @return
	 */
	public Where ne(String alias,String key,Object value){
		return this.where(alias,key, SqlOperator.NE, value);
	}
	
	/**
	 * key 小于
	 * @param key
	 * @param value
	 * @return
	 */
	public Where lt(String key,Object value){
		return this.where(key, SqlOperator.LT, value);
	}
	
	/**
	 * alias.key 小于
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public Where lt(String alias,String key,Object value){
		return this.where(alias,key, SqlOperator.LT, value);
	}
	
	/**
	 * key 小于等于
	 * @param key
	 * @param value
	 * @return
	 */
	public Where le(String key,Object value){
		return this.where(key, SqlOperator.LE, value);
	}
	
	/**
	 * alias.key 小于等于
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public Where le(String alias,String key,Object value){
		return this.where(alias, key, SqlOperator.LE, value);
	}
	
	/**
	 * key 大于
	 * @param key
	 * @param value
	 * @return
	 */
	public Where gt(String key,Object value){
		return this.where(key, SqlOperator.GT, value);
	}
	
	/**
	 * alias.key 大于
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public Where gt(String alias,String key,Object value){
		return this.where(alias,key, SqlOperator.GT, value);
	}
	
	/**
	 * key 大于等于
	 * @param key
	 * @param value
	 * @return
	 */
	public Where ge(String key,Object value){
		return this.where(key, SqlOperator.GE, value);
	}
	
	/**
	 * alias.key大于等于
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public Where ge(String alias,String key,Object value){
		return this.where(alias,key, SqlOperator.GE, value);
	}
	
	/**
	 * key LIKE '%值%'
	 * @param key
	 * @param value
	 * @return
	 */
	public Where like(String key,Object value){
		return this.where(key, SqlOperator.LIKE, value);
	}
	
	/**
	 * alias.key LIKE '%值%'
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public Where like(String alias,String key,Object value){
		return this.where(alias,key, SqlOperator.LIKE, value);
	}
	
	/**
	 * key NOT LIKE '%值%'
	 * @param key
	 * @param value
	 * @return
	 */
	public Where notLike(String key,Object value){
		return this.where(key,SqlOperator.NOT_LIKE, value);
	}
	
	/**
	 * alias.key NOT LIKE '%值%'
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public Where notLike(String alias,String key,Object value){
		return this.where(alias,key,SqlOperator.NOT_LIKE, value);
	}
	
	/**
	 * key LIKE '%值'
	 * @param key
	 * @param value
	 * @return
	 */
	public Where likeLeft(String key,Object value){
		return this.where(key, SqlOperator.LIKE_LEFT, value);
	}
	
	/**
	 * alias.key LIKE '%值'
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public Where likeLeft(String alias,String key,Object value){
		return this.where(alias, key, SqlOperator.LIKE_LEFT, value);
	}
	
	/**
	 * key NOT LIKE '%值'
	 * @param key
	 * @param value
	 * @return
	 */
	public Where notLikeLeft(String key,Object value){
		return this.where(key, SqlOperator.NOT_LIKE_LEFT, value);
	}
	
	/**
	 * alias.key NOT LIKE '%值'
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public Where notLikeLeft(String alias,String key,Object value){
		return this.where(alias,key, SqlOperator.NOT_LIKE_LEFT, value);
	}
	
	/**
	 * key LIKE '值%'
	 * @param key
	 * @param value
	 * @return
	 */
	public Where likeRight(String key,Object value){
		return this.where(key, SqlOperator.LIKE_RIGHT, value);
	}
	
	/**
	 * alias.key LIKE '值%'
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public Where likeRight(String alias,String key,Object value){
		return this.where(alias,key, SqlOperator.LIKE_RIGHT, value);
	}
	
	/**
	 * key NOT LIKE '值%'
	 * @param key
	 * @param value
	 * @return
	 */
	public Where notLikeRight(String key,Object value){
		return this.where(key, SqlOperator.NOT_LIKE_RIGHT, value);
	}
	
	/**
	 * alias.key NOT LIKE '值%'
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public Where notLikeRight(String alias,String key,Object value){
		return this.where(alias,key, SqlOperator.NOT_LIKE_RIGHT, value);
	}
	/**
	 * key in (值1，值2...)
	 * @param key
	 * @param value
	 * @return
	 */
	public Where in(String key,Object value) {
		return this.where(key, SqlOperator.IN, value);
	}
	
	/**
	 * alias.key in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public Where in(String alias,String key,Object value) {
		return this.where(alias,key, SqlOperator.IN, value);
	}
	
	/**
	 * key not in (值1，值2...)
	 * @param key
	 * @param value
	 * @return
	 */
	public Where notin(String key,Object value) {
		return this.where(key, SqlOperator.NOT_IN, value);
	}
	
	/**
	 * alias.key not in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public Where notin(String alias,String key,Object value) {
		return this.where(alias,key, SqlOperator.NOT_IN, value);
	}
	
	/**
	 * key not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public Where notin(String key,Collection<?> values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * alias.key not in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public Where notin(String alias,String key,Collection<?> values) {
		return this.where(alias,key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * key in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public Where in(String key,int[] values) {
		return this.where(key, SqlOperator.IN, values);
	}
	
	/**
	 * alias.key in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public Where in(String alias,String key,int[] values) {
		return this.where(alias,key, SqlOperator.IN, values);
	}
	
	/**
	 * key not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public Where notin(String key,int[] values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * alias.key not in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public Where notin(String alias,String key,int[] values) {
		return this.where(alias,key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * key in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public Where in(String key,short[] values) {
		return this.where(key, SqlOperator.IN, values);
	}
	
	/**
	 * alias.key in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public Where in(String alias,String key,short[] values) {
		return this.where(alias,key, SqlOperator.IN, values);
	}
	
	/**
	 * key not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public Where notin(String key,short[] values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * alias.key not in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public Where notin(String alias,String key,short[] values) {
		return this.where(alias,key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * key in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public Where in(String key,long[] values) {
		return this.where(key, SqlOperator.IN, values);
	}
	
	/**
	 * alias.key in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public Where in(String alias,String key,long[] values) {
		return this.where(alias,key, SqlOperator.IN, values);
	}
	
	/**
	 * key not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public Where notin(String key,long[] values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * alias.key not in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public Where notin(String alias,String key,long[] values) {
		return this.where(alias,key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * key in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public Where in(String key,byte[] values) {
		return this.where(key, SqlOperator.IN, values);
	}
	
	/**
	 * alias.key in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public Where in(String alias,String key,byte[] values) {
		return this.where(alias,key, SqlOperator.IN, values);
	}
	
	/**
	 * key not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public Where notin(String key,byte[] values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * alias.key not in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public Where notin(String alias,String key,byte[] values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * key in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public Where in(String key,String[] values) {
		return this.where(key, SqlOperator.IN, values);
	}
	
	/**
	 * alias.key in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public Where in(String alias,String key,String[] values) {
		return this.where(alias,key, SqlOperator.IN, values);
	}
	
	/**
	 * key not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public Where notin(String key,String[] values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * alias.key not in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public Where notin(String alias,String key,String[] values) {
		return this.where(alias,key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * key IS NULL
	 * @param key
	 * @return
	 */
	public Where isNull(String key){
		return this.where(key, SqlOperator.IS_NULL,null);
	}
	
	/**
	 * alias.key IS NULL
	 * @param alias
	 * @param key
	 * @return
	 */
	public Where isNull(String alias,String key){
		return this.where(alias,key,SqlOperator.IS_NULL,null);
	}
	
	/**
	 * key IS NOT NULL
	 * @param key
	 * @return
	 */
	public Where isNotNull(String key){
		return this.where(key, SqlOperator.IS_NOT_NULL,null);
	}
	
	/**
	 * alias.key IS NOT NULL
	 * @param alias
	 * @param key
	 * @return
	 */
	public Where isNotNull(String alias,String key){
		return this.where(alias,key, SqlOperator.IS_NOT_NULL,null);
	}

	/**
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public Where betweenAnd(String key, Object values) {
		return this.where(key, SqlOperator.BETWEEN_AND, values);
	}
	/**
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public Where notBetweenAnd(String key, Object values) {
		return this.where(key, SqlOperator.NOT_BETWEEN_AND, values);
	}
	/**
	 * 
	 * @param childWhere
	 * @return
	 */
	public Where addWhere(Where childWhere) {
		children.add(childWhere);
		return this;
	}
}