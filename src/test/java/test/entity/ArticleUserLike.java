package test.entity;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.ForeignKey;

/**
 * 用户喜爱文章
 * @author skydu
 *
 */
@Entity(entityClass=ArticleUserLike.class)
public class ArticleUserLike extends BaseEntity{
	//
	@ForeignKey(entityClass=Article.class)
	public int articleId;
	
	@ForeignKey(entityClass=User.class)
	public int userId;
	
}
