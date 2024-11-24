package test.domain.entity;

import java.util.Date;
import java.util.List;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.EntityField;
import io.itit.smartjdbc.annotations.ForeignKey;
import io.itit.smartjdbc.annotations.PrimaryKey;
import io.itit.smartjdbc.enums.ColumnType;
import lombok.Data;
/**
 *用户
 * @author skydu
 *
 */
@Entity(tableName = "t_user")
@Data
public class User{
	//
	public static final short GENDER_男=1;
	public static final short GENDER_女=2;
	
	public static final short STATUS_在职=1;
	public static final short STATUS_离职=2;

	@PrimaryKey
	private Integer id;
	
	/**用户名*/
	private String userName;
	
	/**姓名*/
	private String name;
	
	/**手机号*/
	private String mobileNo;
	
	/**性别*/
	private Short gender;
	
	/**工作状态*/
	private Short status;
	
	/**年龄*/
	private Integer age;
	
	/**所属部门*/
	@ForeignKey(entityClass=Department.class)
	private Integer departmentId;
	
	/**角色列表*/
	@EntityField(columnType = ColumnType.JSONB)
	private List<Integer> roleIdList;
	
	/**最后登录时间*/
	private Date lastLoginTime;
	
	/**分表文章数*/
	private Long articleNum;
	
	/**创建人*/
	@ForeignKey(entityClass=User.class)
	private Integer createUserId;
	
	/**最后更新人*/
	@ForeignKey(entityClass=User.class)
	private Integer updateUserId;
	
	/**所属部门名称*/
	@EntityField(foreignKeyFields="departmentId",field="name",persistent = false)
	private String departmentName;
	
	private Double height;
	
	private Long no;
	
	private Boolean isStudent;
	
	private String setting;
	
	private Short[] shortArray;
	
	private Integer[] intArray;

	private Long[] longArray;

	private Float[] floatArray;

	private String[] stringArray;
	
	private Date createTime;
	
	private Date updateTime;
}