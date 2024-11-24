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
public class PgsqlJsonEqOperator extends PgsqlColumnOperator {


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
		sql.append(columnSql).append(" is not null ");
		if(jsonConfig!=null&&jsonConfig.isArray) {
			sql.append(" and jsonb_array_length(").append(columnSql).append(")="+values.length);
		}
		for (int i = 0; i < values.length; i++) {
			sql.append(" and ");
			Object v=values[i];
			sql.append(columnSql).append("@>?::jsonb");
			if(jsonConfig==null||!jsonConfig.isArray) {
				if(jsonConfig==null||jsonConfig.objectField==null) {
					if(v instanceof String) {
						ctx.addParameter(addDoubleQuotes(v.toString()));
					}else {//int
						ctx.addParameter(v.toString());
					}
				}else {
					ctx.addParameter("{\""+jsonConfig.objectField+"\":\""+v.toString()+"\"}");
				}
			}else {
				if(jsonConfig.objectField==null) {//List<String>
					if(v instanceof String) {
						ctx.addParameter(addDoubleQuotes(v.toString()));
					}else {
						ctx.addParameter(v.toString());
					}
				}else {
					ctx.addParameter("[{\""+jsonConfig.objectField+"\":\""+v.toString()+"\"}]");
				}
			}
		}
		sql.append(") ");
		return SqlBean.build(sql.toString());
	}

}
