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
public class PgsqlJsonArrayIsNotEmptyOperator extends ColumnOperator{
	//
	
	@Override
	public SqlBean build(OperatorContext ctx) {
		Condition c=ctx.getCondition();
		String column=c.key;
		if(column==null) {
			return null;
		}
		StringBuilder sql=new StringBuilder();
		sql.append("(");
		sql.append(getColumnSql(ctx));
		sql.append(" is not null and jsonb_array_length(");
		sql.append(getColumnSql(ctx));
		sql.append(") > 0 ");
		sql.append(")");
		return SqlBean.build(sql.toString());
	}

}
