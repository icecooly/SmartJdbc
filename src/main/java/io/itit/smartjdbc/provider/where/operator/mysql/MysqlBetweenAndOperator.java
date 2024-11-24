package io.itit.smartjdbc.provider.where.operator.mysql;

import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.Where.Condition;
import io.itit.smartjdbc.provider.where.operator.ColumnOperator;
import io.itit.smartjdbc.provider.where.operator.OperatorContext;
import io.itit.smartjdbc.util.ArrayUtils;

/**
 * 
 * @author skydu
 *
 */
public class MysqlBetweenAndOperator extends ColumnOperator{

	@Override
	public String getOperatorSql(OperatorContext ctx) {
		return "";
	}

	@Override
	public SqlBean build(OperatorContext ctx) {
		Condition c=ctx.getCondition();
		String column = c.key;
		Object value = c.value;
		if (column == null || value == null) {
			return null;
		}
		Object[] values = ArrayUtils.convert(value);
		if(value==null||values.length!=2) {
			return null;
		}
		StringBuilder sql = new StringBuilder();
		//
		sql.append(getColumnSql(ctx));
		sql.append(" between ? and ? ");
		ctx.addParameter(values[0]);
		ctx.addParameter(values[1]);
		return SqlBean.build(sql.toString());
	}


}
