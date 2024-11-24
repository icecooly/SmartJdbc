package io.itit.smartjdbc.provider.where.operator;

/**
 * 
 * @author skydu
 *
 */
public interface IOperatorFactory {
	
	/**
	 * 
	 * @param ctx
	 * @return
	 */
	IOperator create(OperatorContext ctx);
}
