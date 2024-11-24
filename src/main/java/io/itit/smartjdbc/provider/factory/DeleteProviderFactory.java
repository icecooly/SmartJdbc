package io.itit.smartjdbc.provider.factory;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.enums.DatabaseType;
import io.itit.smartjdbc.provider.DeleteProvider;
import io.itit.smartjdbc.provider.impl.kingbase.KingbaseDeleteProvider;
import io.itit.smartjdbc.provider.impl.mysql.MysqlDeleteProvider;
import io.itit.smartjdbc.provider.impl.pgsql.PgsqlDeleteProvider;

/**
 * 
 * @author skydu
 *
 */
public class DeleteProviderFactory {
	//
	public static DeleteProvider create(SmartDataSource smartDataSource) {
		DatabaseType type=smartDataSource.getDatabaseType();
		if(type.equals(DatabaseType.MYSQL)) {
			return new MysqlDeleteProvider(smartDataSource);
		}
		if(type.equals(DatabaseType.POSTGRESQL)) {
			return new PgsqlDeleteProvider(smartDataSource);
		}
		if(type.equals(DatabaseType.KINGBASE)) {
			return new KingbaseDeleteProvider(smartDataSource);
		}
		throw new RuntimeException("unspoort database type "+type);
	}
}
