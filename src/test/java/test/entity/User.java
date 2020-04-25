package test.entity;

import java.util.Date;
import java.util.List;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.EntityField;
import io.itit.smartjdbc.annotations.ForeignKey;
import lombok.Data;
/**
 *用户
 * @author skydu
 *
 */
@Entity(tableName = "t_user")
@Data
public class User extends BaseEntity{
	
	public static final int STATUS_在职=1;
	public static final int STATUS_离职=2;

	public static final int GENDER_男=1;
	public static final int GENDER_女=2;
	//
	private String userName;
	
	private String name;
	
	private String mobileNo;
	
	private int gender;
	
	private int status;
	
	@ForeignKey(entityClass=Department.class)
	private int departmentId;
	
	/**角色列表*/
	private List<Integer> roleIdList;
	
	/**最后登录时间*/
	private Date lastLoginTime;
	
	private int articleNum;
	
	/**创建人*/
	@ForeignKey(entityClass=User.class)
	private int createUserId;
	
	/**最后更新人*/
	@ForeignKey(entityClass=User.class)
	private int updateUserId;
	
	@EntityField(foreignKeyFields="departmentId",field="name",persistent = false)
	private String departmentName;
}