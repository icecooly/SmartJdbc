package test.entity.info;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.EntinyField;

/**
 * 
 * @author skydu
 *
 */
@Entity(tableName="User")
public class UserStat {

	public int gender;

	@EntinyField(statFunc="count",field="id")
	public int num;
}
