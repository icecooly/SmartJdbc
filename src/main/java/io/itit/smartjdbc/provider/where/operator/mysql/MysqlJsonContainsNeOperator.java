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
public class MysqlJsonContainsNeOperator extends ColumnOperator {

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
		JsonConfig jsonConfig=c.jsonConfig;
		StringBuilder sql = new StringBuilder();
		sql.append("not( ");
		sql.append("json_length(").append(getColumnSql(ctx)).append(")="+values.length);
		for (int i = 0; i < values.length; i++) {
			sql.append(" and ");
			if(jsonConfig==null||jsonConfig.objectField==null) {
				sql.append(" json_contains(").append(getColumnSql(ctx)).append(",JSON_ARRAY(?)").append(") ");
			}else {
				sql.append(" json_contains(").append(getColumnSql(ctx)).append(",JSON_OBJECT('"+jsonConfig.objectField+"',?)").append(") ");
			}
			ctx.addParameter(values[i]);
		}
		sql.append(") ");
		return SqlBean.build(sql.toString());
	}

}
