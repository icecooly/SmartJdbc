package test.entity.info;

import io.itit.smartjdbc.annotations.EntinyField;
import test.entity.User;

/**
 * 用户详情
 * @author skydu
 *
 */
public class UserInfo extends User{

	@EntinyField(foreignKeyFields="departmentId",field="name")
	public String departmentName;
	
	@EntinyField(foreignKeyFields="roleId",field="name")
	public String roleName;
}
