package io.itit.smartjdbc.provider.impl.pgsql;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.provider.SelectProvider;
import io.itit.smartjdbc.provider.SqlProvider;

/**
 * 
 * @author skydu
 *
 */
public class PgsqlSelectProvider extends SelectProvider{
	//
	//
	public PgsqlSelectProvider(SmartDataSource smartDataSource) {
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
		addPaging();	
		if(qw.getLimitEnd()!=-1) {
			sql.append("\nlimit ").append(qw.getLimitEnd()).append(" offset ").append(qw.getLimitStart()).append(" ");
		}
		return sql.toString();
	}
	
	@Override
	protected String getForUpdateSql() {
		if(!qw.isForUpdate()) {
			return "";
		}
		String sql=super.getForUpdateSql();
		
		if(qw.getOf()==null) {
			sql+="\nof "+SqlProvider.MAIN_TABLE_ALIAS;
		}else {
			sql+="\nof "+qw.getOf();
		}
		return sql;
	}
}
