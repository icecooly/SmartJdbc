package test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.itit.smartjdbc.Config;
import io.itit.smartjdbc.DAOInterceptor;
import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.QueryWhere;
import io.itit.smartjdbc.SqlParam;
import io.itit.smartjdbc.util.DumpUtil;
import test.dao.BizDAO;
import test.entity.Article;
import test.entity.User;
import test.entity.info.ArticleInfo;
import test.entity.info.UserInfo;
import test.entity.info.UserSimpleInfo;
import test.entity.info.UserStat;
import test.entity.query.ArticleInfoQuery;
import test.entity.query.UserInfoQuery;
import test.entity.query.UserQuery;
import test.entity.query.UserStatQuery;

/**
 * 
 * @author skydu
 *
 */
public class DAOTestCase extends BaseTestCase{
	//
	BizDAO dao;
	//
	public DAOTestCase() {
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
		user.name="张三";
		user.userName="zhangsan";
		user.roleIdList=Arrays.asList(1,2,3);
		user.id=dao.add(user);
		System.out.println(user.id);
	}
	
	/**
	 * 
	 */
	public void testGetById() {
		User user=dao.getById(User.class, 1);
		System.out.println(DumpUtil.dump(user));
	}
	
	public void testQuery() {
		dao.getEntity(User.class,QueryWhere.create().where("user_name", "test"));
		dao.getEntity(User.class,QueryWhere.create().eq("user_name", "test"));
		dao.getEntity(User.class,QueryWhere.create().ne("user_name", "test"));
		dao.getEntity(User.class,QueryWhere.create().lt("status", 1));
		dao.getEntity(User.class,QueryWhere.create().le("status", 1));
		dao.getEntity(User.class,QueryWhere.create().gt("status", 1));
		dao.getEntity(User.class,QueryWhere.create().ge("status", 1));
	}
	
	/**
	 * 
	 */
	public void testQueryLike() {
		dao.getEntity(User.class,QueryWhere.create().like("user_name","zhangsan"));
		dao.getEntity(User.class,QueryWhere.create().likeLeft("user_name","yu"));
		dao.getEntity(User.class,QueryWhere.create().likeRight("user_name","yu"));
		dao.getEntity(User.class,QueryWhere.create().notLike("user_name","zhangsan"));
		dao.getEntity(User.class,QueryWhere.create().notLikeLeft("user_name","yu"));
		dao.getEntity(User.class,QueryWhere.create().notLikeRight("user_name","yu"));
	}
	
	/**
	 * 
	 */
	public void testQueryIn() {
		dao.getEntity(User.class,QueryWhere.create().in("status",Arrays.asList(1,2,3)));
		dao.getEntity(User.class,QueryWhere.create().in("user_name",Arrays.asList("zhangsan","zhangsan2")));
		dao.getEntity(User.class,QueryWhere.create().in("status",new int[] {1,2}));
		dao.getEntity(User.class,QueryWhere.create().in("status",new byte[] {1,2}));
		dao.getEntity(User.class,QueryWhere.create().in("status",new long[] {1,2}));
		dao.getEntity(User.class,QueryWhere.create().in("user_name",new String[] {"zhangsan","zhangsan2"}));
	}
	
	/**
	 * 
	 */
	public void testQueryNotIn() {
		dao.getEntity(User.class,QueryWhere.create().notin("status",Arrays.asList(1,2,3)));
		dao.getEntity(User.class,QueryWhere.create().notin("user_name",Arrays.asList("zhangsan","zhangsan2")));
		dao.getEntity(User.class,QueryWhere.create().notin("status",new int[] {1,2}));
		dao.getEntity(User.class,QueryWhere.create().notin("status",new byte[] {1,2}));
		dao.getEntity(User.class,QueryWhere.create().notin("status",new long[] {1,2}));
		dao.getEntity(User.class,QueryWhere.create().notin("user_name",new String[] {"zhangsan","zhangsan2"}));
	}
	
