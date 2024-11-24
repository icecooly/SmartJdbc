package io.itit.smartjdbc.provider.where.operator.mysql;

import io.itit.smartjdbc.provider.where.operator.ColumnOperator;
import io.itit.smartjdbc.provider.where.operator.OperatorContext;

/**
 * 
 * @author skydu
 *
 */
public class MysqlLikeLeftOperator extends ColumnOperator{

	
	@Override
	public String getOperatorSql(OperatorContext ctx) {
		return "like";
	}

	@Override
	protected String getValueSql(OperatorContext ctx) {
		return " concat('%',?) ";
	}
}