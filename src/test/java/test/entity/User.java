package test.entity;

import java.util.Date;
import java.util.List;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.EntityField;
import io.itit.smartjdbc.annotations.ForeignKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 *用户
 * @author skydu
 *
 */
@Entity(tableName = "t_user")
@Data
@EqualsAndHashCode(callSuper=true)
public class User extends BaseEntity{
	//
	public static final int GENDER_男=1;
	public static final int GENDER_女=2;
	
	public static final int STATUS_在职=1;
	public static final int STATUS_离职=2;

	
	/**用户名*/
	private String userName;
	
	/**姓名*/
	private String name;
	
	/**手机号*/
	private String mobileNo;
	
	/**性别*/
	private int gender;
	
	/**工作状态*/
	private int status;
	
	/**年龄*/
	private int age;
	
	/**所属部门*/
	@ForeignKey(entityClass=Department.class)
	private int departmentId;
	
	/**角色列表*/
	private List<Integer> roleIdList;
	
	/**最后登录时间*/
	private Date lastLoginTime;
	
	/**分表文章数*/
	private Integer articleNum;
	
	/**创建人*/
	@ForeignKey(entityClass=User.class)
	private Integer createUserId;
	
	/**最后更新人*/
	@ForeignKey(entityClass=User.class)
	private Integer updateUserId;
	
	/**所属部门名称*/
	@EntityField(foreignKeyFields="departmentId",field="name",persistent = false)
	private String departmentName;
}