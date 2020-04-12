package io.itit.smartjdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.itit.smartjdbc.annotations.QueryField.OrGroup;
import io.itit.smartjdbc.enums.SqlOperator;
import io.itit.smartjdbc.util.ArrayUtils;
import io.itit.smartjdbc.util.StringUtil;

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
	public static class Where{
		public String alias;
		public String key;
		public Object value;
		public SqlOperator operator;
		public OrGroup orGroup;
		public String sql;
		public LinkedList<Object> sqlValues;
		public Where() {
			sqlValues=new LinkedList<Object>();
		}
	}
	//
	public static class OrGroupWheres{
		public String group;
		public List<Where> orWheres;
		public Map<String,List<Where>> childAndWheres;
		//
		public OrGroupWheres() {
			orWheres=new ArrayList<>();
			childAndWheres=new LinkedHashMap<>();
		}
	}
	//
	protected List<Where> wheres;
	protected Map<String,OrGroupWheres>orWheres;
	protected Set<String> orderBys;
	protected int limitStart=0;
	protected int limitEnd=-1;
	protected boolean forUpdate;
	//
	protected QueryWhere() {
		wheres=new LinkedList<>();
		orWheres=new LinkedHashMap<>();
		orderBys=new LinkedHashSet<>();
	}
	//
	public static QueryWhere create(){
		return new QueryWhere();
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
		return where(alias, key, op, value, null);
	}
	//
	public QueryWhere where(String alias,String key,SqlOperator op,Object value,OrGroup orGroup){
		Where w=new Where();
		w.alias=alias;
		w.key=key;
		w.operator=op;
		w.value=value;
		w.orGroup=orGroup;
		addWhere(w, orGroup);
		return this;
	}
	//
	private void addWhere(Where w,OrGroup orGroup) {
		if(orGroup!=null) {
			OrGroupWheres orGroupWheres=orWheres.get(orGroup.group());
			if(orGroupWheres==null){
				orGroupWheres=new OrGroupWheres();
				orWheres.put(orGroup.group(), orGroupWheres);
			}
			if(!StringUtil.isEmpty(orGroup.childAndGroup())) {//child and
				List<Where> wheres=orGroupWheres.childAndWheres.get(orGroup.childAndGroup());
				if(wheres==null) {
					wheres=new ArrayList<>();
					orGroupWheres.childAndWheres.put(orGroup.childAndGroup(),wheres);
				}
				wheres.add(w);
			}else {//or
				orGroupWheres.orWheres.add(w);
			}
			
		}else {
			this.wheres.add(w);
		}
	}
	//
	public  QueryWhere in(String alias,String key,Object[] values) {
		return in(alias, key, values, null);
	}
	//
	public  QueryWhere in(String alias,String key,Object[] values,OrGroup orGroup) {
		if(values!=null&&values.length>0) {
			this.where(alias, key, SqlOperator.IN, values,orGroup);
		}
		return this;
	}
	//
	public  QueryWhere notin(String alias,String key,Object[] values) {
		return notin(alias, key, values, null);
	}
	//
	public  QueryWhere notin(String alias,String key,Object[] values,OrGroup orGroup) {
		if(values!=null&&values.length>0) {
			this.where(alias, key, SqlOperator.NOT_IN, values,orGroup);
		}
		return this;
	}
	//
	public QueryWhere whereSql(String sql,Object ...values){
		return whereSql(sql, null, values);
	}
	//
	public QueryWhere whereSql(String sql,OrGroup orGroup,Object ...values){
		Where w=new Where();
		w.sql=sql;
		for(int i=0;i<values.length;i++){
			w.sqlValues.add(values[i]);
		}
		addWhere(w, orGroup);
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
	//
	public WhereStatment whereStatement(){
		WhereStatment statment=new WhereStatment();
		List<Object>values=new LinkedList<Object>();
		StringBuilder sql=new StringBuilder();
		sql.append(" ");
		if(wheres.size()>0) {
			sql.append(" and ");
			appendWhereSql(values,sql,wheres, true);
		}
		for(OrGroupWheres wheres:orWheres.values()){
			if(wheres==null) {
				continue;
			}
			sql.append(" and (");
			appendWhereSql(values,sql,wheres.orWheres, false);
			boolean needAddOr=wheres.orWheres.size()>0;
			for (List<Where> andWheres : wheres.childAndWheres.values()) {
				if(andWheres.isEmpty()) {
					continue;
				}
				if(needAddOr) {
					sql.append(" or ");
				}else{
					needAddOr=true;
				}
				sql.append(" ( ");
				appendWhereSql(values,sql, andWheres, true);
				sql.append(" ) ");
			}
			sql.append(" ) ");
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
	public Object[] whereValues() {
		return whereStatement().values;
	}
	
	//
	private void appendWhereSql(List<Object> valueList,StringBuilder sql,List<Where> wheres,boolean isAnd) {
		int index=0;
		for (Where w : wheres) {
			if(index>0) {
				if(isAnd) {
					sql.append(" and ");
				}else {
					sql.append(" or ");
				}
			}
			if(w.key!=null){
				Set<String> keys=new LinkedHashSet<>();
				if(w.key.indexOf(",")!=-1) {
					String[] keyList=w.key.split(",");
					for (String key : keyList) {
						if(StringUtil.isEmpty(key.trim())) {
							continue;
						}
						keys.add(key);
					}
				}else {
					keys.add(w.key);
				}
				sql.append(" ( ");
				int keyIndex=1;
				for (String key : keys) {
					String value="?";
					if(w.alias!=null) {
						sql.append(w.alias).append(".");
					}
					sql.append("`").append(key).append("` ");
					sql.append(getOperator(w.operator)).append(" ");
					if(w.operator.equals(SqlOperator.LIKE)||
							w.operator.equals(SqlOperator.NOT_LIKE)){
						sql.append(" concat('%',"+value+",'%') ");
						valueList.add(w.value);
					}else if(w.operator.equals(SqlOperator.LIKE_LEFT)||
							w.operator.equals(SqlOperator.NOT_LIKE_LEFT)){
						sql.append(" concat('%',"+value+") ");
						valueList.add(w.value);
					}else if(w.operator.equals(SqlOperator.LIKE_RIGHT)||
							w.operator.equals(SqlOperator.NOT_LIKE_RIGHT)){
						sql.append(" concat("+value+",'%') ");
						valueList.add(w.value);
					}else if(w.operator.equals(SqlOperator.IS_NULL)){
						valueList.add(w.value);
					}else if(w.operator.equals(SqlOperator.IS_NOT_NULL)){
						valueList.add(w.value);
					}else if(w.operator.equals(SqlOperator.IN)) {
						Object[] values=ArrayUtils.convert(w.value);
						if(values!=null&&values.length>0) {
							sql.append(" ( ");
							for (int i = 0; i < values.length; i++) {
								sql.append(" ?,");
								valueList.add(values[i]);
							}
							sql.deleteCharAt(sql.length() - 1);
							sql.append(" ) ");
						}
					}else if(w.operator.equals(SqlOperator.NOT_IN)) {
						Object[] values=ArrayUtils.convert(w.value);
						if(values!=null&&values.length>0) {
							sql.append(" ( ");
							for (int i = 0; i < values.length; i++) {
								sql.append(" ?,");
								valueList.add(values[i]);
							}
							sql.deleteCharAt(sql.length() - 1);
							sql.append(" ) ");
						}
					}else{
						sql.append("  "+value+" ");
						valueList.add(w.value);
					}
					if(keyIndex<keys.size()) {
						sql.append(" or ");
					}
					keyIndex++;
				}
				sql.append(" ) ");
			}else{
				sql.append(" "+ w.sql+" ");
				if(w.sqlValues!=null&&w.sqlValues.size()>0) {
					valueList.addAll(w.sqlValues);
				}
			}
			index++;
		}
	}
	//
	private String getOperator(SqlOperator opr) {
		if(opr.equals(SqlOperator.EQ)) {
			return "=";
		}
		if(opr.equals(SqlOperator.NE)) {
			return "<>";
		}
		if(opr.equals(SqlOperator.LT)) {
			return "<";
		}
		if(opr.equals(SqlOperator.LE)) {
			return "<=";
		}
		if(opr.equals(SqlOperator.GT)) {
			return ">";
		}
		if(opr.equals(SqlOperator.GE)) {
			return ">=";
		}
		if(opr.equals(SqlOperator.LIKE)||
				opr.equals(SqlOperator.LIKE_LEFT)||
				opr.equals(SqlOperator.LIKE_RIGHT)) {
			return "like";
		}
		if(opr.equals(SqlOperator.NOT_LIKE)||
				opr.equals(SqlOperator.NOT_LIKE_LEFT)||
				opr.equals(SqlOperator.NOT_LIKE_RIGHT)) {
			return "not like";
		}
		if(opr.equals(SqlOperator.IN)) {
			return "in";
		}
		if(opr.equals(SqlOperator.NOT_IN)) {
			return "not in";
		}
		if(opr.equals(SqlOperator.IS_NULL)) {
			return "is null";
		}
		if(opr.equals(SqlOperator.IS_NOT_NULL)) {
			return "is not null";
		}
		return null;
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
	
	public List<Where> getWheres() {
		return wheres;
	}
	//
	/**
	 * 等于 =
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere eq(String key,Object value){
		return this.where(key,SqlOperator.EQ, value);
	}
	
	/**
	 * 不等于 <>
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere ne(String key,Object value){
		return this.where(key, SqlOperator.NE, value);
	}
	
	/**
	 * 小于 <
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere lt(String key,Object value){
		return this.where(key, SqlOperator.LT, value);
	}
	
	/**
	 * 小于等于 <
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere le(String key,Object value){
		return this.where(key, SqlOperator.LE, value);
	}
	
	/**
	 * 大于 >
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere gt(String key,Object value){
		return this.where(key, SqlOperator.GT, value);
	}
	
	/**
	 * 大于等于 >=
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere ge(String key,Object value){
		return this.where(key, SqlOperator.GE, value);
	}
	
	/**
	 * LIKE '%值%'
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere like(String key,Object value){
		return this.where(key, SqlOperator.LIKE, value);
	}
	
	/**
	 * NOT LIKE '%值%'
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere notLike(String key,Object value){
		return this.where(key,SqlOperator.NOT_LIKE, value);
	}
	
	/**
	 * LIKE '%值'
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere likeLeft(String key,Object value){
		return this.where(key, SqlOperator.LIKE_LEFT, value);
	}
	
	/**
	 * NOT LIKE '%值'
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere notLikeLeft(String key,Object value){
		return this.where(key, SqlOperator.NOT_LIKE_LEFT, value);
	}
	
	/**
	 * LIKE '值%'
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere likeRight(String key,Object value){
		return this.where(key, SqlOperator.LIKE_RIGHT, value);
	}
	
	/**
	 * NOT LIKE '值%'
	 * @param key
	 * @param value
	 * @return
	 */
	public QueryWhere notLikeRight(String key,Object value){
		return this.where(key, SqlOperator.NOT_LIKE_RIGHT, value);
	}
	
	/**
	 * in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public  QueryWhere in(String key,Object value) {
		return this.where(key, SqlOperator.IN, value);
	}
	
	/**
	 * not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public  QueryWhere notin(String key,Object value) {
		return this.where(key, SqlOperator.NOT_IN, value);
	}
	
	/**
	 * not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public  QueryWhere notin(String key,Collection<?> values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public  QueryWhere in(String key,int[] values) {
		return this.where(key, SqlOperator.IN, values);
	}
	
	/**
	 * not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public  QueryWhere notin(String key,int[] values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public  QueryWhere in(String key,short[] values) {
		return this.where(key, SqlOperator.IN, values);
	}
	
	/**
	 * not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public  QueryWhere notin(String key,short[] values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public  QueryWhere in(String key,long[] values) {
		return this.where(key, SqlOperator.IN, values);
	}
	
	/**
	 * not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public  QueryWhere notin(String key,long[] values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public  QueryWhere in(String key,byte[] values) {
		return this.where(key, SqlOperator.IN, values);
	}
	
	/**
	 * not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public  QueryWhere notin(String key,byte[] values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public  QueryWhere in(String key,String[] values) {
		return this.where(key, SqlOperator.IN, values);
	}
	
	/**
	 * not in (值1，值2...)
	 * @param key
	 * @param values
	 * @return
	 */
	public  QueryWhere notin(String key,String[] values) {
		return this.where(key, SqlOperator.NOT_IN, values);
	}
	
	/**
	 * IS NULL
	 * @param key
	 * @return
	 */
	public QueryWhere isNull(String key){
		return this.where(key, SqlOperator.IS_NULL);
	}
	
	/**
	 * IS NOT NULL
	 * @param key
	 * @return
	 */
	public QueryWhere isNotNull(String key){
		return this.where(key, SqlOperator.IS_NOT_NULL);
	}
	
}
