package test.entity;

import java.util.Date;

import io.itit.smartjdbc.annotations.PrimaryKey;

/**
 * 
 * @author skydu
 *
 */
public abstract class BaseEntity {
	
	@PrimaryKey
	public int id;
	
	public Date createTime;
	
	public Date updateTime;
}
