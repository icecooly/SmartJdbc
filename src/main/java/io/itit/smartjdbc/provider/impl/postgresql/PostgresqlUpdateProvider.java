package io.itit.smartjdbc.provider.impl.postgresql;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.enums.ColumnType;
import io.itit.smartjdbc.provider.UpdateProvider;
import io.itit.smartjdbc.provider.entity.EntityUpdate.EntityUpdateField;

/**
 * 
 * @author skydu
 *
 */
public class PostgresqlUpdateProvider extends UpdateProvider{

	public PostgresqlUpdateProvider(SmartDataSource smartDataSource) {
		super(smartDataSource);
	}
	
	@Override
	public String getValueSql(EntityUpdateField field) {
		String sql="?";
		if(field!=null&&field.columnType!=null) {
			if(field.columnType.equals(ColumnType.JSONB)) {
				sql+="::jsonb"; 
			}
		}
		return sql+",";
	}
}
