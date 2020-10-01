package io.itit.smartjdbc.provider.where.operator;

import io.itit.smartjdbc.util.ArrayUtils;

/**
 * 
 * @author skydu
 *
 */
public class NotInOperator extends FieldOperator{

	@Override
	public String getOperatorSql(OperatorContext ctx) {
		return "not in";
	}

	@Override
	public String build(OperatorContext ctx) {
		String column=getColumn();
		Object value=getValue();
		if(column==null||value==null) {
			return "";
		}
		Object[] values=ArrayUtils.convert(value);
		if(values==null||values.length==0) {
			return "";
		}
		StringBuilder sql=new StringBuilder();
		sql.append(getFieldSql(ctx));
		sql.append(" ");
		sql.append(getOperatorSql(ctx));
		sql.append("( ");
		for (int i = 0; i < values.length; i++) {
			sql.append(" ?,");
			ctx.addParameter(values[i]);
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");
		sql.append(" ");
		return sql.toString();
	}
}
