package io.itit.smartjdbc.provider.where.operator.pgsql;

import io.itit.smartjdbc.provider.where.operator.ColumnOperator;
import io.itit.smartjdbc.provider.where.operator.OperatorContext;

/**
 * 
 * @author skydu
 *
 */
public class PgsqlLikeOperator extends ColumnOperator{


	@Override
	public String getOperatorSql(OperatorContext ctx) {
		return "ilike";
	}
	
	@Override
	protected String getValueSql(OperatorContext ctx) {
		return "concat('%',?,'%')";
	}
	

}
