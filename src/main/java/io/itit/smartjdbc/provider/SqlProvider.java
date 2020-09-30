package io.itit.smartjdbc.provider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.SqlBean;
import io.itit.smartjdbc.annotations.EntityField;
import io.itit.smartjdbc.annotations.PrimaryKey;
import io.itit.smartjdbc.cache.Caches;
import io.itit.smartjdbc.cache.EntityInfo;
import io.itit.smartjdbc.util.ClassUtils;
import io.itit.smartjdbc.util.DumpUtil;

/**
 * 
 * @author skydu
 *
 */
public abstract class SqlProvider {
	//
	private static Logger logger=LoggerFactory.getLogger(SqlProvider.class);
	//
	public static final String MAIN_TABLE_ALIAS="a";
	//
	protected SmartDataSource smartDataSource;
	//
	public SqlProvider(SmartDataSource smartDataSource) {
		this.smartDataSource=smartDataSource;
	}
	//
	protected static final HashSet<Class<?>> WRAP_TYPES=new HashSet<>();
	static{
		WRAP_TYPES.add(Boolean.class);
		WRAP_TYPES.add(Character.class);
		WRAP_TYPES.add(Byte.class);
		WRAP_TYPES.add(Short.class);
		WRAP_TYPES.add(Integer.class);
		WRAP_TYPES.add(Long.class);
		WRAP_TYPES.add(BigDecimal.class);
		WRAP_TYPES.add(BigInteger.class);
		WRAP_TYPES.add(Double.class);
		WRAP_TYPES.add(Float.class);
		WRAP_TYPES.add(String.class);
		WRAP_TYPES.add(Date.class);
		WRAP_TYPES.add(Timestamp.class);
		WRAP_TYPES.add(java.sql.Date.class);
		WRAP_TYPES.add(Byte[].class);
		WRAP_TYPES.add(byte[].class);
		WRAP_TYPES.add(int.class);
		WRAP_TYPES.add(boolean.class);
		WRAP_TYPES.add(char.class);
		WRAP_TYPES.add(byte.class);
		WRAP_TYPES.add(short.class);
		WRAP_TYPES.add(int.class);
		WRAP_TYPES.add(long.class);
		WRAP_TYPES.add(float.class);
		WRAP_TYPES.add(double.class);
	}
	//
	protected SqlBean createSqlBean(String sql,Object[] parameters) {
		SqlBean bean=new SqlBean(sql,parameters);	
		if(logger.isDebugEnabled()) {
			logger.debug("SqlBean {}",DumpUtil.dump(bean));
		}
		return bean;
	}
	
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public String convertFieldName(String name) {
		return smartDataSource.convertFieldName(name);
	}
	
	/**
	 * 
	 * @param clazz
	 * @return
	 */
	public List<Field> getPrimaryKey(Class<?> clazz){
		List<Field> primaryKey=new ArrayList<>();
		List<Field> fields=getPersistentFields(clazz);
		Field idField=null;
		for (Field field : fields) {
			if(field.getAnnotation(PrimaryKey.class)!=null) {
				primaryKey.add(field);
			}
			if(field.getName().equals("id")) {
				idField=field;
			}
		}
		if(primaryKey.size()==0&&idField==null) {
			throw new SmartJdbcException("PrimaryKey not found in "+clazz.getName());
		}
		if(primaryKey.size()==0) {
			return Arrays.asList(idField);
		}
		return primaryKey;
	}
	
	/**
	 * 
	 * @param field
	 * @return
	 */
	public boolean isPersistentField(Field field) {
		if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
			return false;
		}
		EntityField entityField=field.getAnnotation(EntityField.class);
		if(entityField!=null) {
			return entityField.persistent();
		}
		return true;
	}
	/**
	 * 
	 * @param entityClass
	 * @return
	 */
	public List<Field> getPersistentFields(Class<?> entityClass){
		List<Field> fields=new ArrayList<>();
		List<Field> fieldList=ClassUtils.getFieldList(entityClass);
		for (Field field : fieldList) {
			if(isPersistentField(field)) {
				fields.add(field);
			}
		}
		return fields;
	}
	
	/**
	 * 
	 * @param bean
	 * @param fieldName
	 * @return
	 */
	public static Object getEntityFieldValue(Object bean,String fieldName){
		try {
			EntityInfo info=Caches.getEntityInfo(bean.getClass());
			Field field=info.fieldMap.get(fieldName);
			if(field!=null) {
				if(!field.isAccessible()) {
					field.setAccessible(true);
				}
				return field.get(bean);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	/**
	 * 
	 * @param entityClass
	 * @return
	 */
	public String getTableName(Class<?> entityClass) {
		return smartDataSource.getTableName(entityClass);
	}
	/**
	 * 
	 * @return
	 */
	public abstract SqlBean build();
	
	/**
	 * 
	 * @return
	 */
	public String identifier() {
		return "";
	}
}
