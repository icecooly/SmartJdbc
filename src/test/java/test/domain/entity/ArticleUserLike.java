package test.domain.entity;

import java.util.Date;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.ForeignKey;
import io.itit.smartjdbc.annotations.PrimaryKey;
import lombok.Data;

/**
 * 用户喜爱文章
 * @author skydu
 *
 */
@Entity(tableName = "t_article_user_like")
@Data
public class ArticleUserLike{
	//
	@PrimaryKey
	private Integer id;
	
	@ForeignKey(entityClass=Article.class)
	private Integer articleId;
	
	@ForeignKey(entityClass=User.class)
	private Integer userId;
	
	private Date createTime;

	private Date updateTime;
}
