package io.itit.smartjdbc.provider.where.operator.mysql;

import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.Where.Condition;
import io.itit.smartjdbc.provider.where.operator.ColumnOperator;
import io.itit.smartjdbc.provider.where.operator.OperatorContext;

/**
 * 
 * @author skydu
 *
 */
public class MysqlIsNullOperator extends ColumnOperator{
	//
	@Override
	public String getOperatorSql(OperatorContext ctx) {
		return "is null";
	}
	
	@Override
	public SqlBean build(OperatorContext ctx) {
		Condition c=ctx.getCondition();
		String column=c.key;
		if(column==null) {
			return null;
		}
		StringBuilder sql=new StringBuilder();
		sql.append(getColumnSql(ctx));
		sql.append(" ");
		sql.append(getOperatorSql(ctx));
		sql.append(" ");
		return SqlBean.build(sql.toString());
	}

}
