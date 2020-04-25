package test.entity;

import io.itit.smartjdbc.annotations.Entity;
import lombok.Data;

/**
 * 部门
 * @author skydu
 *
 */
@Entity(tableName = "t_department")
@Data
public class Department extends BaseEntity{

	private String name;
	
	private int status;
}
