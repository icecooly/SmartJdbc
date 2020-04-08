package test.entity.info;

import io.itit.smartjdbc.annotations.Entity;
import test.entity.Article;
import test.entity.User;
import io.itit.smartjdbc.annotations.EntinyField;

/**
 * 文章详情
 * @author skydu
 *
 */
@Entity(entityClass=Article.class)
public class ArticleInfo extends Article{

	/**创建人名称*/
	@EntinyField(foreignKeyFields="createUserId",field="name")
	public String createUserName;
	
	/**创建人所在部门名称*/
	@EntinyField(foreignKeyFields="createUserId,departmentId",field="name")
	public String createUserDepartmentName;
	
	/***/
	@EntinyField(foreignKeyFields="updateUserId")
	public User updateUser;
}
