package io.itit.smartjdbc.domain;

import io.itit.smartjdbc.enums.SqlOperator;

/**
 * 
 * @author skydu
 *
 */
public class QueryFieldSetting {
	/**别名*/
	public String alias;//
	
	/**和表结构映射的字段名*/
	public String column;
	
	/** 操作符 非字符串默认是EQ*/
	public SqlOperator operator=SqlOperator.EQ;

	/** 自定义查询sql */
	public String whereSql;//usage: (name like #{nameOrUserName} or userName like #{nameOrUserName})
	
	/**别的表的关联字段  必须填对应的外键字段 可以有多个按照顺序依次  逗号分隔*/
	public String foreignKeyFields;
}
