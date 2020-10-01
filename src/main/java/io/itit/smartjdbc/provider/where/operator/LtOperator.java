package io.itit.smartjdbc.provider.where.operator;

/**
 * 
 * @author skydu
 *
 */
public class LtOperator extends FieldOperator{

	@Override
	public String getOperatorSql(OperatorContext ctx) {
		return "<";
	}

}
