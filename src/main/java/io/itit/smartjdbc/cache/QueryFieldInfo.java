package io.itit.smartjdbc.cache;

import java.lang.reflect.Field;
import java.util.List;

import io.itit.smartjdbc.annotations.InnerJoin;
import io.itit.smartjdbc.annotations.InnerJoins;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.util.StringUtil;

/**
 * 
 * @author skydu
 *
 */
public class QueryFieldInfo {

	public Field field;
	
	public Class<?> fieldType;
	
	public String fieldName;//fullName.field.getName()
	
	public InnerJoin innerJoin;
	
	public InnerJoins innerJoins;
	
	public QueryField queryField;
	
	public List<InnerJoin> innerJoinsList;
	
	public String tableAlias;
	//
	public static QueryFieldInfo create(String fullName, Field field) {
		QueryFieldInfo fieldInfo=new QueryFieldInfo();
		fieldInfo.field=field;
		fieldInfo.fieldType=field.getType();
		if(StringUtil.isEmpty(fullName)) {
			fieldInfo.fieldName=field.getName();
		}else {
			fieldInfo.fieldName=fullName+"."+field.getName();
		}
		fieldInfo.queryField=field.getAnnotation(QueryField.class);
		fieldInfo.innerJoin=field.getAnnotation(InnerJoin.class);
		fieldInfo.innerJoins=field.getAnnotation(InnerJoins.class);
		return fieldInfo;
	}
	
}
