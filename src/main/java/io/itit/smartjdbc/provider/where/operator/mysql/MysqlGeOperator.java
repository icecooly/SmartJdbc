package io.itit.smartjdbc.provider.where.operator.mysql;

import io.itit.smartjdbc.provider.where.operator.ColumnOperator;
import io.itit.smartjdbc.provider.where.operator.OperatorContext;

/**
 * 
 * @author skydu
 *
 */
public class MysqlGeOperator extends ColumnOperator{

	@Override
	public String getOperatorSql(OperatorContext ctx) {
		return ">=";
	}
}
