package test.domain.vo;

import io.itit.smartjdbc.annotations.Entity;
import lombok.Data;

/**
 * 
 * @author skydu
 *
 */
@Entity(tableName = "t_user")
@Data
public class UserSimple {
	
	private Integer id;
	
	
	private String name;
	
	
	private Integer test;

}
