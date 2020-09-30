//package io.itit.smartjdbc.connection;
//
//import java.sql.Connection;
//
//import io.itit.smartjdbc.SmartJdbcException;
//
///**
// * 
// * @author skydu
// *
// */
//public class ConnectionManager {
//	//
//	protected TransactionManager transactionManager=new DefaultTransactionManager();
//	//
//	/**
//	 * @return the transactionManager
//	 */
//	public TransactionManager getTransactionManager() {
//		return transactionManager;
//	}
//
//	/**
//	 * @param transactionManager the transactionManager to set
//	 */
//	public void setTransactionManager(TransactionManager transactionManager) {
//		this.transactionManager = transactionManager;
//	}
//	/**
//	 * 
//	 * @param datasourceIndex
//	 * @return
//	 */
//	public Connection getConnecton(String datasourceIndex) {
//		if(transactionManager==null) {
//			throw new SmartJdbcException("transactionManager is null");
//		}
//		return transactionManager.getConnecton(datasourceIndex);
//	}
//
//	/**
//	 * 
//	 */
//	public void commit() {
//		transactionManager.commit();
//	}
//	
//	/**
//	 * 
//	 */
//	public void rollback() {
//		transactionManager.rollback();
//	}
//
//}
