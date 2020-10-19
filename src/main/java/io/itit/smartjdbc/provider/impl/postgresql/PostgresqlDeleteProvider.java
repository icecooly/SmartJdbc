package io.itit.smartjdbc.provider.impl.postgresql;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.provider.DeleteProvider;
import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.QueryWhere.WhereStatment;

/**
 * 
 * @author skydu
 *
 */
public class PostgresqlDeleteProvider extends DeleteProvider{
	//
	public PostgresqlDeleteProvider(SmartDataSource smartDataSource) {
		super(smartDataSource);
	}
	//
	@Override
	public SqlBean build() {
		StringBuilder sql=new StringBuilder();
		String tableName=getTableName(entityClass);
		sql.append("delete from ").append(tableName).append(" ").append(MAIN_TABLE_ALIAS).append(" ");
		WhereStatment ws=queryWhere.whereStatement(getSmartDataSource().getDatabaseType());
		sql.append(ws.sql);
		return SqlBean.build(sql.toString(),ws.values);
	}
}
