package io.itit.smartjdbc.provider.impl.pgsql;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.enums.ColumnType;
import io.itit.smartjdbc.provider.InsertProvider;
import io.itit.smartjdbc.provider.entity.EntityInsert.EntityInsertField;

/**
 * 
 * @author skydu
 *
 */
public class PgsqlInsertProvider extends InsertProvider{

	public PgsqlInsertProvider(SmartDataSource smartDataSource) {
		super(smartDataSource);
	}
	
	@Override
	public String getValueSql(EntityInsertField field) {
		String sql="?";
		if(field!=null&&field.getColumnType()!=null) {
			if(field.getColumnType().equals(ColumnType.JSONB)) {
				sql+="::jsonb"; 
			}
			if(field.getColumnType().equals(ColumnType.LTREE)) {
				sql+="::ltree"; 
			}
		}
		return sql+",";
	}
}
