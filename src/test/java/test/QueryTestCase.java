package test;

import java.util.Arrays;
import java.util.List;

import io.itit.smartjdbc.QueryWhere;
import io.itit.smartjdbc.SqlParam;
import io.itit.smartjdbc.enums.OrderBy;
import io.itit.smartjdbc.enums.SqlOperator;
import io.itit.smartjdbc.util.DumpUtil;
import test.dao.BizDAO;
import test.entity.Article;
import test.entity.User;
import test.entity.query.ArticleQuery;
import test.entity.query.UserQuery;

/**
 * 
 * @author skydu
 *
 */
public class QueryTestCase extends BaseTestCase{
	static {
	    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
	}
	//
	BizDAO dao;
	//
	public QueryTestCase() {
		dao=new BizDAO();
	}
	//
	/**
	 * 
	 */
	public void testGetById() {
		for(int i=0;i<10;i++) {
			dao.getById(User.class, 1);
		}
	}
	
	public void testQuery() {
		/**查询姓名等于‘张三’的用户*/
		dao.getEntity(User.class,QueryWhere.create().eq("name", "张三"));
		
		/**查询姓名不等于‘张三’的用户*/
		dao.getEntity(User.class,QueryWhere.create().ne("name", "张三"));
		
		/**查询年龄小于25岁的用户*/
		dao.getEntity(User.class,QueryWhere.create().lt("age", 25));
		
		/**查询年龄小于等于25岁的用户*/
		dao.getEntity(User.class,QueryWhere.create().le("age", 25));
		
		/**查询年龄大于25岁的用户*/
		dao.getEntity(User.class,QueryWhere.create().gt("age", 25));
		
		/**查询年龄大于等于25岁的用户*/
		dao.getEntity(User.class,QueryWhere.create().ge("age", 25));
	}
	
	/**
	 * 
	 */
	public void testQueryLike() {
		/**查询姓名包含'张'的用户*/
		dao.getEntity(User.class,QueryWhere.create().like("name","张"));
		
		/**查询姓名是以'张'开头的用户*/
		dao.getEntity(User.class,QueryWhere.create().likeLeft("name","张"));
		
		/**查询姓名是以'张'结尾的用户*/
		dao.getEntity(User.class,QueryWhere.create().likeRight("name","张"));
		
		/**查询姓名不包含'张'的用户*/
		dao.getEntity(User.class,QueryWhere.create().notLike("name","张"));
		
		/**查询姓名不是以'张'开头的用户*/
		dao.getEntity(User.class,QueryWhere.create().notLikeLeft("name","张"));
		
		/**查询姓名不是以'张'结尾的用户*/
		dao.getEntity(User.class,QueryWhere.create().notLikeRight("name","张"));
	}
	
	public void testQueryIn() {
		dao.getEntity(User.class,QueryWhere.create().in("status",Arrays.asList(1,2,3)));
		dao.getEntity(User.class,QueryWhere.create().in("status",new int[] {1,2}));
		dao.getEntity(User.class,QueryWhere.create().in("status",new byte[] {1,2}));
		dao.getEntity(User.class,QueryWhere.create().in("status",new long[] {1,2}));
		dao.getEntity(User.class,QueryWhere.create().in("name",Arrays.asList("张三","张三2")));
		dao.getEntity(User.class,QueryWhere.create().in("name",new String[] {"张三","张三2"}));
	}
	
	public void testQueryNotIn() {
		dao.getEntity(User.class,QueryWhere.create().notin("status",Arrays.asList(1,2,3)));
		dao.getEntity(User.class,QueryWhere.create().notin("status",new int[] {1,2}));
		dao.getEntity(User.class,QueryWhere.create().notin("status",new byte[] {1,2}));
		dao.getEntity(User.class,QueryWhere.create().notin("status",new long[] {1,2}));
		dao.getEntity(User.class,QueryWhere.create().notin("name",Arrays.asList("张三","张三2")));
		dao.getEntity(User.class,QueryWhere.create().notin("name",new String[] {"张三","张三2"}));
	}
	
	public void testQueryWhereSql() {
		dao.getEntity(User.class,QueryWhere.create().whereSql("a.name=?","张三"));
		dao.getEntity(User.class,QueryWhere.create().whereSql("a.name='张三'"));
		dao.getEntity(User.class,QueryWhere.create().whereSql("a.name=#{name}",
				new SqlParam("name", "张三")));
		dao.getEntity(User.class,QueryWhere.create().whereSql("a.name=${name}",
				new SqlParam("name", "张三")));
		dao.getEntity(User.class,QueryWhere.create().whereSql("a.status=${status}",
				new SqlParam("status", 1)));
		dao.getEntity(User.class,QueryWhere.create().whereSql("a.status=${status} or a.name like concat('%',#{name},'%')",
				new SqlParam("status", 1),
				new SqlParam("name", "zhang")));
		dao.getEntity(User.class,QueryWhere.create().whereSql("a.status in ${status} or a.name like concat('%',#{name},'%')",
				new SqlParam("status", Arrays.asList(1,2,3)),
				new SqlParam("name", "zhang")));
	}
	
	public void testQueryOrderByLimit() {
		dao.getEntity(User.class,QueryWhere.create().
				orderBy("id asc").
				orderBy("name desc").
				limit(0, 10));
		
	}
	
