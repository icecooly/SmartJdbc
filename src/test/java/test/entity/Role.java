package test.entity;

import io.itit.smartjdbc.annotations.Entity;

/**
 * 
 * @author skydu
 *
 */
@Entity(entityClass=Role.class)
public class Role extends BaseEntity{

	/**角色名称*/
	public String name;
}
