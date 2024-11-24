package io.itit.smartjdbc.domain;

import java.io.Serializable;

/**
 * 
 * @author skydu
 *
 */
public class SmartJdbcCondition implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//
	public static final String OPT_等于="eq";
	public static final String OPT_不等于="ne";
	public static final String OPT_大于="gt";
	public static final String OPT_大于等于="ge";
	public static final String OPT_小于="lt";
	public static final String OPT_小于等于="le";
	public static final String OPT_包含="contains";//左边是一个列表 右边可以是列表也可以不是列表
	public static final String OPT_不包含="notcontains";
	public static final String OPT_开始是="startswith";
	public static final String OPT_结尾是="endswith";
	public static final String OPT_未设置="isnull";
	public static final String OPT_已设置="isnotnull";
	public static final String OPT_在列表中="in";//右边是一个列表 左边可以是列表 也可以不是列表 左边的所有元素都被右边包含
	public static final String OPT_不在列表中="notin";//右边是一个列表
	public static final String OPT_范围内="between";
	public static final String OPT_不在范围内="notbetween";
	
	public String fieldId;
	
	/***/
	public String opt;

	/***/
	public Object value;
	
	public SmartJdbcCondition() {
		
	}
	
	public SmartJdbcCondition(String fieldId, String opt) {
		this.fieldId=fieldId;
		this.opt=opt;
	}
	//
	public SmartJdbcCondition(String fieldId, String opt, Object value) {
		this.fieldId=fieldId;
		this.opt=opt;
		this.value=value;
	}
}
