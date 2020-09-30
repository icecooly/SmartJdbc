package io.itit.smartjdbc.provider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.itit.smartjdbc.QueryWhere;
import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.QueryWhere.WhereStatment;
import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.SqlBean;
import io.itit.smartjdbc.annotations.EntityField;
import io.itit.smartjdbc.util.ClassUtils;
import io.itit.smartjdbc.util.JSONUtil;

/**
 * 
 * @author skydu
 *
 */
public class UpdateProvider extends SqlProvider{
	//
	protected Object object;
	protected QueryWhere queryWhere;
	protected Set<String> includeFields;
	protected String[] excludeFields;
	protected boolean excludeNull;
	//
	public UpdateProvider(SmartDataSource smartDataSource) {
		super(smartDataSource);
	}
	//
	public UpdateProvider object(Object object) {
		this.object=object;
		return this;
	}
	//
	public UpdateProvider excludeNull(boolean excludeNull) {
		this.excludeNull=excludeNull;
		return this;
	}
	//
	public UpdateProvider excludeFields(String[] excludeFields) {
		this.excludeFields=excludeFields;
		return this;
	}
	public UpdateProvider queryWhere(QueryWhere queryWhere) {
		this.queryWhere=queryWhere;
		return this;
	}
	//
	public UpdateProvider includeFields(Set<String> includeFields) {
		this.includeFields=includeFields;
		return this;
	}
	//
	@Override
	public SqlBean build() {
		StringBuilder sql=new StringBuilder();
		Class<?>type=object.getClass();
		String tableName=getTableName(type);
		sql.append("update ").append(tableName).append(" ");
		Set<String> excludesNames = new TreeSet<String>();
		for (String e : excludeFields) {
			excludesNames.add(e);
		}
		List<Object>fieldList=new ArrayList<Object>();
		sql.append("set ");
		List<Field> fields=ClassUtils.getFieldList(type);
		for (Field f : fields) {
			if(includeFields!=null&&!includeFields.isEmpty()&&(!includeFields.contains(f.getName()))){
				continue;
			}
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
			if (Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			try {
				if(!f.isAccessible()) {
					f.setAccessible(true);
				}
				Object fieldValue=f.get(object);
				if(excludeNull&&fieldValue==null){
					continue;
				}
				if(fieldValue!=null&&!WRAP_TYPES.contains(fieldValue.getClass())){
					fieldList.add(JSONUtil.toJson(fieldValue));
				}else{
					fieldList.add(fieldValue);
				}
			} catch (Exception e) {
				throw new SmartJdbcException(e);
			}
			sql.append(" `").append(fieldName).append("`=?,");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(" where 1=1");
		//
		if(queryWhere==null) {//默认where主键
			queryWhere=QueryWhere.create();
			List<Field> primaryKey=getPrimaryKey(object.getClass());
			for (Field field : primaryKey) {
				queryWhere.where(convertFieldName(field.getName()),getEntityFieldValue(object, field.getName()));
			}
		}
		WhereStatment ws=queryWhere.whereStatement();
		sql.append(ws.sql);
		for(Object o:ws.values){
			fieldList.add(o);
		}
		return createSqlBean(sql.toString(), fieldList.toArray(new Object[fieldList.size()]));
	}
}
