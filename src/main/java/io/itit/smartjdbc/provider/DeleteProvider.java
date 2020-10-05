package io.itit.smartjdbc.provider;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.QueryWhere;
import io.itit.smartjdbc.provider.where.QueryWhere.WhereStatment;

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
	//
	@Override
	public SqlBean build() {
		StringBuilder sql=new StringBuilder();
		String tableName=getTableNameWithIdentifier(entityClass);
		sql.append("delete "+MAIN_TABLE_ALIAS+" from ").append(tableName).append(" ").append(MAIN_TABLE_ALIAS).append(" ");
		sql.append("where 1=1 ");
		WhereStatment ws=queryWhere.whereStatement(getSmartDataSource());
		sql.append(ws.sql);
		return SqlBean.build(sql.toString(),ws.values);
	}
}
