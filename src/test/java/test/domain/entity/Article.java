package test.domain.entity;

import java.util.Date;
import java.util.List;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.EntityField;
import io.itit.smartjdbc.annotations.ForeignKey;
import io.itit.smartjdbc.annotations.PrimaryKey;
import lombok.Data;

/**
 * 文章
 * @author skydu
 *
 */
@Entity(tableName = "t_article")
@Data
public class Article{
	//
	public static final int STATUS_待审核=1;
	public static final int STATUS_审核通过=2;
	public static final int STATUS_审核未通过=3;
	
	@PrimaryKey
	private Integer id;
	//
	/**标题*/
	private String title;
	
	/**内容*/
	private String content;
	
	/**状态*/
	private Integer status;

	@ForeignKey(entityClass = User.class)
	private Integer createUserId;
	
	@ForeignKey(entityClass = User.class)
	private Integer updateUserId;
	
	/**创建人名称*/
	@EntityField(foreignKeyFields="createUserId",field="name",persistent = false)
	private String createUserName;
	
	@EntityField(foreignKeyFields="createUserId",field="mobileNo",persistent = false)
	private String createUserMobileNo;
	
	/**创建人所在部门名称*/
	@EntityField(foreignKeyFields="createUserId,departmentId",field="name",persistent = false)
	private String createUserDepartmentName;
	
	/***/
	@EntityField(foreignKeyFields="updateUserId",persistent = false)
	private User updateUser;
	
	private List<User> favoriteUserList;
	
	private Date createTime;

	private Date updateTime;
	
	//
	public Article() {
		
	}
	//
	public Article(String title) {
		this.title=title;
	}
	
}
