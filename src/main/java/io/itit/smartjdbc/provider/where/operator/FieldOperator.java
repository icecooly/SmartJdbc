package io.itit.smartjdbc.provider.where.operator;

import io.itit.smartjdbc.SmartDataSource;

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
		if(where.key==null) {
			return "";
		}
		StringBuilder sql=new StringBuilder();
		sql.append(getFieldSql());
		sql.append(" ");
		sql.append(getOperatorSql());
		sql.append(" ");
		sql.append(getValueSql());
		sql.append(" ");
		if(where.value!=null) {
			ctx.addParameter(where.value);
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
		if(where.alias!=null) {
			sql.append(where.alias).append(".");
		}
		sql.append(identifier);
		sql.append(where.key);
		sql.append(identifier);
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
