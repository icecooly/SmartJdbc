package io.itit.smartjdbc.cache;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import io.itit.smartjdbc.annotations.Entity;

/**
 * 
 * @author skydu
 *
 */
public class EntityInfo {

	public Class<?> clazz;
	
	public Entity entity;
	
	public List<EntityFieldInfo> fieldList;//=ClassUtils.getFieldList(entityClass);
	
	public Map<String,Field> fieldMap;
}
