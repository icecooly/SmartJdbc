package io.itit.smartjdbc.provider.where.operator.pgsql;

import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.Where.Condition;
import io.itit.smartjdbc.provider.where.operator.ColumnOperator;
import io.itit.smartjdbc.provider.where.operator.OperatorContext;
import io.itit.smartjdbc.util.ArrayUtils;

/**
 * 
 * @author skydu
 *
 */
public class PgsqlInOperator extends ColumnOperator{

	@Override
	public String getOperatorSql(OperatorContext ctx) {
		return "in";
	}

	@Override
	public SqlBean build(OperatorContext ctx) {
		Condition c=ctx.getCondition();
		String column=c.key;
		Object value=c.value;
		if(column==null||value==null) {
			return null;
		}
		Object[] values=ArrayUtils.convert(value);
		if(values==null||values.length==0) {
			return null;
		}
		StringBuilder sql=new StringBuilder();
		sql.append(getColumnSql(ctx));
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
		return SqlBean.build(sql.toString());
	}
}
