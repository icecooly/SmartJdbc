package test.entity;

import io.itit.smartjdbc.annotations.Entity;
import lombok.extern.slf4j.Slf4j;

/**
 * 部门
 * @author skydu
 *
 */
@Entity(tableName = "t_department")
@Slf4j
public class Department extends BaseEntity{

	private String name;
	
	private int status;
}
