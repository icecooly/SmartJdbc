package io.itit.smartjdbc;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.itit.smartjdbc.enums.OrderBy;

/**
 * 
 * @author icecooly
 *
 */
public class Query<T> {
	//
	public int pageIndex;
	
	public int pageSize;
	
	public LinkedHashMap<String,OrderBy> orderBys;//<javaField,DESC> 
	
	public Map<String,Object> params;
	//
	public static Integer defaultPageSize=20;
	//
	public Query(){
		pageSize=20;//
		pageIndex=1;//从1开始
		if(defaultPageSize!=null) {
			pageSize=defaultPageSize;
		}
		orderBys=new LinkedHashMap<>();
		params=new HashMap<>();
	}
	//
	public int getStartPageIndex(){
		return (pageIndex-1)*pageSize;
	}
	//
	public Query<?> orderBy(String javaField,OrderBy orderby) {
		orderBys.put(javaField, orderby);
		return this;
	}
	//
	public Query<?> param(String key,Object value){
		params.put(key, value);
		return this;
	}
}
