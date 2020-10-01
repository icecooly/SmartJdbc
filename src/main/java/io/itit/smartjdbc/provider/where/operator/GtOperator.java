package io.itit.smartjdbc.provider.where.operator;

/**
 * 
 * @author skydu
 *
 */
public class GtOperator extends FieldOperator{

	public GtOperator(OperatorContext ctx) {
		super(ctx);
	}

	@Override
	public String getOperatorSql() {
		return ">";
	}

}
