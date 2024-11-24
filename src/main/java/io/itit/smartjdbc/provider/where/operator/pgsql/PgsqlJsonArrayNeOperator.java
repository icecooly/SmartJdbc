package io.itit.smartjdbc.provider.where.operator.pgsql;

import java.util.List;

import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.Where.Condition;
import io.itit.smartjdbc.provider.where.operator.OperatorContext;
import io.itit.smartjdbc.util.JSONUtil;

/**
 * 
 * @author skydu
 *
 */
public class PgsqlJsonArrayNeOperator extends PgsqlColumnOperator {


	@Override
	public SqlBean build(OperatorContext ctx) {
		Condition c=ctx.getCondition();
		String column = c.key;
		@SuppressWarnings("unchecked")
		List<List<String>> values = (List<List<String>>) c.value;
		if (column == null || values == null || values.size()==0) {
			return null;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("not ( ");
		String columnSql=getColumnSql(ctx);
		sql.append(columnSql).append(" is not null ");
		sql.append(" and ");	
		sql.append(columnSql).append("@>?::jsonb");
		ctx.addParameter(JSONUtil.toJson(values));
		sql.append(" and ");
		sql.append(columnSql).append("<@?::jsonb");
		ctx.addParameter(JSONUtil.toJson(values));
		sql.append(") ");
		return SqlBean.build(sql.toString());
	}

}