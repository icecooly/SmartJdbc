package io.itit.smartjdbc.cache;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.itit.smartjdbc.enums.ConditionType;

/**
 * 
 * @author skydu
 *
 */
public class QueryInfo {

	public Class<?> clazz;
	
	public Field field;
	
	public ConditionType conditionType;
	
	public List<QueryFieldInfo> fieldList;
	
	public List<Field> allFieldList;//no static final field
	
	public List<QueryInfo> children;
	
	//
	public QueryInfo(Class<?> clazz,ConditionType conditionType) {
		this.clazz=clazz;
		this.conditionType=conditionType;
		this.children=new ArrayList<>();
	}
}
