package io.itit.smartjdbc.provider.impl.kingbase;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.domain.EntityFieldInfo;
import io.itit.smartjdbc.provider.SelectProvider;
import io.itit.smartjdbc.provider.SqlProvider;
import io.itit.smartjdbc.util.SmartJdbcUtils;

/**
 * 
 * @author skydu
 *
 */
public class KingbaseSelectProvider extends SelectProvider{
	//
	//
	public KingbaseSelectProvider(SmartDataSource smartDataSource) {
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
	
	@Override
	protected void addSelectFields(StringBuilder sql) {
		for (EntityFieldInfo field : selectFields) {
			if(SmartJdbcUtils.isEmpty(field.getStatFunction())) {
				if(qw.isForUpdate()&&!field.getTableAlias().equals(SqlProvider.MAIN_TABLE_ALIAS)){
					continue;
				}
			}
			sql.append(getSelectFieldSql(field));
			sql.append(",");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(" ");
	}
}
