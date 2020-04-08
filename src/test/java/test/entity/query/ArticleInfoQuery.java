package test.entity.query;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.InnerJoin;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.annotations.QueryField.OrGroup;
import test.entity.ArticleUserLike;
import test.entity.User;
import test.entity.info.ArticleInfo;

/**
 * 
 * @author skydu
 *
 */
public class ArticleInfoQuery extends Query<ArticleInfo>{

	public String title;
	
	public Integer status;
	
	@QueryField(whereSql="and (title like concat('%',#{titleOrContent},'%') or content like concat('%',#{titleOrContent},'%'))")
	public String titleOrContent;
	
	@InnerJoin(table2=User.class,table1Field="createUserId")
	@QueryField(field="name")
	public String createUserName;

	@InnerJoin(table2=User.class,table1Field="updateUserId")
	@QueryField(field="name")
	public String updateUserName;
	
	@QueryField(field="status")
	public int[] statusList;
	
	@QueryField(field="name",foreignKeyFields="createUserId,departmentId")
	public String createUserDepartmentName;
	
	/**likeUserId喜爱的文章*/
	@InnerJoin(table2=ArticleUserLike.class,table2Field="articleId")
	@QueryField(field="userId")
	public Integer likeUserId;
	
	@QueryField(orGroup=@OrGroup(group="123"),field="status")
	public int[] orStatusList;

	@QueryField(orGroup=@OrGroup(group="123"),field="createUserId")
	public int orCreateUserId;
}
