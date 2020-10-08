package io.itit.smartjdbc.provider.where.operator;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.provider.where.Where.Condition;

/**
 * 
 * @author skydu
 *
 */
public abstract class ColumnOperator extends Operator{
	
	public ColumnOperator(OperatorContext ctx) {
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
		sql.append(getColumnSql());
		sql.append(" ");
		sql.append(getOperatorSql());
		sql.append(" ");
		sql.append(getValueSql());
		sql.append(" ");
		ctx.addParameter(c.value);
		return sql.toString();
	}
	
	/**
	 * 
	 * @param ctx
	 * @return
	 */
	protected String getColumnSql() {
		StringBuilder sql=new StringBuilder();
		SmartDataSource smartDataSource=ctx.getSmartDataSource();
		String identifier=smartDataSource.identifier();
		Condition c=getCtx().getCondition();
		if(c.isColumn) {
			if(c.alias!=null) {
				sql.append(c.alias).append(".");
			}
			sql.append(identifier);
			sql.append(c.key);
			sql.append(identifier);
			if(c.keyCast!=null) {
				sql.append(c.keyCast);
			}
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
