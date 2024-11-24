package io.itit.smartjdbc.session;

import java.sql.SQLException;

/**
 * 
 * @author skydu
 *
 */
public interface TransactionManager {

  SqlSession getSession() throws SQLException;
  
  void commit() throws SQLException;
  
  void rollback() throws SQLException;

  Integer getTimeout() throws SQLException;
  
}