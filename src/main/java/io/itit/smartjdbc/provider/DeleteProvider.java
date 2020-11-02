package io.itit.smartjdbc.provider;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.provider.entity.EntityDelete;
import io.itit.smartjdbc.provider.where.QueryWhere;

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
	protected EntityDelete delete;
	protected QueryWhere queryWhere;
	//
	public DeleteProvider delete(EntityDelete delete) {
		this.delete=delete;
		return this;
	}
	
	public DeleteProvider queryWhere(QueryWhere queryWhere) {
		this.queryWhere=queryWhere;
		return this;
	}
}
