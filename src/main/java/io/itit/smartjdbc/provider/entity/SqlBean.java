package io.itit.smartjdbc.provider.entity;

/**
 * 
 * @author skydu
 *
 */
public class SqlBean {

	public String sql;
	
	public Object[] parameters;
	
	public SqlBean() {
		parameters=new Object[0];
	}

	public static SqlBean build(String sql, Object[] parameters) {
		SqlBean bean=new SqlBean();
		bean.sql=sql;
		bean.parameters=parameters;
		return bean;
	}
	
	public static SqlBean build(String sql) {
		SqlBean bean=new SqlBean();
		bean.sql=sql;
		return bean;
	}

}
