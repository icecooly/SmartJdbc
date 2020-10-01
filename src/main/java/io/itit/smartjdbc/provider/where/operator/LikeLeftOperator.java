package io.itit.smartjdbc.provider.where.operator;

/**
 * 
 * @author skydu
 *
 */
public class LikeLeftOperator extends FieldOperator{

	@Override
	public String getOperatorSql(OperatorContext ctx) {
		return "like";
	}

	@Override
	protected String getValueSql(OperatorContext ctx) {
		return " concat('%',?) ";
	}
}
