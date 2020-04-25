package test.entity;

import java.util.Date;
import java.util.List;

import io.itit.smartjdbc.annotations.Entity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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
