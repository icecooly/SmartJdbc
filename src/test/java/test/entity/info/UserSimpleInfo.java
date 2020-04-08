package test.entity.info;

import io.itit.smartjdbc.annotations.Entity;

/**
 * 
 * @author skydu
 *
 */
@Entity(tableName="User")
public class UserSimpleInfo {

	public int id;
	public String name;
}
