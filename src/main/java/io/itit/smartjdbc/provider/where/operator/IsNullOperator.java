package io.itit.smartjdbc.provider.where.operator;

import io.itit.smartjdbc.provider.where.Where.Condition;

/**
 * 
 * @author skydu
 *
 */
public class IsNullOperator extends FieldOperator{
	//
	public IsNullOperator(OperatorContext ctx) {
		super(ctx);
	}

	@Override
	public String getOperatorSql() {
		return "is null";
	}
	
	@Override
	public String build() {
		Condition c=getCtx().getCondition();
		String column=c.key;
		if(column==null) {
			return "";
		}
		StringBuilder sql=new StringBuilder();
		sql.append(getFieldSql());
		sql.append(" ");
		sql.append(getOperatorSql());
		sql.append(" ");
		return sql.toString();
	}

}
