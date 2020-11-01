package io.itit.smartjdbc.provider.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.itit.smartjdbc.Query.OrderBy;
import io.itit.smartjdbc.enums.ConditionType;

/**
 * 
 * @author skydu
 *
 */
public class QueryInfo {
	
	public ConditionType conditionType;
	
	public List<QueryFieldInfo> fieldList;
	
	public List<QueryInfo> children;
	
	public int pageIndex;
	
	public int pageSize;
	
	public List<OrderBy> orderByList;
	
	public Map<String,Object> params;
	//
	public QueryInfo(String tableName,ConditionType conditionType) {
		this.conditionType=conditionType;
		this.children=new ArrayList<>();
		this.pageSize=20;//
		this.pageIndex=1;//从1开始
		this.fieldList=new ArrayList<>();
		this.params=new HashMap<>();
	}
	//
	public int getStartPageIndex(){
		return (pageIndex-1)*pageSize;
	}
	//
	public QueryInfo param(String key,Object value){
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
	
}
