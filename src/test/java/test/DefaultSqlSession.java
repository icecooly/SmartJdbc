package test;

import java.sql.Connection;

import javax.sql.DataSource;

import io.itit.smartjdbc.session.SqlSession;

/**
 * 
 * @author skydu
 *
 */
public class DefaultSqlSession extends SqlSession{

	public DefaultSqlSession(DataSource dataSource, Connection connection) {
		super(dataSource, connection);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
