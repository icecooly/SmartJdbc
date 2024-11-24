package io.itit.smartjdbc.provider;

import java.util.ArrayList;
import java.util.List;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.Types;
import io.itit.smartjdbc.provider.entity.EntityUpdate;
import io.itit.smartjdbc.provider.entity.EntityUpdate.EntityUpdateField;
import io.itit.smartjdbc.provider.entity.EntityUpdate.VersionField;
import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.QueryWhere;
import io.itit.smartjdbc.provider.where.QueryWhere.WhereStatment;
import io.itit.smartjdbc.provider.where.WhereSqlBuilder;
import io.itit.smartjdbc.util.JSONUtil;

/**
 * 
 * @author skydu
 *
 */
public class UpdateProvider extends SqlProvider{
	//
	protected EntityUpdate update;
	
	//
	public UpdateProvider(SmartDataSource smartDataSource) {
		super(smartDataSource);
	}
	//
	public UpdateProvider update(EntityUpdate update) {
		this.update=update;
		return this;
	}
	//
	public String getValueSql(EntityUpdateField field) {
		return "?,";
	}
	//
	@Override
	public SqlBean build() {
		StringBuilder sql=new StringBuilder();
		sql.append("update ").append(addIdentifier(update.getTableName())).append(" ").append(MAIN_TABLE_ALIAS).append(" ");
		List<Object>fieldList=new ArrayList<Object>();
		sql.append("set  ");
		VersionField versionField=update.getVersionField();
		if(versionField!=null) {
			String column=addIdentifier(versionField.getColumn());
			sql.append(" ").append(column).append("=").append(column).append("+1,");
		}
		List<EntityUpdateField> updateFieldList=update.getFieldList();
		for (EntityUpdateField field: updateFieldList) {
			Object fieldValue=field.getValue();
			if(update.isExcludeNull()&&fieldValue==null) {
				continue;
			}
			sql.append(" ").append(addIdentifier(field.getColumn())).append("=");
			if(fieldValue==null) {
				sql.append("null,");
				continue;
			}
			sql.append(getValueSql(field));
			if(fieldValue!=null&&!Types.WRAP_TYPES.contains(fieldValue.getClass())){
				fieldList.add(JSONUtil.toJson(fieldValue));
			}else{
				fieldList.add(fieldValue);
			}
		}
		sql.deleteCharAt(sql.length()-1);
		
		//
		QueryWhere queryWhere=update.getQueryWhere();
		if(queryWhere!=null) {
			if(versionField!=null) {
				queryWhere.where(versionField.getColumn(),versionField.getValue());
			}
			WhereStatment ws=new WhereSqlBuilder(getDatabaseType(),queryWhere).build();
			sql.append(ws.sql);
			for(Object o:ws.values){
				fieldList.add(o);
			}
		}
		//
		SqlBean updateSql=new SqlBean();
		updateSql.sql=sql.toString();
		updateSql.parameters=fieldList.toArray();
		//
		return SqlBean.build(sql.toString(), fieldList.toArray(new Object[fieldList.size()]));
	}
}
