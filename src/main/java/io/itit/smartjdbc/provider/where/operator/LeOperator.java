package io.itit.smartjdbc.provider.where.operator;

/**
 * 
 * @author skydu
 *
 */
public class LeOperator extends FieldOperator{

	@Override
	public String getOperatorSql(OperatorContext ctx) {
		return "<=";
	}

}
