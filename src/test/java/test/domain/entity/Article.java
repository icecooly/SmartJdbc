package test.domain.entity;

import java.util.List;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.EntityField;
import io.itit.smartjdbc.annotations.ForeignKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章
 * @author skydu
 *
 */
@Entity(tableName = "t_article")
@Data
@EqualsAndHashCode(callSuper=true)
public class Article extends BaseEntity{
	//
	public static final int STATUS_待审核=1;
	public static final int STATUS_审核通过=2;
	public static final int STATUS_审核未通过=3;
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
	
	/**创建人所在部门名称*/
	@EntityField(foreignKeyFields="createUserId,departmentId",field="name",persistent = false)
	private String createUserDepartmentName;
	
	/***/
	@EntityField(foreignKeyFields="updateUserId",persistent = false)
	private User updateUser;
	
	private List<User> favoriteUserList;
	
	//
	public Article() {
		
	}
	//
	public Article(String title) {
		this.title=title;
	}
	
}
