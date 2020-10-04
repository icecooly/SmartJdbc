package io.itit.smartjdbc.provider.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.SqlParam;
import io.itit.smartjdbc.util.StringUtil;

/**
 * 
 * @author skydu
 *
 */
public class SqlBean {

	public String sql;
	
	public Object[] parameters;
	
	public SqlBean() {
		parameters=new Object[0];
	}
	//
	public static Object[] convertParameters(Map<String,Object> paraMap) {
		if(paraMap==null||paraMap.isEmpty()) {
			return new Object[0];
		}
		Object[] parameters=new Object[paraMap.size()];
		int i=0;
		for (Map.Entry<String, Object> e : paraMap.entrySet()) {
			parameters[i++]=new SqlParam(e.getKey(), e.getValue());
		}
		return parameters;
	}
	//
	public static SqlBean build(String sql, Object[] parameters) {
		SqlBean bean=new SqlBean();
		bean.sql=sql;
		bean.parameters=parameters;
		//
		if(!bean.preParseSql()) {
			return bean;
		}
		Map<String,Object> paraMap=new HashMap<>();
		if(parameters!=null) {
			for (Object para : parameters) {
				if(para instanceof SqlParam) {
					SqlParam p=(SqlParam) para;
					if(StringUtil.isEmpty(p.name)){
						throw new SmartJdbcException("Param name cann't be null");
					}
					paraMap.put(p.name, p.value);
				}
			}
		}
		return bean.parseSql(paraMap);//#
	}
	//
	public boolean preParseSql() {
		String[] regexs= {"\\#\\{[a-zA-Z_$][a-zA-Z0-9_$]*\\}","\\$\\{[a-zA-Z_$][a-zA-Z0-9_$]*\\}"};
		for (String regex : regexs) {
			Pattern p=Pattern.compile(regex);
			Matcher m = p.matcher(sql);
			if(m.find()) { 
			    return true;
			}
		}
		return false;
	}
	//
	/**
	 * 
	 * @param sql
	 * @param paraMap
	 * @return
	 */
	public SqlBean parseSql(Map<String,Object> paraMap) {
		Pattern p=Pattern.compile("\\#\\{([a-zA-Z_][a-zA-Z0-9_]*)\\}");
		Matcher m = p.matcher(sql);
		Pattern $p=Pattern.compile("\\$\\{([a-zA-Z_][a-zA-Z0-9_]*)\\}");
		Matcher $m = $p.matcher(sql);
		String newSql=sql;
		Object[] values=null;
		List<String> groups=new ArrayList<>();
		while(m.find()) { 
		    groups.add(m.group(1));
		}
		newSql=m.replaceAll("?");
		values=new Object[groups.size()];
		int i=0;
		for (String group : groups) {
			Object value=paraMap.get(group);
			if(value==null) {
				throw new SmartJdbcException("paraMap "+group+" not found.\nsql:"+sql+
						"\nall can choose paras is:"+paraMap.keySet()); 
			}
			values[i++]=value;
		}
		//
		while($m.find()) { 
			String group=$m.group(1);
			String replaceGroup="\\$\\{"+group+"\\}";
			Object value=paraMap.get(group);
			if(value==null) {
				throw new SmartJdbcException(group+" not found.\nsql:"+sql+
						"\nall can choose paras is:"+paraMap.keySet()); 
			}
			if(value instanceof String) {
				newSql=newSql.replaceAll(replaceGroup,"'"+value.toString()+"'");
			}else if(value instanceof Collection<?>){
				StringBuilder in=new StringBuilder();
				Collection<?> array=(Collection<?>)value;
				in.append("(");
				for (Object v : array.toArray()) {
					in.append(v).append(",");
				}
				if(in.length()>1) {
					in.deleteCharAt(in.length()-1);
				}
				in.append(")");
				newSql=newSql.replaceAll(replaceGroup,in.toString());
			}else {
				newSql=newSql.replaceAll(replaceGroup,value.toString());
			}
		}
		this.sql=newSql;
		this.parameters=values;
		return this;
	}
}
