package io.itit.smartjdbc.provider.entity;

import io.itit.smartjdbc.enums.SqlOperator;
import io.itit.smartjdbc.provider.SqlProvider;
import io.itit.smartjdbc.provider.where.Where.JsonContain;

/**
 * 
 * @author skydu
 *
 */
public class QueryFieldInfo{

	/**列名*/
	public String column;//user_name
	
	public String columnCast;//::jsonb ::text
	
	/**别名*/
	public String tableAlias;
	
	/**是否是字段*/
	public boolean isColumn;
	
	public String javaType;//java type	String Date
	
	public String columnType;//db type  varchar text DATETIME
	
	/** 操作符 非字符串默认是EQ*/
	public SqlOperator operator;
	
	public Object value;
	
	public JsonContain jsonContain;
	
	//
	public QueryFieldInfo() {
		this.isColumn=true;
		this.tableAlias=SqlProvider.MAIN_TABLE_ALIAS;
	}
	
}
