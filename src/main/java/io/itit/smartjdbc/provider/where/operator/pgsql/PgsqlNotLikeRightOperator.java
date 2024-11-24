package io.itit.smartjdbc.provider.where.operator.pgsql;

import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.Where.Condition;
import io.itit.smartjdbc.provider.where.operator.ColumnOperator;
import io.itit.smartjdbc.provider.where.operator.OperatorContext;

/**
 * 
 * @author skydu
 *
 */
public class PgsqlNotLikeRightOperator extends ColumnOperator{

	@Override
	public String getOperatorSql(OperatorContext ctx) {
		return "not ilike";
	}

	@Override
	protected String getValueSql(OperatorContext ctx) {
		return "concat(?,'%')";
	}
	
	@Override
	public SqlBean build(OperatorContext ctx) {
		Condition c=ctx.getCondition();
		if(c.key==null) {
			return null;
		}
		StringBuilder sql=new StringBuilder();
		String columnSql=getColumnSql(ctx);
		sql.append("(");
		sql.append(columnSql);
		sql.append(" ");
		sql.append(getOperatorSql(ctx));
		sql.append(" ");
		sql.append(getValueSql(ctx));
		sql.append(" ");
		sql.append(" or ");
		sql.append(getColumnSql(ctx));
		sql.append(" is null");
		sql.append(")");
		ctx.addParameter(c.value);
		return SqlBean.build(sql.toString());
	}
}
