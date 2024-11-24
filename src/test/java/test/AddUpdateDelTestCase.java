package test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import io.itit.smartjdbc.provider.where.QueryWhere;
import test.dao.BizDAO;
import test.domain.entity.Article;
import test.domain.entity.User;

/**
 * 
 * @author skydu
 *
 */
public class AddUpdateDelTestCase extends BaseTestCase{
	//
	static {
	    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
	}
	//
	BizDAO dao;
	//
	public AddUpdateDelTestCase() {
		dao=new BizDAO();
	}
	//
	/**新增用户*/
	public void testAddUser() {
		User user=new User();
		user.setName("王五4");
		user.setUserName("wangwu4");
		user.setAge(20);
		user.setDepartmentId(1);
		user.setRoleIdList(Arrays.asList(1,2,3));
		user.setMobileNo("13012345678");
		user.setHeight(1.78);
		user.setCreateTime(new Date());
		user.setArticleNum(10000L);
		user.setIsStudent(true);
		user.setShortArray(new Short[] {1,2});
		user.setIntArray(new Integer[] {3,4,5});
		user.setLongArray(new Long[] {300000L,400000L,500000L});
//		user.setDoubleArray(new Double[] {1.234567,2.456789}); //not support
		user.setFloatArray(new Float[] {1.234f,2.456f});
		user.setStringArray(new String[] {"test","你好"});
		//
		user.setId(dao.insert(user));
		System.out.println(user.getId());
	}
	
	/**
	 * 更新所有字段
	 */
	public void testUpdateUser() {
		User user=dao.getById(User.class, 2);
		user.setGender(User.GENDER_女);
		user.setDepartmentId(2);
		user.setHeight(null);
		user.setAge(18);
		dao.update(user);
		//
		dao.executeUpdate("update t_user set name='王五2' where id=?",1);
	}
	
	public void testUpdateId() {
		User user=dao.getById(User.class, 2);
		user.setId(100);
		dao.update(user, "id");
	}
	
	/**
	 * 只更新age字段
	 */
	public void testUpdateUserAge() {
		User user=dao.getById(User.class, 2);
		user.setAge(30);
		dao.update(user, "age");
	}
	
	/**
	 * 只更新height字段
	 */
	public void testUpdateUserHeight() {
		User user=dao.getById(User.class, 2);
		user.setHeight(null);
		dao.update(user, "height");
	}
	
	/**
	 * 只更新roleIdList字段
	 */
	public void testUpdateRoleIdList() {
		User user=dao.getById(User.class, 2);
		user.setRoleIdList(Arrays.asList(3,4,5));
		dao.update(user, "roleIdList");
	}
	
	/**
	 * 只更新intArray,stringArray字段
	 */
	public void testUpdateStringArray() {
		User user=dao.getById(User.class, 2);
		user.setStringArray(new String[] {"s9","s8"});
		user.setIntArray(null);
		dao.update(user, "intArray", "stringArray");
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
		int id=dao.insert(bean);
		System.out.println(id);
	}
	
	/**
	 * 
	 */
	public void testDelete() {
		dao.delete(User.class, QueryWhere.create().where("id", 11));
	}
}
