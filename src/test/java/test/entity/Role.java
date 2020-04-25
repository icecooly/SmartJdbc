package test.entity;

import io.itit.smartjdbc.annotations.Entity;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author skydu
 *
 */
@Entity(tableName = "t_role")
@Slf4j
public class Role extends BaseEntity{

	/**角色名称*/
	private String name;
}
