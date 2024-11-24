package io.itit.smartjdbc.provider.impl.pgsql;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.enums.ColumnType;
import io.itit.smartjdbc.provider.UpdateProvider;
import io.itit.smartjdbc.provider.entity.EntityUpdate.EntityUpdateField;

/**
 * 
 * @author skydu
 *
 */
public class PgsqlUpdateProvider extends UpdateProvider{

	public PgsqlUpdateProvider(SmartDataSource smartDataSource) {
		super(smartDataSource);
	}
	
	@Override
	public String getValueSql(EntityUpdateField field) {
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
