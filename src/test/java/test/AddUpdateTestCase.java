package test;

import java.io.IOException;
import java.util.Arrays;

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
	static {
	    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
	}
	//
	BizDAO dao;
	//
	public AddUpdateTestCase() {
		dao=new BizDAO();
	}
	//
	/**新增用户*/
	public void testAddUser() {
		User user=new User();
		user.setName("王五");
		user.setUserName("wangwu");
		user.setStatus(1);
		user.setDepartmentId(1);
		user.setRoleIdList(Arrays.asList(1,2,3));
		user.setId((long)dao.add(user));
		System.out.println(user.getId());
	}
	
	/**
	 * 更新所有字段
	 */
	public void testUpdateUser() {
		User user=dao.getById(User.class, 2);
		user.setGender(User.GENDER_男);
		user.setDepartmentId(2);
		user.setStatus(2);
		dao.update(user);
	}
	
	/**
	 * 只更新非空字段
	 */
	public void testUpdateExcludeNull() {
		User user=new User();
		user.setId(2L);
		user.setDepartmentId(3);
		user.setMobileNo("130000000000");
		dao.updateExcludeNull(user);
	}
	
	/**
	 * 只更新status字段
	 */
	public void testUpdateUserStatus() {
		User user=dao.getById(User.class, 2);
		user.setStatus(3);
		dao.updateIncludeFields(user, "status");
	}
	
	/**
	 * 
	 */
	public void testUpdateUser2() {
		dao.executeUpdate("update t_user set name='王五' where id=?",2);
		dao.executeUpdate("update t_user set name='王五' where id=#{id}",
				new SqlParam("id", 2));
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
