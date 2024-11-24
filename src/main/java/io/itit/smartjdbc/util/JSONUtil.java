package io.itit.smartjdbc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 
 * @author skydu
 *
 */
public class JSONUtil {
	//
	private static org.slf4j.Logger logger=LoggerFactory.getLogger(JSONUtil.class);
	//
	public static interface JSONPropertyFilter{
		boolean apply(Object object, String name, Object arg2);
	}
	/**
	 *convert object to json string 
	 */
	public static String toJson(Object obj,
			JSONPropertyFilter propertyFilter,
			boolean prettyFormat){
		if(prettyFormat){
			return JSON.toJSONString(obj,new FastJsonPropertyFilter(propertyFilter),
					SerializerFeature.PrettyFormat);
		}else{
			return JSON.toJSONString(obj,new FastJsonPropertyFilter(propertyFilter));	
		}
	}
	//
	private static class FastJsonPropertyFilter implements PropertyFilter{
		JSONPropertyFilter propertyFilter;
		public FastJsonPropertyFilter(JSONPropertyFilter filter) {
			this.propertyFilter=filter;
		}
		@Override
		public boolean apply(Object arg0, String arg1, Object arg2) {
			return propertyFilter.apply(arg0, arg1, arg2);
		}
	} 
	/**
	 *convert object to json string 
	 */
	public static String toJson(Object obj){
		if(obj==null) {
			return null;
		}
		return JSON.toJSONString(obj);
	}
	//
	public static String toJsonDisableCircularReferenceDetect(Object obj){
		if(obj==null) {
			return null;
		}
		return JSON.toJSONString(obj,SerializerFeature.DisableCircularReferenceDetect);
	}
	
	/**
	 * 
	 * @param obj
	 * @param features
	 * @return
	 */
	public static String toJson(Object obj, SerializerFeature...features){
		if(obj==null) {
			return null;
		}
		return JSON.toJSONString(obj,features);
	}
	
	public static String toPrettyFormatJson(Object obj){
		if(obj==null) {
			return null;
		}
		return JSON.toJSONString(obj,SerializerFeature.PrettyFormat);
	}
	/**
	 * convert json string to class
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromJson(String str,Class<?>t){
		if(str==null) {
			return null;
		}
		try {
			return (T) JSON.parseObject(str, t);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("fromJson json:{} class:{}",str,t);
			throw e;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T fromJson(JSONObject jsonObject,Class<?>t){
		if(jsonObject==null) {
			return null;
		}
		try {
			return (T) jsonObject.toJavaObject(t);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("fromJson class:{}",t);
			throw e;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T fromJson(Map<String,Object> obj,Class<?>t){
		if(obj==null) {
			return null;
		}
		try {
			return (T) JSON.parseObject(toJson(obj), t);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("fromJson obj:{} class:{}",obj,t);
			throw e;
		}
		
	}
	/**
	 *convert json to class list 
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> fromJsonList(String str,Class<?>t){
		if(str==null) {
			return null;
		}
		try {
			return  (List<T>) JSON.parseArray(str, t);	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("fromJsonList failed str:{} t:{}",str,t);
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> fromJsonList(JSONArray jsonArray,Class<?>t){
		if(jsonArray==null) {
			return null;
		}
		try {
			return  (List<T>) jsonArray.toJavaList(t);	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("fromJsonList failed t:{}",t);
			throw e;
		}
	}
	
	/**
	 * convert json string to Map
	 * @param json
	 * @param keyType
	 * @param valueType
	 * @return
	 */
	public static <K, V> Map<K, V> fromJsonMap(String json, Class<K> keyType,  Class<V> valueType) {
		if(json==null) {
			return null;
		}
	    return JSON.parseObject(json,new TypeReference<Map<K, V>>(keyType, valueType) {});
	}
	
	public static <K, V> Map<K, List<V>> fromJsonMapListValue(String json, Class<K> keyType,  Class<V> valueType) {
		if(json==null) {
			return null;
		} 
		return JSON.parseObject(json,new TypeReference<Map<K, List<V>>>(keyType, valueType) {});
	}
	
