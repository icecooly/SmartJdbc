package io.itit.smartjdbc.provider.impl.kingbase;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.enums.ColumnType;
import io.itit.smartjdbc.provider.InsertProvider;
import io.itit.smartjdbc.provider.entity.EntityInsert.EntityInsertField;

/**
 * 
 * @author skydu
 *
 */
public class KingbaseInsertProvider extends InsertProvider{

	public KingbaseInsertProvider(SmartDataSource smartDataSource) {
		super(smartDataSource);
	}
	
	@Override
	public String getValueSql(EntityInsertField field) {
		String sql="?";
		if(field!=null&&field.getColumnType()!=null) {
			if(field.getColumnType().equals(ColumnType.JSONB)) {
				sql+="::jsonb"; 
			}
		}
		return sql+",";
	}
}
