package test.entity;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.ForeignKey;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户喜爱文章
 * @author skydu
 *
 */
@Entity(tableName = "t_article_user_like")
@Slf4j
public class ArticleUserLike extends BaseEntity{
	//
	@ForeignKey(entityClass=Article.class)
	private int articleId;
	
	@ForeignKey(entityClass=User.class)
	private int userId;
}
