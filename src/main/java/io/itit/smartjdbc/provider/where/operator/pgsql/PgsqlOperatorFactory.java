package io.itit.smartjdbc.provider.where.operator.pgsql;

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
public class PgsqlOperatorFactory implements IOperatorFactory{

	private static final Map<SqlOperator, IOperator> operators=new HashMap<>();
	static {
		operators.put(SqlOperator.EQ, new PgsqlEqOperator());
		operators.put(SqlOperator.NE, new PgsqlNeOperator());
		operators.put(SqlOperator.IN, new PgsqlInOperator());
		operators.put(SqlOperator.NOT_IN, new PgsqlNotInOperator());
		operators.put(SqlOperator.LIKE, new PgsqlLikeOperator());
		operators.put(SqlOperator.NOT_LIKE, new PgsqlNotLikeOperator());
		operators.put(SqlOperator.LIKE_LEFT, new PgsqlLikeLeftOperator());
		operators.put(SqlOperator.LIKE_RIGHT, new PgsqlLikeRightOperator());
		operators.put(SqlOperator.NOT_LIKE_LEFT, new PgsqlNotLikeLeftOperator());
		operators.put(SqlOperator.NOT_LIKE_RIGHT, new PgsqlNotLikeRightOperator());
		operators.put(SqlOperator.GT, new PgsqlGtOperator());
		operators.put(SqlOperator.GE, new PgsqlGeOperator());
		operators.put(SqlOperator.LT, new PgsqlLtOperator());
		operators.put(SqlOperator.LE, new PgsqlLeOperator());
		operators.put(SqlOperator.IS_NULL, new PgsqlIsNullOperator());
		operators.put(SqlOperator.IS_NOT_NULL, new PgsqlIsNotNullOperator());
		operators.put(SqlOperator.IS_EMPTY, new PgsqlIsEmptyOperator());
		operators.put(SqlOperator.IS_NOT_EMPTY, new PgsqlIsNotEmptyOperator());
		operators.put(SqlOperator.JSON_CONTAINS_ANY, new PgsqlJsonContainsAnyOperator());
		operators.put(SqlOperator.JSON_NOT_CONTAINS_ANY, new PgsqlJsonNotContainsAnyOperator());
		operators.put(SqlOperator.JSON_CONTAINS_ALL, new PgsqlJsonContainsAllOperator());
		operators.put(SqlOperator.JSON_NOT_CONTAINS_ALL, new PgsqlJsonNotContainsAllOperator());
		operators.put(SqlOperator.JSON_EQ, new PgsqlJsonEqOperator());
		operators.put(SqlOperator.JSON_NE, new PgsqlJsonNeOperator());
		operators.put(SqlOperator.JSON_FIELD_EQ, new PgsqlJsonFieldEqOperator());
		operators.put(SqlOperator.JSON_FIELD_NE, new PgsqlJsonFieldNeOperator());
		operators.put(SqlOperator.JSON_FIELD_LIKE, new PgsqlJsonFieldLikeOperator());
		operators.put(SqlOperator.JSON_FIELD_NOT_LIKE, new PgsqlJsonFieldNotLikeOperator());
		operators.put(SqlOperator.JSON_FIELD_LIKE_LEFT, new PgsqlJsonFieldLikeLeftOperator());
		operators.put(SqlOperator.JSON_FIELD_LIKE_RIGHT, new PgsqlJsonFieldLikeRightOperator());
		operators.put(SqlOperator.JSON_FIELD_IN, new PgsqlJsonFieldInOperator());
		operators.put(SqlOperator.JSON_ARRAY_EQ, new PgsqlJsonArrayEqOperator());
		operators.put(SqlOperator.JSON_ARRAY_NE, new PgsqlJsonArrayNeOperator());
		operators.put(SqlOperator.JSON_ARRAY_CONTAINS_ANY, new PgsqlJsonArrayContainsAnyOperator());
		operators.put(SqlOperator.JSON_ARRAY_NOT_CONTAINS_ANY, new PgsqlJsonArrayNotContainsAnyOperator());
		operators.put(SqlOperator.JSON_ARRAY_CONTAINS_ALL, new PgsqlJsonArrayContainsAllOperator());
		operators.put(SqlOperator.JSON_ARRAY_NOT_CONTAINS_ALL, new PgsqlJsonArrayNotContainsAllOperator());
		operators.put(SqlOperator.JSON_ARRAY_IS_EMPTY, new PgsqlJsonArrayIsEmptyOperator());
		operators.put(SqlOperator.JSON_ARRAY_IS_NOT_EMPTY, new PgsqlJsonArrayIsNotEmptyOperator());
		operators.put(SqlOperator.ARRAY_EQ, new PgsqlArrayEqOperator());
		operators.put(SqlOperator.ARRAY_NE, new PgsqlArrayNeOperator());
		operators.put(SqlOperator.ARRAY_IS_EMPTY, new PgsqlArrayIsEmptyOperator());
		operators.put(SqlOperator.ARRAY_IS_NOT_EMPTY, new PgsqlArrayIsNotEmptyOperator());
		operators.put(SqlOperator.ARRAY_ANY, new PgsqlArrayAnyOperator());
		operators.put(SqlOperator.ARRAY_IN, new PgsqlArrayInOperator());
		operators.put(SqlOperator.ARRAY_NOT_IN, new PgsqlArrayNotInOperator());
		operators.put(SqlOperator.ARRAY_NOT_ANY, new PgsqlArrayNotAnyOperator());
		operators.put(SqlOperator.BETWEEN_AND, new PgsqlBetweenAndOperator());
		operators.put(SqlOperator.NOT_BETWEEN_AND, new PgsqlNotBetweenAndOperator());
		operators.put(SqlOperator.LTREE_BELONG, new PgsqlLtreeBelongOperator());
	}
	//
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
