package test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import io.itit.smartjdbc.Config;
import io.itit.smartjdbc.DAOInterceptor;
import io.itit.smartjdbc.QueryWhere;
import io.itit.smartjdbc.SqlParam;
import test.dao.BizDAO;
import test.entity.Article;
import test.entity.User;

/**
 * 
 * @author skydu
 *
 */
public class AddUpdateTestCase extends BaseTestCase{
	//
	BizDAO dao;
	//
	public AddUpdateTestCase() {
		dao=new BizDAO();
		Config.addDAOInterceptor(new DAOInterceptor() {
			@Override
			public void beforeInsert(Object bean, boolean withGenerateKey,String[] excludeProperties) {
				super.beforeInsert(bean, withGenerateKey, excludeProperties);
				dao.setFieldValue(bean, "createTime", new Date());
				dao.setFieldValue(bean, "updateTime", new Date());
			}
			//
			@Override
			public void beforeUpdate(Object bean, boolean excludeNull, String[] excludeProperties) {
				super.beforeUpdate(bean, excludeNull, excludeProperties);
				dao.setFieldValue(bean, "updateTime", new Date());
			}
		});
		Config.setConvertFieldNameFunc(this::convertFieldName);
	}
	//
	protected  String convertFieldName(String name) {
		StringBuffer result = new StringBuffer();
		for (char c : name.toCharArray()) {
			if (Character.isUpperCase(c)) {
				result.append("_");
			}
			result.append(Character.toLowerCase(c));
		}
		return result.toString();
	}
	//
	static {
	    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
	}
	//
	/**新增用户*/
	public void testAddUser() {
		User user=new User();
		user.setName("张三");
		user.setUserName("zhangsan");
		user.setRoleIdList(Arrays.asList(1,2,3));
		user.setId(dao.add(user));
		System.out.println(user.getId());
	}
	
	public void testUpdateUser() {
		User user=dao.getById(User.class, 1);
		user.setGender(User.GENDER_男);
		dao.update(user);
	}
	
	public void testUpdateUser2() {
		dao.executeUpdate("update t_user set name='关羽0' where id=?",1);
		dao.executeUpdate("update t_user set name='关羽2' where id=#{id}",new SqlParam("id", 1));
	}
	
	/**删除用户*/
	public void testDeleteUser() {
		int count=dao.delete(User.class, QueryWhere.create().where("id", 3));
		System.out.println(count);
	}
	//
	/**添加文章*/
	public void testAddArticle() throws IOException{
		Article bean=new Article();
		bean.setTitle("桑切斯遭孤立?队友吃饭他加练 为融入曼联拼了");
		bean.setContent("上周，有关桑切斯的负面消息很多，英媒体爆料他在曼联阵中独来独往、总是一个人吃饭，"
				+ "无法融入到集体中，还有媒体称桑切斯甚至在比赛中遭到了队友的当面批评，"
				+ "这些让外界对桑切斯在曼联的未来表示了担忧。不过，英媒体《每日镜报》"
				+ "今日透露桑切斯在曼联并没有被孤立，智利人也没有刻意躲避队友，"
				+ "他之所以经常一个人吃饭竟是这样的原因。");
		bean.setCreateUserId(1);
		bean.setUpdateUserId(1);
		bean.setStatus(Article.STATUS_待审核);
		int id=dao.add(bean);
		System.out.println(id);
	}
}
