package io.itit.smartjdbc.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 
 * @author skydu
 *
 */
public class ArrayUtils {
	//
	public static Set<Class<?>> arrayTypes=new HashSet<>();
	static {
		arrayTypes.add(int[].class);
		arrayTypes.add(short[].class);
		arrayTypes.add(byte[].class);
		arrayTypes.add(long[].class);
		arrayTypes.add(float[].class);
		arrayTypes.add(double[].class);
	}
	/**
	 * 
	 * @param value
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Object[] convert(final Object value) {
		if(value==null) {
			return null;
		}
		Class<?> fieldType=value.getClass();
		if (fieldType.equals(int[].class)) {
			return ArrayUtils.convert((int[])value);
		}else if(fieldType.equals(short[].class)) {
			return ArrayUtils.convert((short[])value);
		}else if(fieldType.equals(byte[].class)) {
			return ArrayUtils.convert((byte[])value);
		}else if(fieldType.equals(long[].class)) {
			return ArrayUtils.convert((long[])value);
		}else if(fieldType.equals(float[].class)) {
			return ArrayUtils.convert((float[])value);
		}else if(fieldType.equals(double[].class)) {
			return ArrayUtils.convert((double[])value);
		}
		// 非基础类数组
		else if  (value.getClass().isArray()) {
			return ArrayUtils.convert((Object[])value);
		}
		else if (value instanceof Collection){
			return ((Collection)value).toArray();
		}
		else {
			return new Object[] {value};
		}
	}
	//
	public static Object[] convert(final int[] t) {
		if(t==null) {
			return null;
		}
		Object[] ret=new Object[t.length];
		for(int i=0;i<t.length;i++) {
			ret[i]=t[i];
		}
		return ret;
	}
	//
	public static  Object[] convert(final byte[] t) {
		if(t==null) {
			return null;
		}
		Object[] ret=new Object[t.length];
		for(int i=0;i<t.length;i++) {
			ret[i]=t[i];
		}
		return ret;
	}
	//
	public static  Object[] convert(final short[] t) {
		if(t==null) {
			return null;
		}
		Object[] ret=new Object[t.length];
		for(int i=0;i<t.length;i++) {
			ret[i]=t[i];
		}
		return ret;
	}
	//
	public static Object[] convert(final long[] t) {
		if(t==null) {
			return null;
		}
		Object[] ret=new Object[t.length];
		for(int i=0;i<t.length;i++) {
			ret[i]=t[i];
		}
		return ret;
	}
	//
	public static Object[] convert(final float[] t) {
		if(t==null) {
			return null;
		}
		Object[] ret=new Object[t.length];
		for(int i=0;i<t.length;i++) {
			ret[i]=t[i];
		}
		return ret;
	}
	//
	public static Object[] convert(final double[] t) {
		if(t==null) {
			return null;
		}
		Object[] ret=new Object[t.length];
		for(int i=0;i<t.length;i++) {
			ret[i]=t[i];
		}
		return ret;
	}
	//
	public static  <T> Object[] convert(final T[] t) {
		if(t==null) {
			return null;
		}
		Object[] ret=new Object[t.length];
		for(int i=0;i<t.length;i++) {
			ret[i]=t[i];
		}
		return ret;
	}
	//
	public static Set<String> toSet(String ... includeFields){
		if(includeFields==null) {
			return null;
		}
		Set<String> set=new HashSet<>();
		for (String field : includeFields) {
			set.add(field);
		}
		return set;
	}
	//
	public static <T> boolean contains(List<T> array, String fieldName, Object target) {
		if (array == null) {
			return false;
		}
		for (Object bean : array) {
			Object v = getFieldValue(bean, fieldName);
			if(v==null) {
				return false;
			}
			if (v.equals(target)) {
				return true;
			}
		}
		return false;
	}
	
	//
	public static Object getFieldValue(Object bean, String fieldName) {
		try {
			return getFieldValue(bean, bean.getClass().getField(fieldName));
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Object getFieldValue(Object bean, Field f) {
		try {
			return f.get(bean);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static boolean isArrayType(Field field) {
		Class<?> fieldType=field.getType();
		if (arrayTypes.contains(fieldType)) {
			return true;
		}
		if(Collection.class.isAssignableFrom(fieldType)) {
			return true;
		}
		return false;
	}
	
	public static boolean isArrayEmpty(Object array) {
		if(array==null) {
			return true;
		}
		Object[] list=convert(array);
		return list.length==0;
	}
	//
	public static <T> boolean isEmpty(T[] array) {
		return array == null || array.length == 0;
	}
}
