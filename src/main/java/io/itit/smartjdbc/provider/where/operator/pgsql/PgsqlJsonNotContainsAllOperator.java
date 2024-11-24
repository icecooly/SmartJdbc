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
public class PgsqlJsonNotContainsAllOperator extends PgsqlColumnOperator {

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
		sql.append("not ( ");
		String columnSql=getColumnSql(ctx);
		sql.append(columnSql).append(" is not null ");
		sql.append(" and ");
		if(jsonConfig==null||jsonConfig.objectField==null) {
			sql.append(columnSql).append("->>0 in (");
			for (int i = 0; i < values.length; i++) {
				sql.append("?,");
				ctx.addParameter(values[i]);
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(")");
		}else if(!jsonConfig.isArray){//{"id":"xxx1","name":"xxxx"}
			sql.append(columnSql).append("->>'"+jsonConfig.objectField+"' in (");
			for (int i = 0; i < values.length; i++) {
				sql.append("?,");
				ctx.addParameter(values[i]);
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(")");
		}else {//array
			sql.append("jsonb_id_to_array(").append(columnSql).append(") ").append("<@ ?::jsonb ");
			StringBuilder v=new StringBuilder();
			v.append("[");
			for (Object item : values) {
				v.append(addDoubleQuotes(item.toString()));
				v.append(",");
			}
			v.deleteCharAt(v.length()-1);
			v.append("]");
			ctx.addParameter(v.toString());
		}
		sql.append(") ");
		return SqlBean.build(sql.toString());
	}

}
