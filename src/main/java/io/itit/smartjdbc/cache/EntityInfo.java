package io.itit.smartjdbc.cache;

import java.util.List;

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
}
