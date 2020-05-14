package test.domain.vo;

import io.itit.smartjdbc.annotations.Entity;

/**
 * 
 * @author skydu
 *
 */
@Entity(tableName = "t_user")
public class UserSimple {
	
	public Integer id;
	
	
	public String name;
	
	
	public Integer test;

}
