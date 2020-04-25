package test.entity;

import io.itit.smartjdbc.annotations.Entity;
import lombok.Data;

/**
 * 
 * @author skydu
 *
 */
@Entity(tableName = "t_role")
@Data
public class Role extends BaseEntity{

	/**角色名称*/
	private String name;
}
