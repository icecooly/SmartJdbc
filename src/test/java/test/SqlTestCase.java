package test;

import org.junit.Test;

import io.itit.smartjdbc.util.SqlUtil;

/**
 * 
 * @author skydu
 *
 */
public class SqlTestCase {

	
	@Test
	public void checkOrderByFieldValid() {
		SqlUtil.checkColumnName("user_name");
		SqlUtil.checkColumnName("a.user_name");
		SqlUtil.checkColumnName("i1.user_name");
		SqlUtil.checkColumnName("i1.`user_name`");
	}
}
