package io.itit.smartjdbc.provider.entity;

/**
 * 
 * @author skydu
 *
 */
public class Aggregation {
	/** 聚合函数 count,avg,sum,max,min */
	public String func;// AVG([DISTINCT] expr)
	/** 可能是公式 */
	public String expr;
	/** 是否去重 */
	public boolean distinct;
	/***/
	public String asName;
	//
	/**
	 * @return the func
	 */
	public String getFunc() {
		return func;
	}
	/**
	 * @param func the func to set
	 */
	public void setFunc(String func) {
		this.func = func;
	}
	/**
	 * @return the expr
	 */
	public String getExpr() {
		return expr;
	}
	/**
	 * @param expr the expr to set
	 */
	public void setExpr(String expr) {
		this.expr = expr;
	}
	/**
	 * @return the distinct
	 */
	public boolean isDistinct() {
		return distinct;
	}
	/**
	 * @param distinct the distinct to set
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}
	/**
	 * @return the asName
	 */
	public String getAsName() {
		return asName;
	}
	/**
	 * @param asName the asName to set
	 */
	public void setAsName(String asName) {
		this.asName = asName;
	}
	
}