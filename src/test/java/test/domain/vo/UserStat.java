package test.domain.vo;

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

	private Integer gender;

	@EntityField(statFunc="count",field="id")
	private Integer num;
}