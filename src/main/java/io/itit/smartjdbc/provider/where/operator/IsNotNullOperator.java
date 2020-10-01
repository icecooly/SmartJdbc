package io.itit.smartjdbc.provider.where.operator;

/**
 * 
 * @author skydu
 *
 */
public class IsNotNullOperator extends FieldOperator{

	public IsNotNullOperator(OperatorContext ctx) {
		super(ctx);
	}

	@Override
	public String getOperatorSql() {
		return "is not null";
	}
	
	@Override
	public String build() {
		String column=where.key;
		if(column==null) {
			return "";
		}
		StringBuilder sql=new StringBuilder();
		sql.append(getFieldSql());
		sql.append(" ");
		sql.append(getOperatorSql());
		sql.append(" ");
		return sql.toString();
	}

}
