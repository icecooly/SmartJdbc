package io.itit.smartjdbc.provider.where.operator;

/**
 * 
 * @author skydu
 *
 */
public class NotLikeRightOperator extends FieldOperator{

	public NotLikeRightOperator(OperatorContext ctx) {
		super(ctx);
	}

	@Override
	public String getOperatorSql() {
		return "not like";
	}

	@Override
	protected String getValueSql() {
		return "concat(?,'%')";
	}
}
