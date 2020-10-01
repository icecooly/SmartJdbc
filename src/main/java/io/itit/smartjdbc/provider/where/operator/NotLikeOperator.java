package io.itit.smartjdbc.provider.where.operator;

/**
 * 
 * @author skydu
 *
 */
public class NotLikeOperator extends FieldOperator{

	public NotLikeOperator(OperatorContext ctx) {
		super(ctx);
	}

	@Override
	public String getOperatorSql() {
		return "like";
	}
	
	@Override
	protected String getValueSql() {
		return "concat('%',?,'%')";
	}
	

}
