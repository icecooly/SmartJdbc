package io.itit.smartjdbc.provider.impl.mysql;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.provider.DeleteProvider;
import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.WhereSqlBuilder;
import io.itit.smartjdbc.provider.where.QueryWhere.WhereStatment;

/**
 * 
 * @author skydu
 *
 */
public class MysqlDeleteProvider extends DeleteProvider{

	public MysqlDeleteProvider(SmartDataSource smartDataSource) {
		super(smartDataSource);
	}
	
	@Override
	public SqlBean build() {
		StringBuilder sql=new StringBuilder();
		String tableName=addIdentifier(delete.getTableName());
		sql.append("delete "+MAIN_TABLE_ALIAS+" from ").append(tableName).append(" ").append(MAIN_TABLE_ALIAS).append(" ");
		WhereStatment ws=new WhereSqlBuilder(getDatabaseType(),queryWhere).build();
		sql.append(ws.sql);
		return SqlBean.build(sql.toString(),ws.values);
	}
}
