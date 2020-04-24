package test.entity.query;

import java.util.List;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.enums.SqlOperator;
import test.entity.User;

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
	
	@QueryField(field ="status",operator=SqlOperator.IN)
	public List<Integer> statusInList;
	
	@QueryField(field ="status",operator=SqlOperator.NOT_IN)
	public List<Integer> statusNotInList;
	
	@QueryField(whereSql="json_contains(a.role_id_list,'${roleId}')")
    public Integer roleId; 
	
	@QueryField(whereSql="a.status=#{orStatus} or json_contains(a.role_id_list,#{orRoleId})")
    public Boolean statusOrRoleId;
}
