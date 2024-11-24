package io.itit.smartjdbc.provider;

import java.util.ArrayList;
import java.util.List;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.Types;
import io.itit.smartjdbc.provider.entity.EntityInsert;
import io.itit.smartjdbc.provider.entity.EntityInsert.EntityInsertField;
import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.util.JSONUtil;

/**
 * 
 * @author skydu
 *
 */
public abstract class InsertProvider extends SqlProvider{
	//
	protected EntityInsert insert;
	//
	public InsertProvider(SmartDataSource smartDataSource) {
		super(smartDataSource);
	}
	//
	public InsertProvider insert(EntityInsert insert){
		this.insert=insert;
		return this;
	}
	//
	public String getValueSql(EntityInsertField field) {
		return "?,";
	}
	
	@Override
	public SqlBean build() {
		StringBuilder sql=new StringBuilder();
		sql.append("insert into ").append(addIdentifier(insert.getTableName())).append("(");
		List<Object>fieldList=new ArrayList<Object>();
		StringBuilder values=new StringBuilder();
		List<EntityInsertField> insertFieldList=insert.getFieldList();
		for (EntityInsertField field : insertFieldList) {
			Object value=field.getValue();
			if(value==null) {
				continue;
			}
			values.append(getValueSql(field));
			sql.append("").append(addIdentifier(field.getColumn())).append(",");
			if(!Types.WRAP_TYPES.contains(value.getClass())){
				fieldList.add(JSONUtil.toJson(value));
			}else{
				fieldList.add(value);
			}
		}
		//
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		sql.append("values(");
		sql.append(values.toString());
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		SqlBean insertSql=new SqlBean();
		insertSql.sql=sql.toString();
		insertSql.parameters=fieldList.toArray();
		return insertSql;
	}

}
