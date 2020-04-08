package test.entity;

import io.itit.smartjdbc.annotations.Entity;

/**
 * 部门
 * @author skydu
 *
 */
@Entity(entityClass=Department.class)
public class Department extends BaseEntity{

	public String name;
	
	public int status;
}
