package io.itit.smartjdbc.provider.impl.postgresql;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.annotations.EntityField;
import io.itit.smartjdbc.enums.ColumnType;
import io.itit.smartjdbc.provider.InsertProvider;

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
	public String getValueSql(EntityField entityField) {
		String sql="?";
		if(entityField!=null&&entityField.columnType()!=null) {
			ColumnType columnType=entityField.columnType();
			if(columnType.equals(ColumnType.JSONB)) {
				sql+="::jsonb"; 
			}
		}
		return sql+",";
	}
}
