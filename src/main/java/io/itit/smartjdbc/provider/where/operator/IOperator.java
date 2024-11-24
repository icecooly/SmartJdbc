package io.itit.smartjdbc.provider.where.operator;

import io.itit.smartjdbc.provider.entity.SqlBean;

/**
 * 
 * @author skydu
 *
 */
public interface IOperator {
	//
	/**
	 * 
	 * @param ctx
	 * @return
	 */
	SqlBean build(OperatorContext ctx);
}
