package io.itit.smartjdbc.provider.where.operator;

/**
 * 
 * @author skydu
 *
 */
public class NotLikeRightOperator extends FieldOperator{

	@Override
	public String getOperatorSql(OperatorContext ctx) {
		return "not like";
	}

	@Override
	protected String getValueSql(OperatorContext ctx) {
		return "concat(?,'%')";
	}
}
