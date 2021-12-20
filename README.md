# 轻量级ORM框架SmartJdbc

# 1 简介

SmartJdbc一个简单易用的ORM框架，它支持sql构建、sql执行、命名参数绑定、查询结果自动映射和自动跨表join等。

不用写DAO,不用写SQL,不用写XML。

# 2 入门

## 2.1 安装

要使用 SmartJdbc， 只需将 SmartJdbc-2.0.38.jar 文件置于 classpath 中即可。

如果使用 Maven 来构建项目，则需将下面的 dependency 代码置于 pom.xml 文件中：

```xml
<dependency>
    <groupId>com.github.icecooly</groupId>
    <artifactId>smartjdbc</artifactId>
    <version>2.0.38</version>
</dependency>
<dependency>
    <groupId>com.github.icecooly</groupId>
    <artifactId>smartJdbc-spring</artifactId>
    <version>2.0.0</version>
</dependency>
```

如果使用 Gradle 来构建项目，则需将下面的代码置于 build.gradle 文件的 dependencies 代码块中：

```groovy
compile 'com.github.icecooly:smartjdbc:2.0.38'
compile 'com.github.icecooly:smartjdbc-spring:2.0.0'
```

# 3 特性

* 强大的 CRUD 操作
* 支持自动跨表查询无需写sql
* 除基本数据类型自动映射外，也支持对象自动映射List,Set等
* 支持json函数

# 4 例子

## 4.1 简单增删改
```mysql
CREATE TABLE `t_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_name` varchar(32) DEFAULT NULL COMMENT '用户名',
  `name` varchar(32) DEFAULT NULL COMMENT '姓名',
  `department_id` int(11) DEFAULT NULL COMMENT '所属部门',
  `role_id_list` varchar(512) DEFAULT NULL COMMENT '角色列表',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

```java
@Data
@Entity(tableName = "t_user")
public class User{
	
	@PrimaryKey
	private Long id;
	
	/**姓名*/
	private String name;
	
	/**所属部门*/
	@ForeignKey(entityClass=Department.class)
	private Integer departmentId;
	
	/**角色列表*/
	private List<Integer> roleIdList;
	
	/**创建时间*/
	private Date createTime;
	
	/**更新时间*/
	private Date updateTime;
```

新增

```java
User user=new User();
user.setName("张三");
user.setDepartmentId(1);
user.setRoleIdList(Arrays.asList(1,2,3));//会自动转化为JSON.toString(roleIdList)
user.setId((long)dao.insert(user));
```

修改

```java

/**更新所有字段*/
User user=dao.getById(User.class, 1);
user.setName("张三2");
user.setDepartmentId(2);
user.setRoleIdList(Arrays.asList(1,2));
dao.update(user);

/**更新非空字段*/
User user=new User();
user.setId(1L);
user.setDepartmentId(3);
user.setMobileNo("130000000000");
dao.updateExcludeNull(user);

/**更新指定字段*/
User user=dao.getById(User.class, 2);
user.setAge(30);
dao.updateIncludeFields(user, "age");


/**通过sql更新字段*/
dao.executeUpdate("update t_user set name='王五' where id=?",2);
dao.executeUpdate("update t_user set name='王五' where id=#{id}",
				new SqlParam("id", 2));
	
```

删除

```java
dao.deleteById(User.class, 1);
```

## 4.2 基本查询

```java
除了基本数据类型会自动映射外,复杂数据类型List<对象>,Set<对象>等都可以自动映射
User user=dao.getById(User.class, 1);
User user=dao.getEntity(User.class,QueryWhere.create().where("name", "张三"));
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

## 4.3 列表查询

```java
//查询姓名包含‘王’的用户列表
List<User> list=dao.getList(User.class, QueryWhere.create().where("name",SqlOperator.LIKE,"王"));
System.out.println(DumpUtil.dump(list));
```

## 4.4 复杂查询

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
```
### 4.4.1 where操作符 eq、 ne、 lt、 le、 gt、 ge

```java
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
```

### 4.4.2 where操作符 like

```java
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
```

### 4.4.3 where操作符 in、not in

```java
dao.getEntity(User.class,QueryWhere.create().in("status",Arrays.asList(1,2,3)));
dao.getEntity(User.class,QueryWhere.create().in("status",new int[] {1,2}));
dao.getEntity(User.class,QueryWhere.create().in("status",new byte[] {1,2}));
dao.getEntity(User.class,QueryWhere.create().in("status",new long[] {1,2}));
dao.getEntity(User.class,QueryWhere.create().in("name",Arrays.asList("张三","张三2")));
dao.getEntity(User.class,QueryWhere.create().in("name",new String[] {"张三","张三2"}));

dao.getEntity(User.class,QueryWhere.create().notin("status",Arrays.asList(1,2,3)));
dao.getEntity(User.class,QueryWhere.create().notin("status",new int[] {1,2}));
dao.getEntity(User.class,QueryWhere.create().notin("status",new byte[] {1,2}));
dao.getEntity(User.class,QueryWhere.create().notin("status",new long[] {1,2}));
dao.getEntity(User.class,QueryWhere.create().notin("name",Arrays.asList("张三","张三2")));
dao.getEntity(User.class,QueryWhere.create().notin("name",new String[] {"张三","张三2"}));

```

### 4.4.4 where操作符 自定义where条件

```java
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
```

### 4.4.5 排序分页

```java
dao.getEntity(User.class,QueryWhere.create().
				orderBy("id asc").
				orderBy("name desc").
				limit(0, 10));
```

## 5 跨表查询 Query<T>类
```java
public class Query<T> {
	//
	public int pageIndex;
	
	public int pageSize;
	
	public LinkedHashMap<String,OrderBy> orderBys;//<javaField,DESC> 
	
	public Map<String,Object> params;
	//
	public static Integer defaultPageSize=20;
}
@Data
public class UserQuery extends Query<User>{
	
	@QueryField(foreignKeyFields = "departmentId",field = "name")
	private String departmentName;
}
```
### 5.1 查询所属部门是`技术部`的用户
```java
UserQuery query=new UserQuery();
query.setDepartmentName("技术部");
List<User> list=dao.getList(query);
```
@QueryField注解说明这是一个查询字段
foreignKeyFields:说明这个查询字段所在的表是这个外键所对应的表
在User.class这个实体类中，departmentId是外键，对应的表是t_department
query.setDepartmentName("技术部");
等价于
inner join  t_department i1 on a.`department_id`=i1.`id`
where i1.`name` =   '技术部'   
主表的别名为a


### 5.2 查询所属部门包含`技术`的用户
```java
@QueryField(foreignKeyFields = "departmentId",field = "name",operator = SqlOperator.LIKE)
private String likeDepartmentName;
	
UserQuery query=new UserQuery();
query.setLikeDepartmentName("技术部");
List<User> list=dao.getList(query);
```

这个例子跟5.1类似，唯一的区别就是@QueryField设置操作符为LIKE,默认是EQ


其他更多复杂查询可参考test/QueryTestCase.java

# 6 Spring Boot中使用SmartJdbc
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
