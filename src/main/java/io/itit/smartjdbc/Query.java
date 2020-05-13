package io.itit.smartjdbc;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author icecooly
 *
 */
public class Query<T> {
	//
	public int pageIndex;
	
	public int pageSize;
	
	public LinkedHashMap<String,String> orderBys;//<javaField,DESC> 
	
	public Map<String,Object> params;
	//
	public static Integer defaultPageSize=20;
	public static String defaultOrderBy;//id desc
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
	public Query<?> orderBy(String javaField,String orderby) {
		orderBys.put(javaField, orderby);
		return this;
	}
	//
	public Query<?> param(String key,Object value){
		params.put(key, value);
		return this;
	}
	//
	/**
	 * @return the pageIndex
	 */
	public int getPageIndex() {
		return pageIndex;
	}
	/**
	 * @param pageIndex the pageIndex to set
	 */
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	/**
	 * @return the orderBys
	 */
	public LinkedHashMap<String, String> getOrderBys() {
		return orderBys;
	}
	/**
	 * @param orderBys the orderBys to set
	 */
	public void setOrderBys(LinkedHashMap<String, String> orderBys) {
		this.orderBys = orderBys;
	}
	
}
