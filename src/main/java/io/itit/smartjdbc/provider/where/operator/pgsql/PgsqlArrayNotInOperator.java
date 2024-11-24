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
public class PgsqlArrayNotInOperator extends ColumnOperator {

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
		//
		sql.append("not ( ");
		String columnSql=getColumnSql(ctx);
		sql.append(columnSql).append("is not null ");
		sql.append("and ");
		sql.append(columnSql);
		sql.append("<@ ?");
		ctx.addParameter(values);
		sql.append(") ");
		return SqlBean.build(sql.toString());
	}

	

}
