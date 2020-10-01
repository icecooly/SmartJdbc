package io.itit.smartjdbc.provider.where.operator;

/**
 * 
 * @author skydu
 *
 */
public class LtOperator extends FieldOperator{

	public LtOperator(OperatorContext ctx) {
		super(ctx);
	}

	@Override
	public String getOperatorSql() {
		return "<";
	}

}
