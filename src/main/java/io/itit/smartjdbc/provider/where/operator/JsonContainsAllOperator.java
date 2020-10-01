package io.itit.smartjdbc.provider.where.operator;

import io.itit.smartjdbc.enums.DatabaseType;
import io.itit.smartjdbc.util.ArrayUtils;

/**
 * 
 * @author skydu
 *
 */
public class JsonContainsAllOperator extends FieldOperator {

	public JsonContainsAllOperator(OperatorContext ctx) {
		super(ctx);
	}

	@Override
	public String getOperatorSql() {
		return "";
	}

	@Override
	public String build() {
		DatabaseType type = ctx.getSmartDataSource().getDatabaseType();
		String column = where.key;
		Object value = where.value;
		if (column == null || value == null) {
			return "";
		}
		Object[] values = ArrayUtils.convert(value);
		if(values==null||values.length==0) {
			return "";
		}
		StringBuilder sql = new StringBuilder();
		if (type.equals(DatabaseType.MYSQL)) {
			sql.append("( ");
			for (int i = 0; i < values.length; i++) {
				sql.append(" json_contains(").append(getFieldSql()).append(",JSON_ARRAY(?)").append(") ");
				ctx.addParameter(values[i]);
				if (i != (values.length - 1)) {
					sql.append(" and ");
				}
			}
			sql.append(") ");
		}
		if (type.equals(DatabaseType.POSTGRESQL)) {
			sql.append("( ");
			for (int i = 0; i < values.length; i++) {
				Object v=values[i];
				if(v instanceof String) {
					sql.append(getFieldSql()).append("::jsonb@>'\""+values[i]+"\"'");
				}else {
					sql.append(getFieldSql()).append("::jsonb@>'"+values[i]+"'");
				}
				if (i != (values.length - 1)) {
					sql.append(" and ");
				}
			}
			sql.append(") ");
		}
		return sql.toString();
	}

}
