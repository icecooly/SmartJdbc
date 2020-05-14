package io.itit.smartjdbc;

import java.sql.ResultSet;

/**
 * 
 * @author skydu
 *
 */
public interface SqlInterceptor {

	/**
	 * 
	 * @param sql
	 * @param parameters
	 */
	void beforeExcute(String sql,Object ... parameters);
	
	
	/**
	 * 
	 * @param <T>
	 * @param type
	 * @param rs
	 * @return
	 */
	<T> T convertBean(Class<T> type,ResultSet rs);

	/**
	 * 
	 * @param sql
	 * @param parameters
	 */
	void afterExcute(String sql,Object ... parameters);
}
