package io.itit.smartjdbc.provider.where.operator;

/**
 * 
 * @author skydu
 *
 */
public class GeOperator extends FieldOperator{

	@Override
	public String getOperatorSql(OperatorContext ctx) {
		return ">=";
	}

}
