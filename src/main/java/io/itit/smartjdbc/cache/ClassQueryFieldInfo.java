package io.itit.smartjdbc.cache;

import java.lang.reflect.Field;
import java.util.List;

import io.itit.smartjdbc.annotations.InnerJoin;
import io.itit.smartjdbc.annotations.InnerJoins;
import io.itit.smartjdbc.annotations.QueryField;

/**
 * 
 * @author skydu
 *
 */
public class ClassQueryFieldInfo extends QueryFieldInfo{

	public Field field;
	
	public Class<?> fieldType;
	
	public String fieldName;//fullName.field.getName()
	
	public InnerJoin innerJoin;
	
	public InnerJoins innerJoins;
	
	public QueryField queryField;
	
	public List<InnerJoin> innerJoinsList;
	
}
