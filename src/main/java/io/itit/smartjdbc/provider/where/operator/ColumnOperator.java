package io.itit.smartjdbc.provider.where.operator;

import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.Where.Condition;

/**
 * 
 * @author skydu
 *
 */
public abstract class ColumnOperator implements IOperator{
	
	/**
	 * 
	 */
	@Override
	public SqlBean build(OperatorContext ctx) {
		Condition c=ctx.getCondition();
		SqlBean bean=new SqlBean();
		if(c.key==null) {
			return null;
		}
		if(c.value==null) {
			return null;
		}
		StringBuilder sql=new StringBuilder();
		sql.append(getColumnSql(ctx));
		sql.append(" ");
		sql.append(getOperatorSql(ctx));
		sql.append(" ");
		if(c.formula) {
			sql.append(c.value.toString());
		}else {
			sql.append(getValueSql(ctx));
			sql.append(" ");
			ctx.addParameter(c.value);
		}
		
		bean.sql=sql.toString();
		return bean;
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getOperatorSql(OperatorContext ctx) {
		return "";
	}
	
	/**
	 * 
	 * @param ctx
	 * @return
	 */
	protected String getColumnSql(OperatorContext ctx) {
		StringBuilder sql=new StringBuilder();
		String identifier=ctx.identifier();
		Condition c=ctx.getCondition();
		if(c.isColumn) {
			if(c.alias!=null) {
				sql.append(c.alias).append(".");
			}
			sql.append(identifier);
			sql.append(c.key);
			sql.append(identifier);
		}else {
			sql.append(c.key);
		}
		if(c.keyFunc!=null) {
			String columnSql=sql.toString();
			sql=new StringBuilder();
			sql.append(c.keyFunc);
			sql.append("(");
			sql.append(columnSql);
			sql.append(") ");
		}
		if(c.keyCast!=null) {
			sql.append(c.keyCast);
		}
		return sql.toString();
	}

	/**
	 * 
	 * @return
	 */
	protected String getValueSql(OperatorContext ctx) {
		return "?";
	}
}
