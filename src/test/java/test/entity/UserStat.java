package test.entity;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.EntityField;
import lombok.Data;

/**
 * 
 * @author skydu
 *
 */
@Entity(tableName="t_user")
@Data
public class UserStat {

	private int gender;

	@EntityField(statFunc="count",field="id")
	private int num;
}