package io.itit.smartjdbc.provider.where.operator.pgsql;

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
public class PgsqlBetweenAndOperator extends ColumnOperator{

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
		if(values[0]==null||values[1]==null) {//如果有一个为空,则不用between and
			if(values[0]!=null) {
				sql.append(" >= ? ");
				ctx.addParameter(values[0]);
			}
			if(values[1]!=null) {
				sql.append(" <= ? ");
				ctx.addParameter(values[1]);
			}
		}else {
			sql.append(" between ? and ? ");
			ctx.addParameter(values[0]);
			ctx.addParameter(values[1]);
		}
		return SqlBean.build(sql.toString());
	}


}
