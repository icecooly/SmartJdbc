package test.domain.query;

import java.util.List;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.InnerJoin;
import io.itit.smartjdbc.annotations.Joins;
import io.itit.smartjdbc.annotations.QueryConditionType;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.enums.ConditionType;
import io.itit.smartjdbc.enums.SqlOperator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import test.domain.entity.Article;
import test.domain.entity.ArticleUserLike;
import test.domain.entity.Department;
import test.domain.entity.User;

/**
 * 
 * @author skydu
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class ArticleQuery extends Query<Article>{

	@QueryField(field = "id", operator = SqlOperator.IN)
	private List<Integer> idInList;
	
	@QueryField
	private String title;
	
	@QueryField
	private Integer status;
	
	@QueryField(whereSql=" (title like concat('%',#{titleOrContent},'%') or content like concat('%','${titleOrContent}','%'))")
	private String titleOrContent;
	
	@InnerJoin(table2=User.class,table1Fields= {"createUserId"},table2Fields = {"id"})
	@QueryField(field="name")
	private String createUserName;

	@QueryField(field="mobileNo",foreignKeyFields = "createUserId")
	private String createUserMobileNo;

	@InnerJoin(table2=User.class,table1Fields= {"updateUserId"},table2Fields= {"id"})
	@QueryField(field="name")
	private String updateUserName;
	
	@QueryField(field="status")
	private int[] statusList;
	
	/**likeUserId喜爱的文章*/
	@InnerJoin(table2=ArticleUserLike.class,table1Fields = {"id"},table2Fields= {"articleId"})
	@QueryField(field="userId")
	private Integer likeUserId;
	
	@QueryField(field="name",operator = SqlOperator.LIKE,foreignKeyFields="createUserId,departmentId")
	private String createUserDepartmentName;
	
	@Joins(joins = {
			@InnerJoin(table2 = User.class,table1Fields = {"createUserId"},table2Fields = {"id"}),
			@InnerJoin(table2 = Department.class,table1Fields = {"departmentId"},table2Fields = {"id"})
			})
	@QueryField(field="name",operator = SqlOperator.LIKE_RIGHT)
	private String createUserDepartmentName2;
	
	//
	@QueryConditionType(ConditionType.OR)
	private IdListOrTitle idListOrTitle;
	//
	@Data
	public static class IdListOrTitle {
		//
		@QueryField(operator = SqlOperator.LIKE)
		private String title;
		
		@QueryField(field = "id", operator = SqlOperator.IN)
		private List<Integer> idInList;

	};
}
