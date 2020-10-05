package io.itit.smartjdbc.provider.where.operator;

import io.itit.smartjdbc.enums.DatabaseType;
import io.itit.smartjdbc.provider.where.Where.Condition;
import io.itit.smartjdbc.provider.where.Where.JsonContain;
import io.itit.smartjdbc.util.ArrayUtils;

/**
 * 
 * @author skydu
 *
 */
public class JsonContainsAnyOperator extends FieldOperator {

	public JsonContainsAnyOperator(OperatorContext ctx) {
		super(ctx);
	}

	@Override
	public String getOperatorSql() {
		return "";
	}

	@Override
	public String build() {
		Condition c=getCtx().getCondition();
		DatabaseType type = ctx.getSmartDataSource().getDatabaseType();
		String column = c.key;
		Object value = c.value;
		if (column == null || value == null) {
			return "";
		}
		Object[] values = ArrayUtils.convert(value);
		if(values==null||values.length==0) {
			return "";
		}
		JsonContain jsonContain=c.jsonContain;
		StringBuilder sql = new StringBuilder();
		if (type.equals(DatabaseType.MYSQL)) {
			sql.append("( ");
			for (int i = 0; i < values.length; i++) {
				if(jsonContain==null||jsonContain.objectField==null) {
					sql.append(" json_contains(").append(getFieldSql()).append(",JSON_ARRAY(?)").append(") ");
				}else {
					sql.append(" json_contains(").append(getFieldSql()).append(",JSON_OBJECT('"+jsonContain.objectField+"',?)").append(") ");
				}
				ctx.addParameter(values[i]);
				if (i != (values.length - 1)) {
					sql.append(" or ");
				}
			}
			sql.append(") ");
		}
		if (type.equals(DatabaseType.POSTGRESQL)) {
			sql.append("( ");
			for (int i = 0; i < values.length; i++) {
				Object v=values[i];
				if(jsonContain==null||jsonContain.objectField==null) {
					if(v instanceof String) {
						sql.append(getFieldSql()).append("::jsonb@>'\""+values[i]+"\"'");
					}else {
						sql.append(getFieldSql()).append("::jsonb@>'"+values[i]+"'");
					}
				}else {
					sql.append(getFieldSql()).append("::jsonb@>'[{\""+jsonContain.objectField+"\":\""+values[i]+"\"}]'");
				}
				if (i != (values.length - 1)) {
					sql.append(" or ");
				}
			}
			sql.append(") ");
		}
		return sql.toString();
	}

}
