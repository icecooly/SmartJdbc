package test;

import java.util.Date;

import test.dao.BizDAO;
import test.domain.entity.User;

/**
 * 
 * @author skydu
 *
 */
public class InsertTestCase extends BaseTestCase{
	static {
	    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
	}
	//
	BizDAO dao;
	//
	public InsertTestCase() {
		dao=new BizDAO();
	}
	//
	/**
	 * 
	 */
	public void testAddUser() {
		User user=new User();
		user.setUserName("test1234");
		user.setName("test1234");
		user.setCreateTime(new Date());
		user.setUpdateTime(new Date());
		dao.insert(user);
	}
	
}