	/**查询用户列表*/
	public void testGetUsers() {
		UserQuery query=new UserQuery();
		query.setUserName("test");
		query.setNameOrUserName("t");
		query.orderBy("name", OrderBy.ASC);
		query.orderBy("id", OrderBy.DESC);
		List<User> list=dao.getList(query,"createTime","updateTime");
		System.out.println(DumpUtil.dump(list));
	}
	
	/**查询用户列表*/
	public void testQueryList() {
		List<User> list=dao.getList(User.class, 
				QueryWhere.create().where("name",SqlOperator.LIKE,"王"));
		System.out.println(DumpUtil.dump(list));
	}
	
	public void testGetUsersWithInnerJoins() {
		UserQuery query=new UserQuery();
		query.setCreateUserName("root");
		query.setDepartmentName("技术");
		List<User> list=dao.getList(query,"createTime","updateTime");
		System.out.println(DumpUtil.dump(list));
	}
	
	public void testQueryInList() {
		UserQuery query=new UserQuery();
		query.setStatusInList(Arrays.asList(1,2));
		List<User> list=dao.getList(query);
		System.out.println(DumpUtil.dump(list));
	}
	
	public void testQueryNotInList() {
		UserQuery query=new UserQuery();
		query.setStatusNotInList(Arrays.asList(1,2));
		List<User> list=dao.getList(query);
		System.out.println(DumpUtil.dump(list));
	}
	
	public void testQueryRoleId() {
		UserQuery query=new UserQuery();
		query.setRoleId(1);
		List<User> list=dao.getList(query);
		System.out.println(DumpUtil.dump(list));
	}
	
	public void testQueryStatusOrRoleId() {
		UserQuery query=new UserQuery();
		query.setStatusOrRoleId(true);
		query.param("orStatus",1);
		query.param("orRoleId",1);
		List<User> list=dao.getList(query);
		System.out.println(DumpUtil.dump(list));
	}
	
	/**查询用户列表总数*/
	public void testGetUsersCounts() {
		UserQuery query=new UserQuery();
		query.setUserName("test");
		int count=dao.getListCount(query);
		System.out.println(count);
	}
	
	/**当然写sql也是支持的*/
	public void testQueryUsers() {
		List<User> users=dao.queryList(User.class, 
				"select * from t_user where name like concat('%'?,'%') and id=?", 
				"liu",
				1);
		System.out.println(DumpUtil.dump(users));
	}
	
	public void testQueryUsers2() {
		List<User> users=dao.queryList(User.class, 
				"select * from t_user where name like concat('%',#{name},'%') and id=#{id}", 
				new SqlParam("name", "liu"),
				new SqlParam("id", 1));
		System.out.println(DumpUtil.dump(users));
	}
	
	public void testQueryUsersCount() {
		int count=dao.queryCount(
				"select count(1) from t_user where name like concat('%',?,'%') and id=?", 
				"liu",
				1);
		System.out.println(count);
	}
	
	public void testGetUserIds() {
		List<Integer> userIds=dao.queryForIntegers("select id from t_user where id=#{id}",
				new SqlParam("id", 1));
		System.out.println(DumpUtil.dump(userIds));
		//
		userIds=dao.queryForIntegers("select id from t_user where id=?",
				1);
		System.out.println(DumpUtil.dump(userIds));
	}
	
	public void testQueryUsersCount2() {
		int count=dao.queryCount(
				"select count(1) from t_user where name like concat('%',#{name},'%') and id=#{id}", 
				new SqlParam("name", "张"),
				new SqlParam("id", 1));
		System.out.println(count);
	}
	//
	/**
	 * 查询文章详情
	 */
	public void testGetArticle() {
		Article info=dao.getById(Article.class,1);
		System.out.println(DumpUtil.dump(info));
	}
	
	/**
	 * 查询userId为1的用户喜爱的文章列表
	 */
	public void testGetUserLikeArticles() {
		ArticleQuery query=new ArticleQuery();
		query.setLikeUserId(1);
		query.pageSize=20;
		List<Article> list=dao.getList(query);
		System.out.println(DumpUtil.dump(list));
	}
	//
	public void testSum() {
		Long sum=dao.sum(User.class,Long.class, "articleNum", QueryWhere.create());
		System.out.println("sum:"+sum);
	}
	
	public void testSumByQuery() {
		UserQuery query=new UserQuery();
		query.setUserName("liu");
		Long sum=dao.sum(query,Long.class, "articleNum");
		System.out.println("sum:"+sum);
	}
	//
	public void testQueryUserList() {
		UserQuery query=new UserQuery();
		query.setNameOrUserName("张");
		List<User> users=dao.getList(query);
		System.out.println(DumpUtil.dump(users));
	}
	
	public void testQueryWithWhereSql() {
		UserQuery query=new UserQuery();
		query.setNameOrUserName("zhang");
		List<User> users=dao.getList(query);
		System.out.println(DumpUtil.dump(users));
	}
	
	public void testOr() {
		ArticleQuery query=new ArticleQuery();
		query.setOrCreateUserId(1);
		query.setOrStatusList(new int[] {2,3});
		List<Article> list=dao.getList(query);
		System.out.println(DumpUtil.dump(list));
	}
	//
	public void testGetArticleWithJoins() {
		ArticleQuery query=new ArticleQuery();
		query.setCreateUserDepartmentName("技术");
		query.setCreateUserDepartmentName2("技术");
		List<Article> list=dao.getList(query);
		System.out.println(DumpUtil.dump(list));
	}
}
