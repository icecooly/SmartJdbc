package test.entity;

import java.util.Date;
import java.util.List;

import io.itit.smartjdbc.annotations.PrimaryKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author skydu
 *
 */
@Data
public abstract class BaseEntity {
	
	@PrimaryKey
	private int id;
	
	private Date createTime;
	
	private Date updateTime;
}
