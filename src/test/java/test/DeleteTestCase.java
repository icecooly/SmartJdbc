package test;

import io.itit.smartjdbc.provider.where.QueryWhere;
import test.dao.BizDAO;
import test.domain.entity.User;

/**
 * 
 * @author skydu
 *
 */
public class DeleteTestCase extends BaseTestCase{
	static {
	    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
	}
	//
	BizDAO dao;
	//
	public DeleteTestCase() {
		dao=new BizDAO();
	}
	//
	/**
	 * 
	 */
	public void testDeleteUser() {
		dao.delete(User.class, QueryWhere.create().where("id", 7));
	}
	
}
