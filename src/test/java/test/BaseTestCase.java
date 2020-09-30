package test;

import javax.sql.DataSource;

import io.itit.smartjdbc.DataSourceManager;
import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.connection.DefaultTransactionManager;
import io.itit.smartjdbc.datasource.DriverManagerDataSource;
import junit.framework.TestCase;

/**
 * 
 * @author skydu
 *
 */
public abstract class BaseTestCase extends TestCase{
	//
	private static final String dbName="db_test";
	private static final String dbHost="119.29.88.217";
	private static final String dbPort="3306";
	private static final String dbUser="db_test";
	private static final String dbPwd="DBtest12345!@#";
	//
	private SmartDataSource smartDataSource;
	//
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		smartDataSource=new SmartDataSource(createDriverManagerDataSource(),new DefaultTransactionManager());
		DataSourceManager.registerDataSource(smartDataSource);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		smartDataSource.commit();
	}
	//
	private String getJdbcUrl(String dbName) {
		return "jdbc:mysql://"+dbHost+":"+dbPort+"/"+dbName;
	}
	//
	private DataSource createDriverManagerDataSource() throws Exception{
		DriverManagerDataSource dataSource=new DriverManagerDataSource();
		dataSource.setUrl(getJdbcUrl(dbName));
		dataSource.setUsername(dbUser);
		dataSource.setPassword(dbPwd);  
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		return dataSource;
	}
}
