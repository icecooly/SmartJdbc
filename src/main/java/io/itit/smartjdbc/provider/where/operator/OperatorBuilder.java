package io.itit.smartjdbc.provider.where.operator;

import io.itit.smartjdbc.enums.SqlOperator;

/**
 * 
 * @author skydu
 *
 */
public class OperatorBuilder {

	/*
	 * 
	 */
	public static IOperator build(SqlOperator operator, String alias, String column, Object value) {
		FieldOperator opt=null;
		if(operator.equals(SqlOperator.EQ)) {
			opt=new EqOperator();
		}
		if(operator.equals(SqlOperator.NE)) {
			opt=new NeOperator();
		}
		if(operator.equals(SqlOperator.IN)) {
			opt=new InOperator();
		}
		if(operator.equals(SqlOperator.NOT_IN)) {
			opt=new NotInOperator();
		}
		if(operator.equals(SqlOperator.LIKE)) {
			opt=new LikeOperator();
		}
		if(operator.equals(SqlOperator.NOT_LIKE)) {
			opt=new NotLikeOperator();
		}
		if(operator.equals(SqlOperator.LIKE_LEFT)) {
			opt=new LikeLeftOperator();
		}
		if(operator.equals(SqlOperator.LIKE_RIGHT)) {
			opt=new LikeRightOperator();
		}
		if(operator.equals(SqlOperator.NOT_LIKE_LEFT)) {
			opt=new NotLikeLeftOperator();
		}
		if(operator.equals(SqlOperator.NOT_LIKE_RIGHT)) {
			opt=new NotLikeRightOperator();
		}
		if(operator.equals(SqlOperator.GT)) {
			opt=new GtOperator();
		}
		if(operator.equals(SqlOperator.GE)) {
			opt=new GeOperator();
		}
		if(operator.equals(SqlOperator.LT)) {
			opt=new LtOperator();
		}
		if(operator.equals(SqlOperator.LE)) {
			opt=new LeOperator();
		}
		if(operator.equals(SqlOperator.IS_NULL)) {
			opt=new IsNullOperator();
		}
		if(operator.equals(SqlOperator.IS_NOT_NULL)) {
			opt=new IsNotNullOperator();
		}
		if(operator.equals(SqlOperator.JSONCONTAINS)) {
			opt=new JsonContainsAnyOperator();
		}
		if(operator.equals(SqlOperator.NOT_JSONCONTAINS)) {
			opt=new JsonNotContainsAnyOperator();
		}
		opt.setAlias(alias);
		opt.setColumn(column);
		opt.setValue(value);
		return opt;
	}
}
