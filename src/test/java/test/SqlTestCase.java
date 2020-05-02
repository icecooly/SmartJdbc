package test;

import org.junit.Test;

import io.itit.smartjdbc.util.ClassUtils;
import io.itit.smartjdbc.util.SqlUtil;
import test.entity.query.UserComplexQuery;
import test.entity.query.UserQuery;

/**
 * 
 * @author skydu
 *
 */
public class SqlTestCase {

	//
	public static class TestQuery{
		
	}
	//
	public static class UserTestQuery extends UserComplexQuery{
		
	}
	@Test
	public void getSuperClassGenricType() {
		System.out.println(ClassUtils.getSuperClassGenricType(UserQuery.class));
		System.out.println(ClassUtils.getSuperClassGenricType(UserComplexQuery.class));
		System.out.println(ClassUtils.getSuperClassGenricType(UserTestQuery.class));
		System.out.println(ClassUtils.getSuperClassGenricType(TestQuery.class));
	}

	@Test
	public void checkOrderByFieldValid() {
		SqlUtil.checkColumnName("user_name");
		SqlUtil.checkColumnName("a.user_name");
		SqlUtil.checkColumnName("i1.user_name");
		SqlUtil.checkColumnName("i1.`user_name`");
	}
}
