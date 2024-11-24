package test.domain.entity;

import java.util.Date;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.PrimaryKey;
import lombok.Data;

/**
 * 部门
 * 
 * @author skydu
 *
 */
@Entity(tableName = "t_department")
@Data
public class Department {

	@PrimaryKey
	private Integer id;

	private String name;

	private Integer status;

	private Date createTime;

	private Date updateTime;
}
