package test.domain.entity;

import java.util.Date;

import io.itit.smartjdbc.annotations.PrimaryKey;
import lombok.Data;

/**
 * 
 * @author skydu
 *
 */
@Data
public abstract class BaseEntity {
	
	@PrimaryKey
	private Integer id;
	
	private Date createTime;
	
	private Date updateTime;
}
