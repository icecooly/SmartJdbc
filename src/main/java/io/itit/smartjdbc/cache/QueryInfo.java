package io.itit.smartjdbc.cache;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 
 * @author skydu
 *
 */
public class QueryInfo {

	public Class<?> clazz;
	
	public List<QueryFieldInfo> fieldList;
	
	public List<Field> allFieldList;//no static final field
}
