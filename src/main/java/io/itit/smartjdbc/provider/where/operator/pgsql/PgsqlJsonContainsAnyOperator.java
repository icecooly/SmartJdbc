package io.itit.smartjdbc.provider.where.operator.pgsql;

import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.Where.Condition;
import io.itit.smartjdbc.provider.where.Where.JsonConfig;
import io.itit.smartjdbc.provider.where.operator.OperatorContext;
import io.itit.smartjdbc.util.ArrayUtils;

/**
 * 
 * @author skydu
 *
 */
public class PgsqlJsonContainsAnyOperator extends PgsqlColumnOperator {

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
		
		sql.append("( ");
		String columnSql=getColumnSql(ctx);
		for (int i = 0; i < values.length; i++) {
			Object v=values[i];
			String doubleQuotesV=addDoubleQuotes(v.toString());
			sql.append(columnSql).append("@> ?::jsonb ");
			if(jsonConfig==null||!jsonConfig.isArray) {
				if(jsonConfig==null||jsonConfig.objectField==null) {
					if(v instanceof String) {
						ctx.addParameter(doubleQuotesV);
					} else {
						ctx.addParameter(v);
					}
				}else {
					ctx.addParameter("{\""+jsonConfig.objectField+"\":" + doubleQuotesV +"}");
				}
			}else {//Array
				ctx.addParameter("[{\""+jsonConfig.objectField+"\":" + doubleQuotesV + "}]");
			}
			if (i != (values.length - 1)) {
				sql.append(" or ");
			}
		}
		sql.append(") ");
		return SqlBean.build(sql.toString());
	}

}
