package io.itit.smartjdbc.provider.factory;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.enums.DatabaseType;
import io.itit.smartjdbc.provider.UpdateProvider;
import io.itit.smartjdbc.provider.impl.kingbase.KingbaseUpdateProvider;
import io.itit.smartjdbc.provider.impl.mysql.MysqlUpdateProvider;
import io.itit.smartjdbc.provider.impl.pgsql.PgsqlUpdateProvider;

/**
 * 
 * @author skydu
 *
 */
public class UpdateProviderFactory {
	//
	public static UpdateProvider create(SmartDataSource smartDataSource) {
		DatabaseType type=smartDataSource.getDatabaseType();
		if(type.equals(DatabaseType.MYSQL)) {
			return new MysqlUpdateProvider(smartDataSource);
		}
		if(type.equals(DatabaseType.POSTGRESQL)) {
			return new PgsqlUpdateProvider(smartDataSource);
		}
		if(type.equals(DatabaseType.KINGBASE)) {
			return new KingbaseUpdateProvider(smartDataSource);
		}
		throw new RuntimeException("unspoort database type "+type);
	}
}
