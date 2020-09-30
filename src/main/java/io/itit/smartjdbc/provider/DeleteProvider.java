package io.itit.smartjdbc.provider;

import io.itit.smartjdbc.QueryWhere;
import io.itit.smartjdbc.QueryWhere.WhereStatment;
import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.SqlBean;

/**
 * 
 * @author skydu
 *
 */
public abstract class DeleteProvider extends SqlProvider{
	//
	public DeleteProvider(SmartDataSource smartDataSource) {
		super(smartDataSource);
	}
	//
	protected Class<?> entityClass;
	protected QueryWhere queryWhere;
	//
	public DeleteProvider entityClass(Class<?> entityClass) {
		this.entityClass=entityClass;
		return this;
	}
	
	public DeleteProvider queryWhere(QueryWhere queryWhere) {
		this.queryWhere=queryWhere;
		return this;
	}
	
	@Override
	public SqlBean build() {
		StringBuilder sql=new StringBuilder();
		String tableName=getTableName(entityClass);
		sql.append("delete from ").append(tableName).append(" ");
		sql.append("where 1=1 ");
		WhereStatment ws=queryWhere.whereStatement(this);
		sql.append(ws.sql);
		return createSqlBean(sql.toString(),ws.values);
	}
}
