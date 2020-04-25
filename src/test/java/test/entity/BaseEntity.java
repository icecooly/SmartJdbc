package test.entity;

import java.util.Date;

import io.itit.smartjdbc.annotations.PrimaryKey;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author skydu
 *
 */
@Slf4j
public abstract class BaseEntity {
	
	@PrimaryKey
	private int id;
	
	private Date createTime;
	
	private Date updateTime;
}
