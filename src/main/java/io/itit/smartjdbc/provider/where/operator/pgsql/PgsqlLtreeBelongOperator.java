package io.itit.smartjdbc.provider.where.operator.pgsql;

import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.Where.Condition;
import io.itit.smartjdbc.provider.where.operator.ColumnOperator;
import io.itit.smartjdbc.provider.where.operator.OperatorContext;

/**
 * 
 * @author skydu
 *
 */
public class PgsqlLtreeBelongOperator extends ColumnOperator {

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
		StringBuilder sql = new StringBuilder();
		ctx.addParameter(value);
		String columnSql=getColumnSql(ctx);
		sql.append(columnSql).append("<@").append("?");
		return SqlBean.build(sql.toString());
	}

}
