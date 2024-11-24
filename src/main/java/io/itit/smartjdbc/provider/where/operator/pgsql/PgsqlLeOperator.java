package io.itit.smartjdbc.provider.where.operator.pgsql;

import io.itit.smartjdbc.provider.where.operator.ColumnOperator;
import io.itit.smartjdbc.provider.where.operator.OperatorContext;

/**
 * 
 * @author skydu
 *
 */
public class PgsqlLeOperator extends ColumnOperator{

	@Override
	public String getOperatorSql(OperatorContext ctx) {
		return "<=";
	}

}
