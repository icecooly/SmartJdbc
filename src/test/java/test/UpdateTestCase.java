package test;

import java.util.Date;

import io.itit.smartjdbc.provider.where.QueryWhere;
import test.dao.BizDAO;
import test.domain.entity.User;

/**
 * 
 * @author skydu
 *
 */
public class UpdateTestCase extends BaseTestCase{
	static {
	    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
	}
	//
	BizDAO dao;
	//
	public UpdateTestCase() {
		dao=new BizDAO();
	}
	//
	/**
	 * 
	 */
	public void testUpdateUser() {
		User user=new User();
		user.setName("test1");
		user.setCreateTime(new Date());
		user.setUpdateTime(new Date());
		dao.update(user, QueryWhere.create().where("id", 5));
	}
	
}
