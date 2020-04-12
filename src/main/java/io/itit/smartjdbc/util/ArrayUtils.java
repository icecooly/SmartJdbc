package io.itit.smartjdbc.util;

import java.util.Collection;

/**
 * 
 * @author skydu
 *
 */
public class ArrayUtils {

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
		}else if(fieldType.equals(String[].class)) {
			return ArrayUtils.convert((String[])value);
		}else if (value instanceof Collection) {
			return ((Collection)value).toArray();
		}
		return new Object[] {value};
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
}
