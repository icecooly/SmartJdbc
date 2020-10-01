package io.itit.smartjdbc.provider.impl.postgresql;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.provider.SelectProvider;

/**
 * 
 * @author skydu
 *
 */
public class PostgresqlSelectProvider extends SelectProvider{
	//
	//
	public PostgresqlSelectProvider(SmartDataSource smartDataSource) {
		super(smartDataSource);
	}
	
	@Override
	protected String getLimitSql() {
		if(isSelectCount) {
			return "";
		}
		if(!needPaging) {
			return "";
		}
		StringBuilder sql=new StringBuilder();
		addPaging(query);	
		if(qw.getLimitEnd()!=-1) {
			sql.append("limit ").append(qw.getLimitEnd()).append(" offset ").append(qw.getLimitStart()).append("\n");
		}
		return sql.toString();
	}
}
