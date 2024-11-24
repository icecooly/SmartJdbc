package io.itit.smartjdbc.provider.where.operator.pgsql;

import io.itit.smartjdbc.provider.where.operator.ColumnOperator;

/**
 * 
 * @author skydu
 *
 */
public abstract class PgsqlColumnOperator extends ColumnOperator {

	public String addDoubleQuotes(String v) {
		return "\""+v+"\"";
	}
	
}
