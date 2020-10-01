package test;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.SmartJdbc;
import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.connection.ConnectionHolder;
import io.itit.smartjdbc.connection.TransactionManager;
import io.itit.smartjdbc.util.JdbcUtil;

/**
 * 
 * @author skydu
 *
 */
public class DefaultTransactionManager implements TransactionManager{
	//
	private static Logger logger = LoggerFactory.getLogger(DefaultTransactionManager.class);
	//
	protected static ThreadLocal<ConnectionHolder> connectionHolder = new ThreadLocal<>();

	/**
	 * 
	 */
	public void commit() {
		ConnectionHolder holder = connectionHolder.get();
		connectionHolder.set(null);
		if (holder == null || holder.getConnection() == null) {
			return;
		}
		try {
			if (holder.isUseTransaction()) {// use Transaction
				try {
					holder.getConnection().commit();
				} catch (SQLException e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		} finally {
			JdbcUtil.close(holder.getConnection());
		}
	}

	/**
	 * 
	 */
	public void rollback() {
		ConnectionHolder holder = connectionHolder.get();
		connectionHolder.set(null);
		if (holder == null || holder.getConnection() == null) {
			return;
		}
		try {
			if (holder.isUseTransaction()) {// use Transaction
				try {
					holder.getConnection().rollback();
				} catch (SQLException e1) {
					logger.error(e1.getMessage(), e1);
				}
			}

		} finally {
			JdbcUtil.close(holder.getConnection());
		}
	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection openConnection() throws SQLException {
		SmartDataSource smartDataSource=SmartJdbc.getDatasource();
		if(smartDataSource==null) {
			throw new RuntimeException("DataSource not found");
		}
		return smartDataSource.getDataSource().getConnection();
	}

	/**
	 * 
	 */
	public Connection getConnection(){
		Connection conn=null;
		try {
			ConnectionHolder holder = connectionHolder.get();
			if(holder==null) {
				holder = new ConnectionHolder();
				connectionHolder.set(holder);
			}
			conn=holder.getConnection();
			if(conn==null) {
				conn=openConnection();
				if(holder.isUseTransaction()==conn.getAutoCommit()) {
					conn.setAutoCommit(!holder.isUseTransaction());
				}
				holder.setConnection(conn);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new SmartJdbcException(e.getMessage());
		}
		return conn;
	}
}
