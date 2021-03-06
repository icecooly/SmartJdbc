package io.itit.smartjdbc.provider.where.operator;

import io.itit.smartjdbc.enums.DatabaseType;
import io.itit.smartjdbc.provider.where.Where.Condition;
import io.itit.smartjdbc.util.ArrayUtils;

/**
 * 
 * @author skydu
 *
 */
public class ArrayContainsOperator extends ColumnOperator {

	public ArrayContainsOperator(OperatorContext ctx) {
		super(ctx);
	}

	@Override
	public String getOperatorSql() {
		return "";
	}

	@Override
	public String build() {
		Condition c=getCtx().getCondition();
		DatabaseType type = ctx.getDatabaseType();
		String column = c.key;
		Object value = c.value;
		if (column == null || value == null) {
			return "";
		}
		Object[] values = ArrayUtils.convert(value);
		if(value==null||values.length==0) {
			return "";
		}
		StringBuilder sql = new StringBuilder();
		//
		if (type.equals(DatabaseType.POSTGRESQL)||
				type.equals(DatabaseType.KINGBASE)) {
			sql.append("( ");
			ctx.addParameter(value);
			sql.append(getColumnSql()).append("@>").append("?");
			sql.append(") ");
		}
		return sql.toString();
	}

}
