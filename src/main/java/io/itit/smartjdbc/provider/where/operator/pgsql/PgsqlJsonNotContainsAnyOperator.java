package io.itit.smartjdbc.provider.where.operator.pgsql;

import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.Where.Condition;
import io.itit.smartjdbc.provider.where.Where.JsonConfig;
import io.itit.smartjdbc.provider.where.operator.ColumnOperator;
import io.itit.smartjdbc.provider.where.operator.OperatorContext;
import io.itit.smartjdbc.util.ArrayUtils;

/**
 * 
 * @author skydu
 *
 */
public class PgsqlJsonNotContainsAnyOperator extends ColumnOperator {


	@Override
	public SqlBean build(OperatorContext ctx) {
		Condition c=ctx.getCondition();
		String column = c.key;
		Object value = c.value;
		if (column == null || value == null) {
			return null;
		}
		Object[] values = ArrayUtils.convert(value);
		if(ArrayUtils.isEmpty(values)) {
			return null;
		}
		JsonConfig jsonConfig=c.jsonConfig;
		StringBuilder sql = new StringBuilder();
		sql.append("( ");
		// 为空的数据 也不算不含
		sql.append(getColumnSql(ctx)).append(" IS  NULL OR (");
		for (int i = 0; i < values.length; i++) {
			Object v=values[i];
			if(v instanceof String) {
				v = "\"" + values[i] + "\"";
			}
			if(jsonConfig==null||jsonConfig.objectField==null) {
				if(v instanceof String) {
					sql.append(getColumnSql(ctx)).append(" @> '" + v +  "'");
				} else {
					sql.append(getColumnSql(ctx)).append( "@> ? ");
					ctx.addParameter(v);
				}
			}else {
				if (jsonConfig.isArray) {
					sql.append(getColumnSql(ctx)).append(" @>'[{\""+jsonConfig.objectField+"\": " + v + "}]'");
				} else {
					sql.append(getColumnSql(ctx)).append(" @>'{\""+jsonConfig.objectField+"\": " + v + "}'");
				}
			}
			sql.append("='f' ");
			if (i != (values.length - 1)) {
				sql.append(" and ");
			}
		}
		sql.append(")) ");
		return SqlBean.build(sql.toString());
	}

}
