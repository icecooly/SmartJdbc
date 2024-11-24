package io.itit.smartjdbc.service;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.annotations.ConvertFieldName;

/**
 * 
 * @author skydu
 *
 */
public class DefaultConvertFieldNameService implements IConvertFieldNameService{
	//
	private static Logger log=LoggerFactory.getLogger(DefaultConvertFieldNameService.class);
	//
	private Map<Class<?>,Method> convertFieldNameMethodMap=new HashMap<>();
	private Set<Class<?>> entityClassSet=new HashSet<>();
	//
	@Override
	public String convert(Class<?> entityClass, String fieldName) {
		if(entityClass==null) {
			return convertFieldNameAsCamelCase(fieldName);
		}
		Method method=convertFieldNameMethodMap.get(entityClass);
		if(method==null&&!entityClassSet.contains(entityClass)) {
			method=getConvertFieldNameMethod(entityClass);
			if(method!=null) {
				convertFieldNameMethodMap.put(entityClass, method);
			}
			entityClassSet.add(entityClass);
		}
		if(method==null) {
			return convertFieldNameAsCamelCase(fieldName);
		}else {
			try {
				return (String) method.invoke(null, fieldName);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private static Method getConvertFieldNameMethod(Class<?> entityClass) {
		Method[] methods=entityClass.getMethods();
		for (Method method : methods) {
			if (!Modifier.isStatic(method.getModifiers())) {
				continue;
			}
			ConvertFieldName convertFieldName=method.getAnnotation(ConvertFieldName.class);
			if(convertFieldName==null) {
				continue;
			}
			if(!method.getReturnType().equals(String.class)) {
				continue;
			}
			if(method.getParameterCount()!=1){
				continue;
			}
			if(!method.getParameterTypes()[0].equals(String.class)) {
				continue;
			}
			log.info("found convertFieldNameMethod {}",entityClass.getName());
			return method;
		}
		return null;
	}

	
	private String convertFieldNameAsCamelCase(String name) {
		StringBuffer result = new StringBuffer();
		for (char c : name.toCharArray()) {
			if (Character.isUpperCase(c)) {
				result.append("_");
			}
			result.append(Character.toLowerCase(c));
		}
		return result.toString();
	}
	
}
