package test.entity;

import java.util.Date;
import java.util.List;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.EntityField;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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