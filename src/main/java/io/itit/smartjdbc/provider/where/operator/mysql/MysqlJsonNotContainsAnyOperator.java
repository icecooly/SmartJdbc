package io.itit.smartjdbc.provider.where.operator.mysql;

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
public class MysqlJsonNotContainsAnyOperator extends ColumnOperator {

	@Override
	public SqlBean build(OperatorContext ctx) {
		Condition c=ctx.getCondition();
		String column = c.key;
		Object value = c.value;
		if (column == null || value == null) {
			return null;
		}
		Object[] values = ArrayUtils.convert(value);
		if (values == null || values.length == 0) {
			return null;
		}
		JsonConfig jsonConfig=c.jsonConfig;
		StringBuilder sql = new StringBuilder();
		sql.append("( ");
		for (int i = 0; i < values.length; i++) {
			if(jsonConfig==null||jsonConfig.objectField==null) {
				sql.append(" json_contains(").append(getColumnSql(ctx)).append(",JSON_ARRAY(?)").append(") ");
			}else {
				sql.append(" json_contains(").append(getColumnSql(ctx)).append(",JSON_OBJECT('"+jsonConfig.objectField+"',?)").append(") ");
			}
			sql.append("=0 ");
			ctx.addParameter(values[i]);
			if (i != (values.length - 1)) {
				sql.append(" and ");
			}
		}
		sql.append(") ");
		return SqlBean.build(sql.toString());
	}

}
