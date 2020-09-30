package io.itit.smartjdbc.provider.impl.postgresql;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.provider.UpdateProvider;

/**
 * 
 * @author skydu
 *
 */
public class PostgresqlUpdateProvider extends UpdateProvider{

	public PostgresqlUpdateProvider(SmartDataSource smartDataSource) {
		super(smartDataSource);
	}
}
