package io.itit.smartjdbc.provider.where.operator;

import io.itit.smartjdbc.provider.where.Where;

/**
 * 
 * @author skydu
 *
 */
public abstract class Operator {
	//
	protected OperatorContext ctx;
	protected Where where;
	//
	public Operator(OperatorContext ctx) {
		this.ctx=ctx;
		this.where=ctx.getWhere();
	}
	//
	public abstract String build();
	//
	/**
	 * @return the where
	 */
	public Where getWhere() {
		return where;
	}
	/**
	 * @param where the where to set
	 */
	public void setWhere(Where where) {
		this.where = where;
	}
	/**
	 * @return the ctx
	 */
	public OperatorContext getCtx() {
		return ctx;
	}
	/**
	 * @param ctx the ctx to set
	 */
	public void setCtx(OperatorContext ctx) {
		this.ctx = ctx;
	}
	
}
