package io.itit.smartjdbc.provider.impl.pgsql;

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
public class PgsqlDeleteProvider extends DeleteProvider{
	//
	public PgsqlDeleteProvider(SmartDataSource smartDataSource) {
		super(smartDataSource);
	}
	//
	@Override
	public SqlBean build() {
		StringBuilder sql=new StringBuilder();
		String tableName=addIdentifier(delete.getTableName());
		sql.append("delete from ").append(tableName).append(" ").append(MAIN_TABLE_ALIAS).append(" ");
		WhereStatment ws=new WhereSqlBuilder(getDatabaseType(),queryWhere).build();
		sql.append(ws.sql);
		return SqlBean.build(sql.toString(),ws.values);
	}
}
