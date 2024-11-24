package io.itit.smartjdbc.provider.where.operator.mysql;

import java.util.HashMap;
import java.util.Map;

import io.itit.smartjdbc.enums.SqlOperator;
import io.itit.smartjdbc.provider.where.Where.Condition;
import io.itit.smartjdbc.provider.where.operator.IOperator;
import io.itit.smartjdbc.provider.where.operator.IOperatorFactory;
import io.itit.smartjdbc.provider.where.operator.OperatorContext;

/**
 * 
 * @author skydu
 *
 */
public class MysqlOperatorFactory implements IOperatorFactory{

	private static final Map<SqlOperator, IOperator> operators=new HashMap<>();
	static {
		operators.put(SqlOperator.EQ, new MysqlEqOperator());
		operators.put(SqlOperator.NE, new MysqlNeOperator());
		operators.put(SqlOperator.IN, new MysqlInOperator());
		operators.put(SqlOperator.NOT_IN, new MysqlNotInOperator());
		operators.put(SqlOperator.LIKE, new MysqlLikeOperator());
		operators.put(SqlOperator.NOT_LIKE, new MysqlNotLikeOperator());
		operators.put(SqlOperator.LIKE_LEFT, new MysqlLikeLeftOperator());
		operators.put(SqlOperator.LIKE_RIGHT, new MysqlLikeRightOperator());
		operators.put(SqlOperator.NOT_LIKE_LEFT, new MysqlNotLikeLeftOperator());
		operators.put(SqlOperator.NOT_LIKE_RIGHT, new MysqlNotLikeRightOperator());
		operators.put(SqlOperator.GT, new MysqlGtOperator());
		operators.put(SqlOperator.GE, new MysqlGeOperator());
		operators.put(SqlOperator.LT, new MysqlLtOperator());
		operators.put(SqlOperator.LE, new MysqlLeOperator());
		operators.put(SqlOperator.IS_NULL, new MysqlIsNullOperator());
		operators.put(SqlOperator.IS_NOT_NULL, new MysqlIsNotNullOperator());
		operators.put(SqlOperator.JSON_CONTAINS_ANY, new MysqlJsonContainsAnyOperator());
		operators.put(SqlOperator.JSON_NOT_CONTAINS_ANY, new MysqlJsonNotContainsAnyOperator());
		operators.put(SqlOperator.JSON_CONTAINS_ALL, new MysqlJsonContainsAllOperator());
		operators.put(SqlOperator.JSON_EQ, new MysqlJsonContainsEqOperator());
		operators.put(SqlOperator.JSON_NE, new MysqlJsonContainsNeOperator());
		operators.put(SqlOperator.BETWEEN_AND, new MysqlBetweenAndOperator());
		operators.put(SqlOperator.NOT_BETWEEN_AND, new MysqlNotBetweenAndOperator());
	}
	
	@Override
	public IOperator create(OperatorContext ctx) {
		Condition w=ctx.getCondition();
		SqlOperator operator=w.operator;
		if(operator==null) {
			operator=SqlOperator.EQ;//default
		}
		return operators.get(operator);
	}

}
