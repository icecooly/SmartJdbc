package test;

import java.util.Arrays;
import java.util.List;

import io.itit.smartjdbc.SqlParam;
import io.itit.smartjdbc.enums.ConditionType;
import io.itit.smartjdbc.enums.OrderByType;
import io.itit.smartjdbc.enums.SqlOperator;
import io.itit.smartjdbc.provider.where.QueryWhere;
import io.itit.smartjdbc.provider.where.Where;
import io.itit.smartjdbc.provider.where.Where.Condition;
import io.itit.smartjdbc.provider.where.Where.JsonContain;
import io.itit.smartjdbc.util.DumpUtil;
import test.dao.BizDAO;
import test.domain.entity.Article;
import test.domain.entity.User;
import test.domain.query.ArticleQuery;
import test.domain.query.UserComplexQuery;
import test.domain.query.UserComplexQuery.NameOrUserNameOrDeptName;
import test.domain.query.UserComplexQuery.StatusAndMobile;
import test.domain.query.UserQuery;
import test.domain.vo.UserSimple;

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
		for(int i=0;i<1;i++) {
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
		dao.getList(User.class,QueryWhere.create().in("status",Arrays.asList(1,2,3)));
		dao.getList(User.class,QueryWhere.create().in("status",new int[] {1,2}));
		dao.getList(User.class,QueryWhere.create().in("status",new byte[] {1,2}));
		dao.getList(User.class,QueryWhere.create().in("status",new long[] {1,2}));
		dao.getList(User.class,QueryWhere.create().in("name",Arrays.asList("张三","张三2")));
		dao.getList(User.class,QueryWhere.create().in("name",new String[] {"张三","张三2"}));
		UserQuery query=new UserQuery();
		query.setStatusInList(Arrays.asList(1,2,3));
		dao.getList(query);
	}
	
	public void testQueryNotIn() {
		dao.getList(User.class,QueryWhere.create().notin("status",Arrays.asList(1,2,3)));
		dao.getList(User.class,QueryWhere.create().notin("status",new int[] {1,2}));
		dao.getList(User.class,QueryWhere.create().notin("status",new byte[] {1,2}));
		dao.getList(User.class,QueryWhere.create().notin("status",new long[] {1,2}));
		dao.getList(User.class,QueryWhere.create().notin("name",Arrays.asList("张三","张三2")));
		dao.getList(User.class,QueryWhere.create().notin("name",new String[] {"张三","张三2"}));
	}
	
	public void testJsonContains() {
		dao.getList(User.class,QueryWhere.create().jsonContainsAny("role_id_list",Arrays.asList(1,2,3)));
		dao.getList(User.class,QueryWhere.create().jsonContainsAny("role_id_list",new int[] {1,2}));
		dao.getList(User.class,QueryWhere.create().jsonContainsAny("role_id_list",new byte[] {1,2}));
		dao.getList(User.class,QueryWhere.create().jsonContainsAny("role_id_list",new long[] {1,2}));
		//
		UserQuery query=new UserQuery();
		query.setRoleId(1);
		dao.getList(query);
		//
		query=new UserQuery();
		query.setGtAge(10);
		query.setRoleIdList(new Integer[] {1,2});
		dao.getList(query);
	}
	
	public void testJsonContainsObjectArray() {
		JsonContain jsonContain=new JsonContain();
		jsonContain.objectField="id";
		dao.getEntity(Article.class,QueryWhere.create().jsonContainsAny(null,"favorite_user_list",new int[] {1,2},jsonContain));
		//
		jsonContain=new JsonContain();
		jsonContain.objectField="userName";
		dao.getEntity(Article.class,QueryWhere.create().jsonContainsAny(null,"favorite_user_list",new String[] {"liubei","guanyu"},jsonContain));
	}
	
	public void testNotJsonContains() {
		dao.getEntity(User.class,QueryWhere.create().jsonNotContainsAny("role_id_list",Arrays.asList(1,2,3)));
		dao.getEntity(User.class,QueryWhere.create().jsonNotContainsAny("role_id_list",new int[] {1,2}));
		dao.getEntity(User.class,QueryWhere.create().jsonNotContainsAny("role_id_list",new byte[] {1,2}));
		dao.getEntity(User.class,QueryWhere.create().jsonNotContainsAny("role_id_list",new long[] {1,2}));
		//
		UserQuery query=new UserQuery();
		query.setNotRoleId(1);
		dao.getList(query);
		//
		query=new UserQuery();
		query.setNotRoleIdList(new Integer[] {1,2});
		dao.getList(query);
	}
	
	public void testQueryIsNull() {
		dao.getEntity(User.class,QueryWhere.create().isNull("name"));
		dao.getEntity(User.class,QueryWhere.create().isNotNull("name"));
		dao.getEntity(User.class,QueryWhere.create().where("name",null));
	}
	
	public void testIsNull() {
		UserQuery query=new UserQuery();
		query.setUserNameIsNull(true);
		dao.getEntity(query);
	}
	
	public void testQueryWhereSql() {
		dao.getEntity(User.class,QueryWhere.create().whereSql("a.name=?","test"));
		dao.getEntity(User.class,QueryWhere.create().whereSql("a.name='张三'"));
		dao.getEntity(User.class,QueryWhere.create().whereSql("a.name=#{name}",
				new SqlParam("name", "张三")));
		dao.getEntity(User.class,QueryWhere.create().whereSql("a.name='${name}'",
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
	
	public void testQueryWhereOr() {
		dao.getEntity(User.class,QueryWhere.create(ConditionType.OR).
				like("user_name","root").
				like("name","root")
		);
	}
	
	public void testQueryWhereAndOr() {
		dao.getEntity(User.class,QueryWhere.create().
				eq("status", 1).
				or(new Where().
						like("user_name","root").
						like("l1","name","技术")
				)
		);
	}
	
	public void testQueryWhereAndOrAnd() {
		dao.getEntity(User.class,QueryWhere.create().
				eq("status", 1).
				or(new Where().
						like("user_name","root").
						like("l1","name","技术").
						and(new Where().
								like("user_name","root").
								like("name","root"))
				)
		);
	}
	
	/**查询用户列表*/
	public void testUserQuery() {
		UserQuery query=new UserQuery();
		query.setUserName("root");
		query.setName("r");
		query.orderBy("name", OrderByType.ASC.name());
		query.orderBy("id", OrderByType.DESC.name());
		List<User> list=dao.getList(query,"createTime","updateTime");
		System.out.println(DumpUtil.dump(list));
	}
	
	public void testUserQueryInList() {
		UserQuery query=new UserQuery();
		query.setStatusInList(Arrays.asList(1,2));
		System.out.println(DumpUtil.dump(dao.getList(query)));
		//
		query=new UserQuery();
		query.setStatusInList2(new Integer[] {1,2});
		dao.getList(query);
		System.out.println(DumpUtil.dump(dao.getList(query)));
	}
	
	/**AND OR嵌套查询*/
	public void testUserComplexQuery() {
		UserComplexQuery query=new UserComplexQuery();
		query.setGtAge(10);
		NameOrUserNameOrDeptName nameOrUserName=new NameOrUserNameOrDeptName();
		nameOrUserName.setName("t");
		nameOrUserName.setUserName("e");
		nameOrUserName.setDeptName("技术");
		StatusAndMobile statusAndMobile=new StatusAndMobile();
		statusAndMobile.setMobileNo("130");
		statusAndMobile.setStatus(1);
		nameOrUserName.setStatusAndMobile(statusAndMobile);
		query.setNameOrUserName(nameOrUserName);
		List<User> list=dao.getList(query);
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
				"select * from t_user where name like concat('%',?,'%') and id=?", 
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
	
	public void testQueryUsers3() {
		List<UserSimple> users=dao.queryList(UserSimple.class, 
				"select *,1 as test from t_user");
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
		query.setPageSize(20);
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
		query.orderBy("id", "desc");
		query.orderBy("name", "asc");
		List<User> users=dao.getList(query);
		System.out.println(DumpUtil.dump(users));
	}
	
	public void testOr() {
		ArticleQuery query=new ArticleQuery();
		query.setTitleOrContent("Java");
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
	
	public void testQueryDouble() {
		UserQuery query=new UserQuery();
		query.setHeightStart(1.65);
		List<User> list=dao.getList(query);
		System.out.println(DumpUtil.dump(list));
	}
	
	public void testQueryLong() {
		UserQuery query=new UserQuery();
		query.setNo(5L);
		List<User> list=dao.getList(query);
		System.out.println(DumpUtil.dump(list));
	}
	
	public void testQueryBoolean() {
		Condition c=new Condition();
		c.key="is_student";
		c.keyCast="::int::bool";
		c.operator=SqlOperator.EQ;
		c.value=true;
		List<User> list=dao.getList(User.class, QueryWhere.create().where(c));
		System.out.println(DumpUtil.dump(list));
	}
	
	public void testWhere() {
		Where w=new Where();
		Where and1=new Where();
		Where and2=new Where();
		and2.where("user_name", SqlOperator.LIKE,"test");
		and1.and(and2);
		w.and(and1);
		dao.getList(User.class, QueryWhere.create().and(w));
		//
		w=new Where();
		and1=new Where();
		and2=new Where();
		and1.and(and2);
		w.and(and1);
		dao.getList(User.class, QueryWhere.create().and(w));
		//
		w=new Where();
		and1=new Where();
		and2=new Where();
		Where or1=new Where();
		Where or2=new Where();
		or2.where("user_name", SqlOperator.LIKE,"test");
		or2.where("name", SqlOperator.LIKE,"123");
		and1.and(and2);
		and2.or(or1);
		or1.or(or2);
		w.and(and1);
		w.and(new Where());
		w.or(new Where());
		dao.getList(User.class, QueryWhere.create().and(w));
	}
}
