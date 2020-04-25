# 轻量级ORM框架SmartJdbc

# 1 简介

SmartJdbc一个简单易用的ORM框架，它支持sql构建、sql执行、命名参数绑定、查询结果自动映射和通用DAO。

SmartJdbc可以让你不用写DAO,不用写SQL,不用写XML。

# 2 入门

## 2.1 安装

要使用 SmartJdbc， 只需将 SmartJdbc-2.0.7.jar 文件置于 classpath 中即可。

如果使用 Maven 来构建项目，则需将下面的 dependency 代码置于 pom.xml 文件中：

```xml
<dependency>
    <groupId>com.github.icecooly</groupId>
    <artifactId>SmartJdbc</artifactId>
    <version>2.0.7</version>
</dependency>
<dependency>
    <groupId>com.github.icecooly</groupId>
    <artifactId>SmartJdbc-Spring</artifactId>
    <version>1.0.1</version>
</dependency>
```

如果使用 Gradle 来构建项目，则需将下面的代码置于 build.gradle 文件的 dependencies 代码块中：

```groovy
compile 'com.github.icecooly:SmartJdbc:2.0.7'
compile 'com.github.icecooly:SmartJdbc-Spring:1.0.1'
```

# 3 例子

## 3.1 简单增删改
```java
@Data
@Entity(tableName = "t_user")
public class User{
	
	@PrimaryKey
	private Long id;
	
	/**用户名*/
	private String userName;
	
	/**姓名*/
	private String name;
	
	/**状态*/
	private Integer status;
	
	/**所属部门*/
	@ForeignKey(entityClass=Department.class)
	private Integer departmentId;
	
	/**角色列表*/
	private List<Integer> roleIdList;
	
	/**最后登录时间*/
	private Date lastLoginTime;
```

新增

```java
User user=new User();
user.setName("张三");
user.setUserName("zhangsan");
user.roleIdList=Arrays.asList(1,2,3);//会自动转化为JSON.toString(roleIdList)
user.setId((long)dao.add(user));
```

修改

```java

/**更新所有字段*/
User user=dao.getById(User.class, 1);
user.setName("张三2");
user.setUserName("zhangsan2");
dao.update(user);

/**只更新非空字段*/
User user=new User();
user.setId(1L);
user.setDepartmentId(3);
user.setMobileNo("130000000000");
dao.updateExcludeNull(user);

```

删除

```java
dao.deleteById(User.class, 1);
```

## 3.2 基本查询

```java
除了基本数据类型会自动映射外,复杂数据类型List<对象>,Set<对象>等都可以自动映射
User user=dao.getById(User.class, 1);
User user=dao.getEntity(User.class,QueryWhere.create().where("userName", "zhangsan"));
result:{
	"id":1,
	"name":"张三",
	"userName":"zhangsan",
	"roleIdList":[
		1,
		2,
		3
	],
	"createTime":"2020-04-12 12:08:43",
	"updateTime":"2020-04-25 18:36:19",
}
```

## 3.3 列表查询

```java
//查询用户名包含test的用户列表,按照创建时间降序
UserQuery query=new UserQuery();
query.userName="test";
List<User> list=dao.getList(query);
```

## 3.4 复杂查询(如果数据库设计是三范式，不冗余存储数据，查询时可以自动join，不用手工写sql)

```java
@Data
public class UserQuery extends Query<User>{

	@QueryField
	private String userName;
	
	@QueryField
	private Integer gender;
	
	@QueryField(field ="status",operator=SqlOperator.IN)
	private List<Integer> statusInList;
	
	@QueryField(foreignKeyFields="departmentId",field="name",operator=SqlOperator.LIKE)
	private String departmentName;
}
UserQuery query=new UserQuery();
query.setDepartmentName("技术部");
query.pageSize=20;
List<User> list=dao.getList(query);
```
更多复杂查询可参考test/QueryTestCase.java

# 4 Spring Boot中使用SmartJdbc
SmartJdbcConfig.java
```java
@Configuration
@EnableTransactionManagement
@AutoConfigureAfter({DataSourceConfig.class})
public class SmartJdbcConfig implements TransactionManagementConfigurer {
	//
	private static final Logger logger = LoggerFactory.getLogger(SmartJdbcConfig.class);
	//
	@Autowired
	private DataSource dataSource;
	//
	@PostConstruct
	public void init() {
		SqlSessionFactory sessionFactory=new SqlSessionFactory();
		sessionFactory.setDataSource(dataSource);
		ConnectionManager.setTransactionManager(sessionFactory);
		Config.addSqlInterceptor(sessionFactory);
	}
	
	@Bean(name="annotationDrivenTransactionManager")
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return new DataSourceTransactionManager(dataSource);
	}
}
```

# 5 其他

* [项目主页](https://github.com/icecooly/SmartJdbc)

# 6 更新日志
