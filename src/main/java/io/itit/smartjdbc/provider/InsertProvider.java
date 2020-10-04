package io.itit.smartjdbc.provider;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.annotations.EntityField;
import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.util.JSONUtil;

/**
 * 
 * @author skydu
 *
 */
public abstract class InsertProvider extends SqlProvider{
	//
	protected Object bean;
	protected String[] excludeFields;
	//
	public InsertProvider(SmartDataSource smartDataSource) {
		super(smartDataSource);
	}
	//
	public InsertProvider object(Object bean){
		this.bean=bean;
		return this;
	}
	public InsertProvider excludeFields(String[] excludeFields) {
		this.excludeFields=excludeFields;
		return this;
	}
	
	@Override
	public SqlBean build() {
		StringBuilder sql=new StringBuilder();
		Class<?>type=bean.getClass();
		String tableName=getTableName(type);
		sql.append("insert into ").append(identifier()).append(tableName).append(identifier()).append("(");
		Set<String> excludesNames = new TreeSet<String>();
		for (String e : excludeFields) {
			excludesNames.add(e);
		}
		List<Object>fieldList=new ArrayList<Object>();
		List<Field> list=getPersistentFields(type);
		for (Field f : list) {
			if (excludesNames.contains(f.getName())) {
				continue;
			}
			if(!isPersistentField(f)) {
				continue;
			}
			EntityField entityField=f.getAnnotation(EntityField.class);
			if(entityField!=null&&entityField.autoIncrement()) {
				continue;
			}
			String fieldName = convertFieldName(f.getName());
			try {
				f.setAccessible(true);
				Object fieldValue=f.get(bean);
				if(fieldValue==null) {
					continue;
				}
				if(!WRAP_TYPES.contains(fieldValue.getClass())){
					fieldList.add(JSONUtil.toJson(fieldValue));
				}else{
					fieldList.add(fieldValue);
				}
			} catch (Exception e) {
				throw new SmartJdbcException(e);
			}
			sql.append(identifier()).append(fieldName).append(identifier()+",");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		sql.append("values(");
		for(int i=0;i<fieldList.size();i++){
			sql.append("?,");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		//
		return SqlBean.build(sql.toString(),fieldList.toArray(new Object[fieldList.size()]));
	}

}
