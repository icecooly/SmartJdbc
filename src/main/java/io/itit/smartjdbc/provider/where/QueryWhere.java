package io.itit.smartjdbc.provider.where;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import io.itit.smartjdbc.enums.ConditionType;
import io.itit.smartjdbc.enums.SqlOperator;
import io.itit.smartjdbc.provider.SqlProvider;
import io.itit.smartjdbc.provider.where.Where.JsonContain;
import io.itit.smartjdbc.provider.where.operator.Operator;
import io.itit.smartjdbc.provider.where.operator.OperatorBuilder;
import io.itit.smartjdbc.provider.where.operator.OperatorContext;

/**
 * 
 * @author skydu
 *
 */
public class QueryWhere {
	//
	public static class WhereStatment{
		public String sql;
		public Object[] values;
	}
	//
	protected Where where;
	protected Set<String> orderBys;
	protected int limitStart=0;
	protected int limitEnd=-1;
	protected boolean forUpdate;
	//
	protected QueryWhere(ConditionType conditionType) {
		this.where=new Where(conditionType);
		this.orderBys=new LinkedHashSet<>();
	}
	//
	public static QueryWhere create(){
		return new QueryWhere(ConditionType.AND);//默认AND
	}
	//
	public static QueryWhere create(ConditionType conditionType){
		return new QueryWhere(conditionType);
	}
	//
	public QueryWhere where(String key,Object value){
		return this.where(key, SqlOperator.EQ, value);
	}
	//
	public QueryWhere where(String key,SqlOperator op,Object value){
		this.where(null, key, op, value);
		return this;
	}
	//
	public QueryWhere where(String alias,String key,SqlOperator op,Object value){
		where.where(alias, key, op, value);
		return this;
	}
	//
	public QueryWhere where(String alias,String key,SqlOperator op,Object value,JsonContain jsonContain){
		where.where(alias, key, op, value, jsonContain);
		return this;
	}
	//
	public QueryWhere whereSql(String sql,Object ...values){
		where.whereSql(sql, values);
		return this;
	}
	/**
	 * 
	 * @param w
	 * @return
	 */
	public QueryWhere and(Where w) {
		where.and(w);
		return this;
	}
	/**
	 * 
	 * @param w
	 * @return
	 */
	public QueryWhere or(Where w) {
		where.or(w);
		return this;
	}
	//
	public QueryWhere orderBy(String orderBy){
		this.orderBys.add(orderBy);
		return this;
	}
	//
	public QueryWhere limit(int start,int limit){
		this.limitStart=start;
		this.limitEnd=limit;
		return this;
	}
	//
	public QueryWhere limit(int end){
		this.limitStart=0;
		this.limitEnd=end;
		return this;
	}
	//
	public QueryWhere forUpdate() {
		forUpdate=true;
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public WhereStatment whereStatement(SqlProvider selectProvider){
		return whereStatement(selectProvider,false);
	}
	/**
	 * 
	 * @param needAliasAll
	 * @return
	 */
	public WhereStatment whereStatement(SqlProvider selectProvider, boolean needAliasAll){
		WhereStatment statment=new WhereStatment();
		List<Object>values=new LinkedList<Object>();
		StringBuilder sql=new StringBuilder();
		sql.append(" ");
		int conditionCount=getConditionCount(where);
		if(conditionCount>0) {
			sql.append(" and ");
			appendWhereSql(selectProvider, needAliasAll,sql, values, where);
		}
		sql.append(" ");
		if(forUpdate) {
			sql.append(" for update ");
		}
		statment.sql=sql.toString();
		statment.values=values.toArray();
		return statment;
	}
	//
	public Object[] whereValues(SqlProvider sqlProvider) {
		return whereStatement(sqlProvider,true).values;
	}
	//
	//获取下一级的查询条件的数量（只是children 非递归）如果没有查询条件则删除这个查询
	protected int getConditionCount(Where w) {
		if(w==null) {
			return 0;
		}
		if(w.conditionType==null) {
			return 1;
		}
		int conditionCount=0;
		if(w.children!=null) {
			for (Where child : w.children) {
				if(child.conditionType==null) {
					conditionCount++;
				}
			}
		}
		return conditionCount;
	}
	//
	private String getAlias( Where w, boolean needAliasAll) {
		if(w.alias!=null) {
			return w.alias;
		}else if(needAliasAll) {
			return SqlProvider.MAIN_TABLE_ALIAS;
		}
		return "";
	}
	//
	protected void appendWhereSql(SqlProvider sqlProvider, boolean needAliasAll,StringBuilder sql,List<Object> valueList,Where parent) {
		List<Where> wheres=parent.children;
		if(wheres==null||wheres.isEmpty()) {
			return;
		}
		int conditionCount=getConditionCount(parent);
		if(conditionCount==0) {
			return;
		}
		boolean and=parent.conditionType==ConditionType.AND?true:false;
		sql.append(" ( ");
		int index=0;
		OperatorContext ctx=new OperatorContext(sqlProvider.getSmartDataSource());
		ctx.setParameters(valueList);
		for (Where w : wheres) {
			if(index>0) {
				if(and) {
					sql.append(" and ");
				}else {
					sql.append(" or ");
				}
			}
			if(w.conditionType!=null) {
				appendWhereSql(sqlProvider, needAliasAll, sql, valueList, w);
				continue;
			}
			if(w.key!=null){
				w.alias=getAlias(w, needAliasAll);
				ctx.setWhere(w);
				Operator operator=OperatorBuilder.build(ctx);
				sql.append(operator.build());
			}else{
				sql.append(" "+ w.sql+" ");
				if(w.sqlValues!=null&&w.sqlValues.size()>0) {
					valueList.addAll(w.sqlValues);
				}
			}
			index++;
		}//for
		sql.append(")");
	}
	//
	/**
	 * @return the orderBy
	 */
	public Set<String> getOrderBys() {
		return orderBys;
	}
	/**
	 * @return the limitStart
	 */
	public int getLimitStart() {
		return limitStart;
	}
	/**
	 * @return the limitEnd
	 */
	public int getLimitEnd() {
		return limitEnd;
	}
	
	/**
	 * 
	 * @return
	 */
	public Where getWhere() {
		return where;
	}
	
	/**
	 * 
	 * @param where
	 */
	public void setWhere(Where where) {
		this.where = where;
	}
	
	/**
	 * @return the forUpdate
	 */
	public boolean isForUpdate() {
		return forUpdate;
	}
	/**
	 * @param forUpdate the forUpdate to set
	 */
	public void setForUpdate(boolean forUpdate) {
		this.forUpdate = forUpdate;
	}
	/**
	 * @param orderBys the orderBys to set
	 */
	public void setOrderBys(Set<String> orderBys) {
		this.orderBys = orderBys;
	}
	/**
	 * @param limitStart the limitStart to set
	 */
	public void setLimitStart(int limitStart) {
		this.limitStart = limitStart;
	}
	/**
	 * @param limitEnd the limitEnd to set
	 */
	public void setLimitEnd(int limitEnd) {
		this.limitEnd = limitEnd;
	}
	//
	/**
	 * key 等于
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere eq(String key,Object value){
		return this.where(key,SqlOperator.EQ, value);
	}
	
	/**
	 * alias.key 等于
	 * @param alias 表别名
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere eq(String alias,String key,Object value){
		return this.where(alias,key,SqlOperator.EQ, value);
	}
	
	/**
	 * key 不等于
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere ne(String key,Object value){
		return this.where(key, SqlOperator.NE, value);
	}
	
	/**
	 * alias.key 不等于
	 * @param alias 表别名
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere ne(String alias,String key,Object value){
		return this.where(alias,key, SqlOperator.NE, value);
	}
	
	/**
	 * key 小于
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere lt(String key,Object value){
		return this.where(key, SqlOperator.LT, value);
	}
	
	/**
	 * alias.key 小于
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere lt(String alias,String key,Object value){
		return this.where(alias,key, SqlOperator.LT, value);
	}
	
	/**
	 * key 小于等于
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere le(String key,Object value){
		return this.where(key, SqlOperator.LE, value);
	}
	
	/**
	 * alias.key 小于等于
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere le(String alias,String key,Object value){
		return this.where(alias, key, SqlOperator.LE, value);
	}
	
	/**
	 * key 大于
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere gt(String key,Object value){
		return this.where(key, SqlOperator.GT, value);
	}
	
	/**
	 * alias.key 大于
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere gt(String alias,String key,Object value){
		return this.where(alias,key, SqlOperator.GT, value);
	}
	
	/**
	 * key 大于等于
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere ge(String key,Object value){
		return this.where(key, SqlOperator.GE, value);
	}
	
	/**
	 * alias.key大于等于
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere ge(String alias,String key,Object value){
		return this.where(alias,key, SqlOperator.GE, value);
	}
	
	/**
	 * key LIKE '%值%'
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere like(String key,Object value){
		return this.where(key, SqlOperator.LIKE, value);
	}
	
	/**
	 * alias.key LIKE '%值%'
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere like(String alias,String key,Object value){
		return this.where(alias,key, SqlOperator.LIKE, value);
	}
	
	/**
	 * key NOT LIKE '%值%'
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere notLike(String key,Object value){
		return this.where(key,SqlOperator.NOT_LIKE, value);
	}
	
	/**
	 * alias.key NOT LIKE '%值%'
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere notLike(String alias,String key,Object value){
		return this.where(alias,key,SqlOperator.NOT_LIKE, value);
	}
	
	/**
	 * key LIKE '%值'
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere likeLeft(String key,Object value){
		return this.where(key, SqlOperator.LIKE_LEFT, value);
	}
	
	/**
	 * alias.key LIKE '%值'
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere likeLeft(String alias,String key,Object value){
		return this.where(alias, key, SqlOperator.LIKE_LEFT, value);
	}
	
	/**
	 * key NOT LIKE '%值'
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere notLikeLeft(String key,Object value){
		return this.where(key, SqlOperator.NOT_LIKE_LEFT, value);
	}
	
	/**
	 * alias.key NOT LIKE '%值'
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere notLikeLeft(String alias,String key,Object value){
		return this.where(alias,key, SqlOperator.NOT_LIKE_LEFT, value);
	}
	
	/**
	 * key LIKE '值%'
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere likeRight(String key,Object value){
		return this.where(key, SqlOperator.LIKE_RIGHT, value);
	}
	
	/**
	 * alias.key LIKE '值%'
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere likeRight(String alias,String key,Object value){
		return this.where(alias,key, SqlOperator.LIKE_RIGHT, value);
	}
	
	/**
	 * key NOT LIKE '值%'
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere notLikeRight(String key,Object value){
		return this.where(key, SqlOperator.NOT_LIKE_RIGHT, value);
	}
	
	/**
	 * alias.key NOT LIKE '值%'
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere notLikeRight(String alias,String key,Object value){
		return this.where(alias,key, SqlOperator.NOT_LIKE_RIGHT, value);
	}
	/**
	 * key in (值1，值2...)
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere in(String key,Object value) {
		return this.where(key, SqlOperator.IN, value);
	}
	
	/**
	 * alias.key in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere in(String alias,String key,Object value) {
		return this.where(alias,key, SqlOperator.IN, value);
	}
	
	/**
	 * key not in (值1，值2...)
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere notin(String key,Object value) {
		return this.where(key, SqlOperator.NOT_IN, value);
	}
	
	/**
	 * alias.key not in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere notin(String alias,String key,Object value) {
		return this.where(alias,key, SqlOperator.NOT_IN, value);
	}
	
	/**
	 * key not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere notin(String key,Collection<?> values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * alias.key not in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere notin(String alias,String key,Collection<?> values) {
		return this.where(alias,key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * key in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere in(String key,int[] values) {
		return this.where(key, SqlOperator.IN, values);
	}
	
	/**
	 * alias.key in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere in(String alias,String key,int[] values) {
		return this.where(alias,key, SqlOperator.IN, values);
	}
	
	/**
	 * key not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere notin(String key,int[] values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * alias.key not in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere notin(String alias,String key,int[] values) {
		return this.where(alias,key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * key in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere in(String key,short[] values) {
		return this.where(key, SqlOperator.IN, values);
	}
	
	/**
	 * alias.key in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere in(String alias,String key,short[] values) {
		return this.where(alias,key, SqlOperator.IN, values);
	}
	
	/**
	 * key not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere notin(String key,short[] values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * alias.key not in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere notin(String alias,String key,short[] values) {
		return this.where(alias,key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * key in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere in(String key,long[] values) {
		return this.where(key, SqlOperator.IN, values);
	}
	
	/**
	 * alias.key in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere in(String alias,String key,long[] values) {
		return this.where(alias,key, SqlOperator.IN, values);
	}
	
	/**
	 * key not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere notin(String key,long[] values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * alias.key not in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere notin(String alias,String key,long[] values) {
		return this.where(alias,key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * key in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere in(String key,byte[] values) {
		return this.where(key, SqlOperator.IN, values);
	}
	
	/**
	 * alias.key in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere in(String alias,String key,byte[] values) {
		return this.where(alias,key, SqlOperator.IN, values);
	}
	
	/**
	 * key not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere notin(String key,byte[] values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * alias.key not in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere notin(String alias,String key,byte[] values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * key in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere in(String key,String[] values) {
		return this.where(key, SqlOperator.IN, values);
	}
	
	/**
	 * alias.key in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere in(String alias,String key,String[] values) {
		return this.where(alias,key, SqlOperator.IN, values);
	}
	
	/**
	 * key not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere notin(String key,String[] values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * alias.key not in (值1，值2...)
	 * @param alias
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere notin(String alias,String key,String[] values) {
		return this.where(alias,key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 *  JSON_CONTAINS (key,值1) or JSON_CONTAINS (key,值2)
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere jsonContainsAny(String key,Object value) {
		return this.where(key, SqlOperator.JSON_CONTAINS_ANY, value);
	}
	
	/**
	 * JSON_CONTAINS (key,值1) or JSON_CONTAINS (key,值2)
	 * @param key
	 * @param values
	 * @return
	 */
	public QueryWhere jsonContainsAny(String key,Collection<?> values) {
		return this.where(key, SqlOperator.JSON_CONTAINS_ANY, values);
	}
	
	/**
	 * JSON_CONTAINS (alias.key,值1) or JSON_CONTAINS (alias.key,值2)
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere jsonContainsAny(String alias,String key,Object value) {
		return this.where(alias,key, SqlOperator.JSON_CONTAINS_ANY, value);
	}
	
	/**
	 * 
	 * @param alias
	 * @param key
	 * @param values
	 * @param jsonContain
	 * @return
	 */
	public QueryWhere jsonContainsAny(String alias,String key,Object values,JsonContain jsonContain) {
		return this.where(alias, key, SqlOperator.JSON_CONTAINS_ANY, values, jsonContain);
	}
	
	/**
	 *  JSON_CONTAINS (key,值1)=0 and JSON_CONTAINS (key,值2)=0
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere jsonNotContainsAny(String key,Object value) {
		return this.where(key, SqlOperator.JSON_NOT_CONTAINS_ANY, value);
	}
	
	/**
	 * JSON_CONTAINS (alias.key,值1)=0 and JSON_CONTAINS (alias.key,值2)
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere jsonNotContainsAny(String alias,String key,Object value) {
		return this.where(alias,key, SqlOperator.JSON_NOT_CONTAINS_ANY, value);
	}
	
	/**
	 * 
	 * @param alias
	 * @param key
	 * @param values
	 * @param jsonContain
	 * @return
	 */
	public QueryWhere jsonNotContainsAny(String alias,String key,Collection<?> values,JsonContain jsonContain) {
		return this.where(alias, key, SqlOperator.JSON_NOT_CONTAINS_ANY, values, jsonContain);
	}
	
	/**
	 * 
	 * @param alias
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere jsonContainsAnyAll(String alias,String key,Object value) {
		return this.where(alias,key, SqlOperator.JSON_CONTAINS_ALL, value);
	}
	
	/**
	 * 
	 * @param alias
	 * @param key
	 * @param values
	 * @param jsonContain
	 * @return
	 */
	public QueryWhere jsonContainsAnyAll(String alias,String key,Collection<?> values,JsonContain jsonContain) {
		return this.where(alias, key, SqlOperator.JSON_CONTAINS_ALL, values, jsonContain);
	}
	/**
	 * key IS NULL
	 * @param key
	 * @return
	 */
	public QueryWhere isNull(String key){
		return this.where(key, SqlOperator.IS_NULL,null);
	}
	
	/**
	 * alias.key IS NULL
	 * @param alias
	 * @param key
	 * @return
	 */
	public QueryWhere isNull(String alias,String key){
		return this.where(alias,key,SqlOperator.IS_NULL,null);
	}
	
	/**
	 * key IS NOT NULL
	 * @param key
	 * @return
	 */
	public QueryWhere isNotNull(String key){
		return this.where(key, SqlOperator.IS_NOT_NULL,null);
	}
	
	/**
	 * alias.key IS NOT NULL
	 * @param alias
	 * @param key
	 * @return
	 */
	public QueryWhere isNotNull(String alias,String key){
		return this.where(alias,key, SqlOperator.IS_NOT_NULL,null);
	}
	
}
