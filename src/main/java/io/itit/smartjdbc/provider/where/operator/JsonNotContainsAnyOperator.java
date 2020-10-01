package io.itit.smartjdbc.provider.where.operator;

import io.itit.smartjdbc.enums.DatabaseType;
import io.itit.smartjdbc.util.ArrayUtils;

/**
 * 
 * @author skydu
 *
 */
public class JsonNotContainsAnyOperator extends FieldOperator {

	@Override
	public String getOperatorSql(OperatorContext ctx) {
		return "";
	}

	@Override
	public String build(OperatorContext ctx) {
		DatabaseType type = ctx.getSmartDataSource().getDatabaseType();
		String column = getColumn();
		Object value = getValue();
		if (column == null || value == null) {
			return "";
		}
		Object[] values = ArrayUtils.convert(value);
		if (values == null || values.length == 0) {
			return "";
		}
		StringBuilder sql = new StringBuilder();
		if (type.equals(DatabaseType.MYSQL)) {
			sql.append("( ");
			for (int i = 0; i < values.length; i++) {
				sql.append(" json_contains(").append(getFieldSql(ctx)).append(",JSON_ARRAY(?)").append(") ");
				sql.append("=0 ");
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
					sql.append(getFieldSql(ctx)).append("::jsonb@>'\""+values[i]+"\"'");
				}else {
					sql.append(getFieldSql(ctx)).append("::jsonb@>'"+values[i]+"'");
				}
				sql.append("='f' ");
				if (i != (values.length - 1)) {
					sql.append(" and ");
				}
			}
			sql.append(") ");
		}
		return sql.toString();
	}

}
