package test.entity;

import java.util.Date;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.ForeignKey;
import test.entity.Role;
/**
 *用户
 * @author skydu
 *
 */
@Entity(entityClass=User.class)
public class User extends BaseEntity{
	
	public static final int STATUS_在职=1;
	public static final int STATUS_离职=2;

	public static final int GENDER_男=1;
	public static final int GENDER_女=2;
	//
	public String userName;
	
	public String password;
	
	public String name;
	
	public String mobileNo;
	
	public int gender;
	
	public int status;
	
	@ForeignKey(entityClass=Department.class)
	public int departmentId;
	
	@ForeignKey(entityClass=Role.class)
	public int roleId;
	
	/**最后登录时间*/
	public Date lastLoginTime;
	
	/**创建人*/
	@ForeignKey(entityClass=User.class)
	public int createUserId;
	
	/**最后更新人*/
	@ForeignKey(entityClass=User.class)
	public int updateUserId;
}