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
public class PgsqlArrayNeOperator extends ColumnOperator {

	@Override
	public String getOperatorSql(OperatorContext ctx) {
		return "";
	}

	@Override
	public SqlBean build(OperatorContext ctx) {
		Condition c=ctx.getCondition();
		String column = c.key;
		Object value = c.value;
		if (column == null || value == null) {
			return null;
		}
		Object[] values = ArrayUtils.convert(value);
		if(value==null||values.length==0) {
			return null;
		}
		StringBuilder sql = new StringBuilder();
		//
		sql.append("( ");
		sql.append(getColumnSql(ctx)).append(" is null or ");
		sql.append(getColumnSql(ctx)).append("!=ARRAY[").append("");
		for (Object v : values) {
			sql.append("?").append(",");
			ctx.addParameter(v);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append("]::text[]");
		sql.append(") ");
		return SqlBean.build(sql.toString());
	}

}
