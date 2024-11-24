package io.itit.smartjdbc.provider.factory;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.enums.DatabaseType;
import io.itit.smartjdbc.provider.SelectProvider;
import io.itit.smartjdbc.provider.impl.kingbase.KingbaseSelectProvider;
import io.itit.smartjdbc.provider.impl.mysql.MysqlSelectProvider;
import io.itit.smartjdbc.provider.impl.pgsql.PgsqlSelectProvider;

/**
 * 
 * @author skydu
 *
 */
public class SelectProviderFactory {
	//
	public static SelectProvider create(SmartDataSource smartDataSource) {
		DatabaseType type=smartDataSource.getDatabaseType();
		if(type.equals(DatabaseType.MYSQL)) {
			return new MysqlSelectProvider(smartDataSource);
		}
		if(type.equals(DatabaseType.POSTGRESQL)) {
			return new PgsqlSelectProvider(smartDataSource);
		}
		if(type.equals(DatabaseType.KINGBASE)) {
			return new KingbaseSelectProvider(smartDataSource);
		}
		throw new RuntimeException("unspoort database type "+type);
	}
}