	public void testQueryWhereSql() {
		dao.getEntity(User.class,QueryWhere.create().whereSql("user_name=?","zhangsan"));
		dao.getEntity(User.class,QueryWhere.create().whereSql("user_name='zhangsan'"));
		dao.getEntity(User.class,QueryWhere.create().whereSql("user_name=#{userName}",
				new SqlParam("userName", "zhangsan")));
		dao.getEntity(User.class,QueryWhere.create().whereSql("user_name=${userName}",
				new SqlParam("userName", "zhangsan")));
		dao.getEntity(User.class,QueryWhere.create().whereSql("status=${status}",
				new SqlParam("status", 1)));
		dao.getEntity(User.class,QueryWhere.create().whereSql("status=${status} or user_name like concat('%',#{userName},'%')",
				new SqlParam("status", 1),
				new SqlParam("userName", "yu")));
		dao.getEntity(User.class,QueryWhere.create().whereSql("status in ${status} or user_name like concat('%',#{userName},'%')",
				new SqlParam("status", Arrays.asList(1,2,3)),
				new SqlParam("userName", "yu")));
	}
	
	/**查询用户列表*/
	public void testGetUsers() {
		UserQuery query=new UserQuery();
		query.userName="test";
		query.nameOrUserName="t";
		List<User> list=dao.getList(query,"createTime","updateTime");
		System.out.println(DumpUtil.dump(list));
	}
	
	public void testQueryInList() {
		UserQuery query=new UserQuery();
		query.statusInList=Arrays.asList(1,2);
		dao.getList(query);
	}
	
	public void testQueryNotInList() {
		UserQuery query=new UserQuery();
		query.statusNotInList=Arrays.asList(1,2);
		dao.getList(query);
	}
	
	/**查询用户列表总数*/
	public void testGetUsersCounts() {
		UserQuery query=new UserQuery();
		query.userName="test";
		int count=dao.getListCount(query);
		System.out.println(count);
	}
	
	/**当然写sql也是支持的*/
	public void testQueryUsers() {
		List<User> users=dao.queryList(User.class, 
				"select * from t_user where user_name like concat('%'?,'%') and id=?", 
				"liu",
				1);
		System.out.println(DumpUtil.dump(users));
	}
	
	public void testQueryUsers2() {
		List<User> users=dao.queryList(User.class, 
				"select * from t_user where user_name like concat('%',#{userName},'%') and id=#{id}", 
				new SqlParam("userName", "liu"),
				new SqlParam("id", 1));
		System.out.println(DumpUtil.dump(users));
	}
	
	public void testQueryUsersCount() {
		int count=dao.queryCount(
				"select count(1) from t_user where user_name like concat('%',?,'%') and id=?", 
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
				"select count(1) from t_user where user_name like concat('%',#{userName},'%') and id=#{id}", 
				new SqlParam("userName", "liu"),
				new SqlParam("id", 1));
		System.out.println(count);
	}
	
