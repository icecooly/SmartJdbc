package test.entity.query;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.InnerJoin;
import io.itit.smartjdbc.annotations.InnerJoins;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.annotations.QueryField.OrGroup;
import io.itit.smartjdbc.enums.SqlOperator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import test.entity.Article;
import test.entity.ArticleUserLike;
import test.entity.Department;
import test.entity.User;

/**
 * 
 * @author skydu
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class ArticleQuery extends Query<Article>{

	@QueryField
	private String title;
	
	@QueryField
	private Integer status;
	
	@QueryField(whereSql="and (title like concat('%',#{titleOrContent},'%') or content like concat('%',#{titleOrContent},'%'))")
	private String titleOrContent;
	
	@InnerJoin(table2=User.class,table1Fields= {"createUserId"},table2Fields = {"id"})
	@QueryField(field="name")
	private String createUserName;

	@InnerJoin(table2=User.class,table1Fields= {"updateUserId"},table2Fields= {"id"})
	@QueryField(field="name")
	private String updateUserName;
	
	@QueryField(field="status")
	private int[] statusList;
	
	/**likeUserId喜爱的文章*/
	@InnerJoin(table2=ArticleUserLike.class,table1Fields = {"id"},table2Fields= {"articleId"})
	@QueryField(field="userId")
	private Integer likeUserId;
	
	@QueryField(orGroup=@OrGroup(group="1"),field="status")
	private int[] orStatusList;

	@QueryField(orGroup=@OrGroup(group="1"),field="createUserId")
	private Integer orCreateUserId;
	
	@QueryField(field="name",operator = SqlOperator.LIKE,foreignKeyFields="createUserId,departmentId")
	private String createUserDepartmentName;
	
	@InnerJoins(joins = {
			@InnerJoin(table2 = User.class,table1Fields = {"createUserId"},table2Fields = {"id"}),
			@InnerJoin(table2 = Department.class,table1Fields = {"departmentId"},table2Fields = {"id"})
			})
	@QueryField(field="name",operator = SqlOperator.LIKE_RIGHT)
	private String createUserDepartmentName2;
}
