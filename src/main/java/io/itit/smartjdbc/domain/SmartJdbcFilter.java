package io.itit.smartjdbc.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author skydu
 *
 */
public class SmartJdbcFilter implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//
	public static final String OPT_AND="and";
	public static final String OPT_OR="or";
	//
	public String opt=OPT_AND;
	/**过滤器*/
	public List<SmartJdbcCondition> conditionList=new ArrayList<>();
	/**子过滤器*/
	public List<SmartJdbcFilter> children=new ArrayList<>(0);
	//
	public void addCondition(SmartJdbcCondition condition) {
		conditionList.add(condition);
	}
}