	public void testUpdateUser() {
		User user=dao.getById(User.class, 1);
		user.gender=User.GENDER_男;
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
		bean.title="桑切斯遭孤立?队友吃饭他加练 为融入曼联拼了";
		bean.content="上周，有关桑切斯的负面消息很多，英媒体爆料他在曼联阵中独来独往、总是一个人吃饭，"
				+ "无法融入到集体中，还有媒体称桑切斯甚至在比赛中遭到了队友的当面批评，"
				+ "这些让外界对桑切斯在曼联的未来表示了担忧。不过，英媒体《每日镜报》"
				+ "今日透露桑切斯在曼联并没有被孤立，智利人也没有刻意躲避队友，"
				+ "他之所以经常一个人吃饭竟是这样的原因。";
		bean.createUserId=1;
		bean.updateUserId=1;
		bean.status=Article.STATUS_待审核;
		int id=dao.add(bean);
		System.out.println(id);
	}
	//
	public void testGetUserInfoById() {
		dao.getById(UserInfo.class,1);
	}
	//
	/**查询角色名称是总监是用户列表*/
	public void testGetUserInfos() {
		UserInfoQuery query=new UserInfoQuery();
		query.roleName="总监";
		List<UserInfo> users=dao.getList(query);
		System.out.println(DumpUtil.dump(users));
	}
	//
	public void testGetUserInfosCount() {
		UserInfoQuery query=new UserInfoQuery();
		query.gender=1;
		query.roleName="总经理";
		dao.getListCount(query);
	}
	//
	public void testGetUserInfosOrderBySortFields() {
		UserInfoQuery query=new UserInfoQuery();
		// order by name asc,roleId desc
		query.gender=Query.SORT_TYPE_ASC;
		query.nameSort=Query.SORT_TYPE_ASC;
		query.roleIdSort=Query.SORT_TYPE_DESC;
		List<UserInfo> users=dao.getList(query);
		System.out.println(DumpUtil.dump(users));
	}
	//
	public void testGetArticleInfo() {
		dao.getById(ArticleInfo.class,1);
	}
	//
	public void testGetArticleInfos() {
		ArticleInfoQuery query=new ArticleInfoQuery();
		query.createUserName="刘备";
		query.statusList=new int[] {1,2};
		List<ArticleInfo> users=dao.getList(query);
		System.out.println(DumpUtil.dump(users));
	}
	//
	public void testGetArticleInfosCount() {
		ArticleInfoQuery query=new ArticleInfoQuery();
		query.createUserName="刘备";
		query.statusList=new int[] {ArticleInfo.STATUS_审核通过,ArticleInfo.STATUS_审核未通过};
		System.out.println(DumpUtil.dump(dao.getListCount(query)));
	}
	/**
	 * 查询文章详情
	 */
	public void testArticleInfo() {
		ArticleInfo info=dao.getById(ArticleInfo.class,1);
		System.out.println(DumpUtil.dump(info));
	}
	
	/**
	 * 查询userId为1的用户喜爱的文章列表
	 */
	public void testGetUserLikeArticles() {
		ArticleInfoQuery query=new ArticleInfoQuery();
		query.likeUserId=1;
		query.pageSize=20;
		List<ArticleInfo> list=dao.getList(query);
		System.out.println(DumpUtil.dump(list));
	}
	//
	public void getUserStats() {
		UserStatQuery query=new UserStatQuery();
		List<UserStat> list=dao.getList(query);
		System.out.println(DumpUtil.dump(list));
	}
	
	public void testSum() {
		Long sum=dao.sum(User.class,Long.class, "roleId", QueryWhere.create());
		System.out.println("sum:"+sum);
	}
	
	public void testSumByQuery() {
		UserQuery query=new UserQuery();
		query.userName="liu";
		Long sum=dao.sum(query,Long.class, "roleId");
		System.out.println("sum:"+sum);
	}
	
	public void testGetUserSimpleInfo() {
		List<UserSimpleInfo> list=dao.getList(UserSimpleInfo.class,QueryWhere.create());
		System.out.println(DumpUtil.dump(list));
	}
	//
	public void testQueryUserInfoList() {
		UserInfoQuery query=new UserInfoQuery();
		query.nameOrUserName="关";
		List<UserInfo> users=dao.getList(query);
		System.out.println(DumpUtil.dump(users));
	}
	
	public void testQueryWithWhereSql() {
		UserInfoQuery query=new UserInfoQuery();
		query.nameOrUserName="zhang";
		List<UserInfo> users=dao.getList(query);
		System.out.println(DumpUtil.dump(users));
	}
	
	public void testOr() {
		ArticleInfoQuery query=new ArticleInfoQuery();
		query.orCreateUserId= 1;
		query.orStatusList=new int[] {2,3};
		List<ArticleInfo> users=dao.getList(query);
		System.out.println(DumpUtil.dump(users));
	}
}
