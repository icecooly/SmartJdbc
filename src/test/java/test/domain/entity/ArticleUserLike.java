package test.domain.entity;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.ForeignKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户喜爱文章
 * @author skydu
 *
 */
@Entity(tableName = "t_article_user_like")
@Data
@EqualsAndHashCode(callSuper=true)
public class ArticleUserLike extends BaseEntity{
	//
	@ForeignKey(entityClass=Article.class)
	private Integer articleId;
	
	@ForeignKey(entityClass=User.class)
	private Integer userId;
}
