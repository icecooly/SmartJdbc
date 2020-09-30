package io.itit.smartjdbc.provider.factory;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.enums.DatabaseType;
import io.itit.smartjdbc.provider.InsertProvider;
import io.itit.smartjdbc.provider.impl.mysql.MysqlInsertProvider;
import io.itit.smartjdbc.provider.impl.postgresql.PostgresqlInsertProvider;

/**
 * 
 * @author skydu
 *
 */
public class InsertProviderFactory {
	//
	public static InsertProvider create(SmartDataSource smartDataSource) {
		DatabaseType type=smartDataSource.getDatabaseType();
		if(type.equals(DatabaseType.MYSQL)) {
			return new MysqlInsertProvider(smartDataSource);
		}
		if(type.equals(DatabaseType.POSTGRESQL)) {
			return new PostgresqlInsertProvider(smartDataSource);
		}
		throw new RuntimeException("unspoort database type "+type);
	}
}
