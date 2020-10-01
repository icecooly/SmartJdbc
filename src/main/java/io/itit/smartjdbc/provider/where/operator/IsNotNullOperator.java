package io.itit.smartjdbc.provider.where.operator;

/**
 * 
 * @author skydu
 *
 */
public class IsNotNullOperator extends FieldOperator{

	@Override
	public String getOperatorSql(OperatorContext ctx) {
		return "is not null";
	}
	
	@Override
	public String build(OperatorContext ctx) {
		String column=getColumn();
		if(column==null) {
			return "";
		}
		StringBuilder sql=new StringBuilder();
		sql.append(getFieldSql(ctx));
		sql.append(" ");
		sql.append(getOperatorSql(ctx));
		sql.append(" ");
		return sql.toString();
	}

}
