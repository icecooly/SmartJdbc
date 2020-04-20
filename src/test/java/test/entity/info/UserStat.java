package test.entity.info;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.EntityField;

/**
 * 
 * @author skydu
 *
 */
@Entity(tableName="User")
public class UserStat {

	public int gender;

	@EntityField(statFunc="count",field="id")
	public int num;
}
