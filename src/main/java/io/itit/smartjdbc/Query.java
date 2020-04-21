package io.itit.smartjdbc;

import java.util.LinkedHashMap;

import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.enums.OrderBy;

/**
 * 
 * @author icecooly
 *
 */
public class Query<T> {
	//
	@QueryField(ingore=true)
	public int pageIndex;
	
	@QueryField(ingore=true)
	public int pageSize;
	
	@QueryField(ingore=true)
	public LinkedHashMap<String,OrderBy> orderBys;//<userName,DESC>
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
	}
	//
	public int getStartPageIndex(){
		return (pageIndex-1)*pageSize;
	}
}
