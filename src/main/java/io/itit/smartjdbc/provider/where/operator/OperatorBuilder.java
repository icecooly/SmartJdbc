package io.itit.smartjdbc.provider.where.operator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.enums.SqlOperator;
import io.itit.smartjdbc.provider.where.Where.Condition;

/**
 * 
 * @author skydu
 *
 */
public class OperatorBuilder {
	//
	private static Map<String, Operator> customOperators=new ConcurrentHashMap<>();
	//
	private static Logger logger=LoggerFactory.getLogger(OperatorBuilder.class);

	
	/**
	 * 
	 * @param ctx
	 * @return
	 */
	public static Operator build(OperatorContext ctx) {
		Operator opt=null;
		Condition w=ctx.getCondition();
		SqlOperator operator=w.operator;
		if(operator==null) {
			operator=SqlOperator.EQ;//default
		}
		if(operator.equals(SqlOperator.CUSTOM)) {
			opt=getCustomOperator(w);
		}
		if(operator.equals(SqlOperator.EQ)) {
			opt=new EqOperator(ctx);
		}
		if(operator.equals(SqlOperator.NE)) {
			opt=new NeOperator(ctx);
		}
		if(operator.equals(SqlOperator.IN)) {
			opt=new InOperator(ctx);
		}
		if(operator.equals(SqlOperator.NOT_IN)) {
			opt=new NotInOperator(ctx);
		}
		if(operator.equals(SqlOperator.LIKE)) {
			opt=new LikeOperator(ctx);
		}
		if(operator.equals(SqlOperator.NOT_LIKE)) {
			opt=new NotLikeOperator(ctx);
		}
		if(operator.equals(SqlOperator.LIKE_LEFT)) {
			opt=new LikeLeftOperator(ctx);
		}
		if(operator.equals(SqlOperator.LIKE_RIGHT)) {
			opt=new LikeRightOperator(ctx);
		}
		if(operator.equals(SqlOperator.NOT_LIKE_LEFT)) {
			opt=new NotLikeLeftOperator(ctx);
		}
		if(operator.equals(SqlOperator.NOT_LIKE_RIGHT)) {
			opt=new NotLikeRightOperator(ctx);
		}
		if(operator.equals(SqlOperator.GT)) {
			opt=new GtOperator(ctx);
		}
		if(operator.equals(SqlOperator.GE)) {
			opt=new GeOperator(ctx);
		}
		if(operator.equals(SqlOperator.LT)) {
			opt=new LtOperator(ctx);
		}
		if(operator.equals(SqlOperator.LE)) {
			opt=new LeOperator(ctx);
		}
		if(operator.equals(SqlOperator.IS_NULL)) {
			opt=new IsNullOperator(ctx);
		}
		if(operator.equals(SqlOperator.IS_NOT_NULL)) {
			opt=new IsNotNullOperator(ctx);
		}
		if(operator.equals(SqlOperator.JSON_CONTAINS_ANY)) {
			opt=new JsonContainsAnyOperator(ctx);
		}
		if(operator.equals(SqlOperator.JSON_NOT_CONTAINS_ANY)) {
			opt=new JsonNotContainsAnyOperator(ctx);
		}
		if(operator.equals(SqlOperator.JSON_CONTAINS_ALL)) {
			opt=new JsonContainsAllOperator(ctx);
		}
		if(operator.equals(SqlOperator.JSON_CONTAINS_EQ)) {
			opt=new JsonContainsEqOperator(ctx);
		}
		if(operator.equals(SqlOperator.JSON_CONTAINS_NE)) {
			opt=new JsonContainsNeOperator(ctx);
		}
		if(operator.equals(SqlOperator.ARRAY_ANY)) {
			opt=new ArrayAnyOperator(ctx);
		}
		if(operator.equals(SqlOperator.ARRAY_NOT_ANY)) {
			opt=new ArrayNotAnyOperator(ctx);
		}
		if(operator.equals(SqlOperator.ARRAY_CONTAINS)) {
			opt=new ArrayContainsOperator(ctx);
		}
		if(operator.equals(SqlOperator.ARRAY_NOT_CONTAINS)) {
			opt=new ArrayNotContainsOperator(ctx);
		}
		if(operator.equals(SqlOperator.BETWEEN_AND)) {
			opt=new BetweenAndOperator(ctx);
		}
		if(operator.equals(SqlOperator.NOT_BETWEEN_AND)) {
			opt=new NotBetweenAndOperator(ctx);
		}
		return opt;
	}
	//
	private static Operator getCustomOperator(Condition w) {
		if(w.customOperator==null) {
			throw new SmartJdbcException("no custom operator found ");
		}
		Operator operator=customOperators.get(w.customOperator);
		if(operator==null) {
			throw new SmartJdbcException("no custom operator found "+w.customOperator);
		}
		return operator;
	}
	//
	public static void registerCustomOperator(String customOperator, Operator operator) {
		if(customOperator==null||operator==null) {
			throw new IllegalArgumentException();
		}
		customOperators.put(customOperator, operator);
		logger.info("registerCustomOperator {}",customOperator);
	}
}
