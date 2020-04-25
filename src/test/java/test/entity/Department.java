package test.entity;

import io.itit.smartjdbc.annotations.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门
 * @author skydu
 *
 */
@Entity(tableName = "t_department")
@Data
@EqualsAndHashCode(callSuper=true)
public class Department extends BaseEntity{

	private String name;
	
	private int status;
}
