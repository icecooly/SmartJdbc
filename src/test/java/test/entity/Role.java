package test.entity;

import java.util.Date;
import java.util.List;

import io.itit.smartjdbc.annotations.Entity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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
