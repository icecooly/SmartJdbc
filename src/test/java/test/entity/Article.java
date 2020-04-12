package test.entity;

import io.itit.smartjdbc.annotations.EntinyField;
import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.ForeignKey;

/**
 * 文章
 * @author skydu
 *
 */
@Entity(entityClass=Article.class,tableName = "t_article")
public class Article extends BaseEntity{
	//
	public static final int STATUS_待审核=1;
	public static final int STATUS_审核通过=2;
	public static final int STATUS_审核未通过=3;
	//
	/**标题*/
	public String title;
	
	/**内容*/
	public String content;
	/**状态*/
	public int status;

	@ForeignKey(entityClass = User.class)
	public int createUserId;
	
	@ForeignKey(entityClass = User.class)
	public int updateUserId;
	
	/**创建人名称*/
	@EntinyField(foreignKeyFields="createUserId",field="name",persistent = false)
	public String createUserName;
	
	/**创建人所在部门名称*/
	@EntinyField(foreignKeyFields="createUserId,departmentId",field="name",persistent = false)
	public String createUserDepartmentName;
	
	/***/
	@EntinyField(foreignKeyFields="updateUserId",persistent = false)
	public User updateUser;
	
}
