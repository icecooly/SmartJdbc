package io.itit.smartjdbc.cache;

import java.lang.reflect.Field;

import io.itit.smartjdbc.annotations.EntityField;
import io.itit.smartjdbc.annotations.LeftJoin;

/**
 * 
 * @author skydu
 *
 */
public class EntityFieldInfo {

	public Field field;
	
	public EntityField entityField;
	
	public LeftJoin leftJoin;
}
