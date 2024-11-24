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
public class PgsqlArrayNotAnyOperator extends ColumnOperator {

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
		if(values==null||values.length==0) {
			return null;
		}
		StringBuilder sql = new StringBuilder();
		// 为空的数据 也不算不含
		sql.append("(");
		String columnSql=getColumnSql(ctx);
		sql.append(columnSql).append(" is  null or (");
		sql.append(" not ( ");
		for (int i = 0; i < values.length; i++) {
			Object v=values[i];
			ctx.addParameter(v);
			sql.append("?=any(").append(columnSql).append(")");
			if (i != (values.length - 1)) {
				sql.append(" or ");
			}
		}
		sql.append(") ");
		sql.append(") ");
		sql.append(") ");
		return SqlBean.build(sql.toString());
	}

}
