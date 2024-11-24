package test.domain.entity;

import java.util.Date;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.PrimaryKey;
import lombok.Data;

/**
 * 
 * @author skydu
 *
 */
@Entity(tableName = "t_role")
@Data
public class Role {

	@PrimaryKey
	private Integer id;

	/** 角色名称 */
	private String name;

	private Date createTime;

	private Date updateTime;
}
