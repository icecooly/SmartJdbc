package io.itit.smartjdbc.domain;

import io.itit.smartjdbc.enums.ColumnType;

/**
 * 
 * @author skydu
 *
 */  
public class EntityFieldSetting {
	
	public String column;
	
	public String comment;
	
	public boolean autoIncrement;
	
	public String foreignKeyFields;
	
	/**查询时去重*/
	public boolean distinct;

	/**统计函数 eg:select min() */
	public String func;
	
	/**查询时不映射sql*/
	public boolean ignoreWhenSelect;
	
	/**是否持久化到数据库*/
	public boolean persistent;
	
	/**字段类型*/
	public ColumnType columnType=ColumnType.NONE;
}