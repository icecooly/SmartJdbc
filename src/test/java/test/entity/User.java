package test.entity;

import java.util.Date;
import java.util.List;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.ForeignKey;
/**
 *用户
 * @author skydu
 *
 */
@Entity(entityClass=User.class,tableName = "t_user")
public class User extends BaseEntity{
	
	public static final int STATUS_在职=1;
	public static final int STATUS_离职=2;

	public static final int GENDER_男=1;
	public static final int GENDER_女=2;
	//
	public String userName;
	
	public String name;
	
	public String mobileNo;
	
	public int gender;
	
	public int status;
	
	@ForeignKey(entityClass=Department.class)
	public int departmentId;
	
	/**角色列表*/
	public List<Integer> roleIdList;
	
	/**最后登录时间*/
	public Date lastLoginTime;
	
	/**创建人*/
	@ForeignKey(entityClass=User.class)
	public int createUserId;
	
	/**最后更新人*/
	@ForeignKey(entityClass=User.class)
	public int updateUserId;
}