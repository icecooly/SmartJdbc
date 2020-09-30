package test;

import org.junit.Test;

import io.itit.smartjdbc.util.ClassUtils;
import test.domain.query.UserComplexQuery;
import test.domain.query.UserQuery;

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
}
