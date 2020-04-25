package test.entity;

import io.itit.smartjdbc.annotations.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author skydu
 *
 */
@Entity(tableName = "t_role")
@Data
@EqualsAndHashCode(callSuper=true)
public class Role extends BaseEntity{

	/**角色名称*/
	private String name;
}
