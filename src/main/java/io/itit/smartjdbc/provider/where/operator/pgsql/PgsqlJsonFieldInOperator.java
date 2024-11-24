package io.itit.smartjdbc.provider.where.operator.pgsql;

import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.Where.Condition;
import io.itit.smartjdbc.provider.where.Where.JsonConfig;
import io.itit.smartjdbc.provider.where.operator.ColumnOperator;
import io.itit.smartjdbc.provider.where.operator.OperatorContext;
import io.itit.smartjdbc.util.ArrayUtils;

/**
 * 
 * @author liutao
 *
 */
public class PgsqlJsonFieldInOperator extends ColumnOperator {

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
		if(jsonConfig.isArray) {
			sql.append(" exists(");
			sql.append("select 1 from jsonb_array_elements(");
			sql.append(getColumnSql(ctx));
			sql.append(" ) item where item::jsonb->>'").append(jsonConfig.objectField).append("' ");
			sql.append(" in (");
			for (int i = 0; i < values.length; i++) {
				sql.append(" ?,");
				ctx.addParameter(values[i]);
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(")) ");
		} else {
			sql.append(getColumnSql(ctx)).append("->>'")
					.append(jsonConfig.objectField)
					.append("' in");
			for (int i = 0; i < values.length; i++) {
				sql.append(" ?,");
				ctx.addParameter(values[i]);
			}
			sql.deleteCharAt(sql.length() - 1);
		}
		return SqlBean.build(sql.toString());
	}

}
