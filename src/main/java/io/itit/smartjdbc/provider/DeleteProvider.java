package io.itit.smartjdbc.provider;

import io.itit.smartjdbc.QueryWhere;
import io.itit.smartjdbc.QueryWhere.WhereStatment;
import io.itit.smartjdbc.SqlBean;

/**
 * 
 * @author skydu
 *
 */
public class DeleteProvider extends SqlProvider{
	//
	protected Class<?> entityClass;
	protected QueryWhere qw;
	//
	public DeleteProvider(Class<?> entityClass,QueryWhere qw) {
		this.entityClass=entityClass;
		this.qw=qw;
	}
	
	@Override
	public SqlBean build() {
		StringBuilder sql=new StringBuilder();
		String tableName=getTableName(entityClass);
		sql.append("delete from ").append(tableName).append(" ");
		sql.append("where 1=1 ");
		WhereStatment ws=qw.whereStatement();
		sql.append(ws.sql);
		return createSqlBean(sql.toString(),ws.values);
	}

}
