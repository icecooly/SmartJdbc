package test;

import java.io.FileInputStream;
import java.util.Properties;

import javax.sql.DataSource;

import io.itit.smartjdbc.SmartJdbc;
import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.datasource.DriverManagerDataSource;
import junit.framework.TestCase;

/**
 * 
 * @author skydu
 *
 */
public abstract class BaseTestCase extends TestCase{
	//
	private SmartDataSource smartDataSource;
	//
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		smartDataSource=new SmartDataSource(createDriverManagerDataSource(),new DefaultTransactionManager());
		smartDataSource.init();
		smartDataSource.start();
		SmartJdbc.registerDataSource(smartDataSource);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	//
	private DataSource createDriverManagerDataSource() throws Exception{
		Properties prop = new Properties();
		prop.load(new FileInputStream("env"));
		DriverManagerDataSource dataSource=new DriverManagerDataSource();
		dataSource.setUrl(prop.getProperty("dbUrl"));
		dataSource.setUsername(prop.getProperty("dbUser"));
		dataSource.setPassword(prop.getProperty("dbPassword"));
		dataSource.setDriverClassName(prop.getProperty("dbDriverClass"));
		return dataSource;
	}
}
