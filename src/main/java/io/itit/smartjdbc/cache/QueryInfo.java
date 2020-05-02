package io.itit.smartjdbc.cache;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.itit.smartjdbc.enums.ConditionType;
import io.itit.smartjdbc.util.StringUtil;

/**
 * 
 * @author skydu
 *
 */
public class QueryInfo {
	
	public Class<?> clazz;
	
	public Field field;

	public String fullName;//parent.fullName.field.getName()
	
	public ConditionType conditionType;
	
	public List<QueryFieldInfo> fieldList;
	
	public List<QueryInfo> children;
	
	//
	public QueryInfo(String parentFullName,Field field,Class<?> clazz,ConditionType conditionType) {
		this.field=field;
		this.clazz=clazz;
		this.conditionType=conditionType;
		this.children=new ArrayList<>();
		StringBuilder fullName=new StringBuilder();
		if((!StringUtil.isEmpty(parentFullName))) {
			fullName.append(parentFullName).append(".");
		}
		if(field!=null) {
			fullName.append(field.getName());
		}
		this.fullName=fullName.toString();
	}
}
