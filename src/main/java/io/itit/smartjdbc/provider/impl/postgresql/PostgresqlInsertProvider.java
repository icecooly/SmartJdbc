package io.itit.smartjdbc.provider.impl.postgresql;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.enums.ColumnType;
import io.itit.smartjdbc.provider.InsertProvider;
import io.itit.smartjdbc.provider.entity.EntityInsert.EntityInsertField;

/**
 * 
 * @author skydu
 *
 */
public class PostgresqlInsertProvider extends InsertProvider{

	public PostgresqlInsertProvider(SmartDataSource smartDataSource) {
		super(smartDataSource);
	}
	
	@Override
	public String getValueSql(EntityInsertField field) {
		String sql="?";
		if(field!=null&&field.columnType!=null) {
			if(field.columnType.equals(ColumnType.JSONB)) {
				sql+="::jsonb"; 
			}
		}
		return sql+",";
	}
}
