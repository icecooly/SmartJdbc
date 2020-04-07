package test.domain.query;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.QueryField;
import test.domain.User;

/**
 * 
 * @author skydu
 *
 */
public class UserQuery extends Query<User>{

	public String userName;
	
	public Integer gender;
	
	@QueryField(field="name,userName")
	public String nameOrUserName;
}