	/**
	 * 
	 * @param <K>
	 * @param <V>
	 * @param json
	 * @param keyType
	 * @param valueType
	 * @return
	 */
	public static <K, V> Map<K, Set<V>> fromJsonMapSetValue(String json, Class<K> keyType,  Class<V> valueType) {
		if(json==null) {
			return null;
		} 
		return JSON.parseObject(json,new TypeReference<Map<K, Set<V>>>(keyType, valueType) {});
	}
	
	/**
	 * 
	 * @param <T>
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> Set<T> fromJsonSet(String json, Class<T> clazz){
		if(json==null) {
			return null;
		}
		return  JSON.parseObject(json,new TypeReference<Set<T>>(clazz){});
	}
	
	public static <T> T clone(Object src, SerializerFeature...features) {
		if(src==null) {
			return null;
		}
		String json=JSONUtil.toJson(src, features);
		return JSONUtil.fromJson(json, src.getClass());
	}
	//
	public static <T> List<T> cloneList(List<T> src,Class<T> clazz) {
		if(src==null) {
			return null;
		}
		String json=JSONUtil.toJson(src);
		return JSONUtil.fromJsonList(json, clazz);
	}
	//
	public static <T> T cloneWithNull(Object src) {
		if(src==null) {
			return null;
		}
		String json=JSONUtil.toJson(src, SerializerFeature.WriteMapNullValue);
		return JSONUtil.fromJson(json, src.getClass());
	}
	//
	public static <T> T clone(Object src, Class<T> clazz, SerializerFeature...features) {
		if(src==null) {
			return null;
		}
		String json=JSONUtil.toJson(src, features);
		return JSONUtil.fromJson(json, clazz);
	}
	//
	@SuppressWarnings("unchecked")
	public static Map<String,Object> toMap(JSONObject obj) {
		if(obj==null) {
			return null;
		}
		return JSONObject.toJavaObject(obj, Map.class);
	}
	//
	public static List<Object> toList(JSONArray array){
		if(array==null) {
			return null;
		}
		List<Object> ret=new ArrayList<>();
		for (int i = 0; i < array.size(); i++) {
			Object obj=array.get(i);
			if(obj instanceof JSONObject) {
				ret.add(toMap((JSONObject)obj));
			}else {
				ret.add(obj);
			}
        }
		return ret;
	}
	//
	public static final int DEFAULT_MAX_CHAR_LENGTH=1024*100;//
	public static final int DEFAULT_MAX_LIST_SIZE=10;
	//
	public static String dump(Object o, SerializerFeature... feature) {
		return dump(o, DEFAULT_MAX_CHAR_LENGTH, DEFAULT_MAX_LIST_SIZE, feature);
	}
	//
	public static String dump(Object o,int maxLength, int maxListSize, SerializerFeature... features) {
		if(o==null){
			return "<null>";
		}
		try {
			if(maxLength==0) {
				maxLength=DEFAULT_MAX_CHAR_LENGTH;
			}
			if(maxListSize==0) {
				maxListSize=DEFAULT_MAX_LIST_SIZE;
			}
			String data=null;
			if(o instanceof List) {
				List<?> list=(List<?>)o;
				if(list.size()>maxListSize) {
					List<?> subList=list.subList(0, maxListSize);
					StringBuilder sb=new StringBuilder();
					sb.append(JSON.toJSONString(subList, features));
					sb.append("...total:").append(list.size());
					data=sb.toString();
				}else {
					data=JSON.toJSONString(list, features);
				}
			}else {
				data=JSON.toJSONString(o, features);
			}
			if(data==null) {
				return "<null>";
			}
			if(data.length()>maxLength) {
				data=data.substring(0,maxLength);
			}
			return data;
		} catch (Throwable e) {
			logger.warn("class:{}",o.getClass());
			logger.warn(e.getMessage(),e);
			return "";
		}
	}
}
