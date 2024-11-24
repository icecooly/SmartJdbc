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
public class PgsqlArrayIsNotEmptyOperator extends ColumnOperator {

	@Override
	public String getOperatorSql(OperatorContext ctx) {
		return "";
	}

	@Override
	public SqlBean build(OperatorContext ctx) {
		Condition c=ctx.getCondition();
		String column = c.key;
		if (column == null) {
			return null;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("not( ");
		sql.append(getColumnSql(ctx)).append(" is null or ");
		sql.append("array_length(").append(getColumnSql(ctx)).append(",1) is null");
		sql.append(") ");
		return SqlBean.build(sql.toString());
	}

}
