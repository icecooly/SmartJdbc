package test.domain.vo;

import java.util.List;
import java.util.Map;

import io.itit.smartjdbc.annotations.Entity;
import io.itit.smartjdbc.annotations.EntityField;
import io.itit.smartjdbc.annotations.ForeignKey;
import lombok.Data;
import test.domain.entity.Article;
import test.domain.entity.User;

/**
 * 
 * @author skydu
 *
 */
@Entity(tableName = "t_test")
@Data
public class Test {
	
	private Integer Id;
	
	@ForeignKey(entityClass = User.class)
	private Integer createUserId;
	
	@EntityField(foreignKeyFields = "createUserId")
	private User createUser;

	private List<Article> articleList;	
	
	private Map<String,List<Article>> articleMap;

}
