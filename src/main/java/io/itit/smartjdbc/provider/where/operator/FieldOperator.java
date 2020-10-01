package io.itit.smartjdbc.provider.where.operator;

import io.itit.smartjdbc.SmartDataSource;

/**
 * 
 * @author skydu
 *
 */
public abstract class FieldOperator implements IOperator{

	private String alias;
	
	private String column;
	
	private Object value;
	//

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	/**
	 * @return the column
	 */
	public String getColumn() {
		return column;
	}

	/**
	 * @param column the column to set
	 */
	public void setColumn(String column) {
		this.column = column;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	
	/**
	 * 
	 * @return
	 */
	public abstract String getOperatorSql(OperatorContext ctx);
	
	/**
	 * 
	 */
	@Override
	public String build(OperatorContext ctx) {
		if(column==null) {
			return "";
		}
		StringBuilder sql=new StringBuilder();
		sql.append(getFieldSql(ctx));
		sql.append(" ");
		sql.append(getOperatorSql(ctx));
		sql.append(" ");
		sql.append(getValueSql(ctx));
		sql.append(" ");
		if(value!=null) {
			ctx.addParameter(value);
		}
		return sql.toString();
	}
	
	/**
	 * 
	 * @param ctx
	 * @return
	 */
	protected String getFieldSql(OperatorContext ctx) {
		StringBuilder sql=new StringBuilder();
		SmartDataSource smartDataSource=ctx.getSmartDataSource();
		String identifier=smartDataSource.identifier();
		if(alias!=null) {
			sql.append(alias).append(".");
		}
		sql.append(identifier);
		sql.append(column);
		sql.append(identifier);
		return sql.toString();
	}

	/**
	 * 
	 * @param ctx
	 * @return
	 */
	protected String getValueSql(OperatorContext ctx) {
		return "?";
	}
}
