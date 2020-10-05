package io.itit.smartjdbc.provider.where.operator;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.provider.where.Where.Condition;

/**
 * 
 * @author skydu
 *
 */
public abstract class FieldOperator extends Operator{
	
	public FieldOperator(OperatorContext ctx) {
		super(ctx);
	}
	//
	/**
	 * 
	 * @return
	 */
	public abstract String getOperatorSql();
	/**
	 * 
	 */
	@Override
	public String build() {
		Condition c=getCtx().getCondition();
		if(c.key==null) {
			return "";
		}
		StringBuilder sql=new StringBuilder();
		sql.append(getFieldSql());
		sql.append(" ");
		sql.append(getOperatorSql());
		sql.append(" ");
		sql.append(getValueSql());
		sql.append(" ");
		if(c.value!=null) {
			ctx.addParameter(c.value);
		}
		return sql.toString();
	}
	
	/**
	 * 
	 * @param ctx
	 * @return
	 */
	protected String getFieldSql() {
		StringBuilder sql=new StringBuilder();
		SmartDataSource smartDataSource=ctx.getSmartDataSource();
		String identifier=smartDataSource.identifier();
		Condition c=getCtx().getCondition();
		if(c.alias!=null) {
			sql.append(c.alias).append(".");
		}
		if(c.isField) {
			sql.append(identifier);
			sql.append(c.key);
			sql.append(identifier);
		}else {
			sql.append(c.key);
		}
		return sql.toString();
	}

	/**
	 * 
	 * @param ctx
	 * @return
	 */
	protected String getValueSql() {
		return "?";
	}
}
