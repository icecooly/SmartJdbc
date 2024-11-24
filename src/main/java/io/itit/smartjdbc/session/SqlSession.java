package io.itit.smartjdbc.session;

import java.sql.Connection;

import javax.sql.DataSource;

/**
 * 
 * @author skydu
 *
 */
public abstract class SqlSession {

	protected DataSource dataSource;

	protected Connection connection;

	//
	public SqlSession(DataSource dataSource, Connection connection) {
		this.dataSource = dataSource;
		this.connection = connection;
	}

	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

	public abstract void close();
}