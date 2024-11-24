package io.itit.smartjdbc.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.Types;
import io.itit.smartjdbc.cache.CacheManager;
import io.itit.smartjdbc.domain.EntityFieldInfo;
import io.itit.smartjdbc.domain.EntityInfo;
import io.itit.smartjdbc.enums.DatabaseType;
import io.itit.smartjdbc.provider.DeleteProvider;
import io.itit.smartjdbc.provider.InsertProvider;
import io.itit.smartjdbc.provider.SelectProvider;
import io.itit.smartjdbc.provider.UpdateProvider;
import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.factory.DeleteProviderFactory;
import io.itit.smartjdbc.provider.factory.InsertProviderFactory;
import io.itit.smartjdbc.provider.factory.SelectProviderFactory;
import io.itit.smartjdbc.provider.factory.UpdateProviderFactory;
import io.itit.smartjdbc.util.JSONUtil;

/**
 * 
 * @author skydu
 *
 */
public abstract class BaseEntityDAO extends BaseDAO{
	//
	private static final Logger logger=LoggerFactory.getLogger(BaseEntityDAO.class);
	/**
	 * 
	 * @param type
	 * @param rs
	 * @return
	 */
	protected <T> T convertBean(Class<T> type,ResultSet rs){
		try{
			T instance=type.getDeclaredConstructor().newInstance();
			convertBean(instance,rs);
			return instance;
		}catch(Exception e){
			throw new SmartJdbcException(e.getMessage(),e);
		}
	} 
	//
	protected void convertBean(Object o, ResultSet rs, String... excludeFields)
			throws Exception {
		convertBean(o, null, rs, excludeFields);
	}
	//
	protected List<Field> getEntityFields(Class<?> clazz) {
		List<Field> fieldList = new ArrayList<>();
		Set<String> nameSet=new HashSet<>();
		for(;clazz != Object.class;clazz = clazz.getSuperclass()) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (Modifier.isStatic(field.getModifiers()) || 
						Modifier.isFinal(field.getModifiers())) {
					continue;
				}
				if(nameSet.contains(field.getName())) {
					continue;
				}
				nameSet.add(field.getName());
				fieldList.add(field);
			}
		}
		return fieldList;
	}
	
	private Boolean getBoolean(ResultSet rs, String fieldName) throws SQLException {
		return rs.getObject(fieldName)==null?null:rs.getBoolean(fieldName);
	}
	/**
	 * 
	 * @param o
	 * @param preAliasField
	 * @param rs
	 * @param excludeFields
	 * @throws Exception
	 */
	protected void convertBean(Object o,String preAliasField, ResultSet rs, String... excludeFields)
			throws Exception {
		Set<String> excludesNames = new TreeSet<String>();
		for (String e : excludeFields) {
			excludesNames.add(e);
		}
		Class<?> entityClass = o.getClass();
		EntityInfo entityInfo=CacheManager.getEntityInfo(entityClass);
		//
		ResultSetMetaData rsmd=rs.getMetaData();
		int columnCount=rsmd.getColumnCount();
		Set<String> columnNames=new HashSet<>();
		for(int i=1;i<=columnCount;i++) {
			columnNames.add(rsmd.getColumnLabel(i));
		}
		List<EntityFieldInfo> fieldList=entityInfo.getFieldList();
		for (EntityFieldInfo f : fieldList) {
			Field field=f.getField();
			String name=field.getName();
			if (excludesNames.contains(name)) {
				continue;
			}
			
 			String fieldName=convertFieldNameToColumnName(entityClass,name);
			if(preAliasField!=null) {
				fieldName=convertFieldNameToColumnName(preAliasField)+fieldName;
			}
			Class<?> fieldType = f.getFieldType();
			if(!columnNames.contains(fieldName)) {
				if(Types.WRAP_TYPES.contains(fieldType)){
					continue;
				}
			}
			try {
				Object value = null;
				if (fieldType.equals(String.class)) {
					value = rs.getString(fieldName);
				} else if (fieldType.equals(int.class)) {
					value = rs.getInt(fieldName);
				} else if (fieldType.equals(Integer.class)) {
					value = rs.getObject(fieldName)==null?null:rs.getInt(fieldName);
				} else if (fieldType.equals(short.class)) {
					value = rs.getShort(fieldName);
				} else if (fieldType.equals(Short.class)) {
					value = rs.getObject(fieldName)==null?null:rs.getShort(fieldName);
				} else if (fieldType.equals(long.class)) {
					value = rs.getLong(fieldName);
				} else if (fieldType.equals(Long.class)) {
					value = rs.getObject(fieldName)==null?null:rs.getLong(fieldName);
				} else if (fieldType.equals(double.class)) {
					value = rs.getDouble(fieldName);
				} else if (fieldType.equals(Double.class)) {
					value = rs.getObject(fieldName)==null?null:rs.getDouble(fieldName);
				} else if (fieldType.equals(float.class)) {
					value = rs.getFloat(fieldName);
				} else if (fieldType.equals(Float.class)) {
					value = rs.getObject(fieldName)==null?null:rs.getFloat(fieldName);
				} else if (fieldType.equals(Date.class)) {
					value = rs.getTimestamp(fieldName);
				} else if (fieldType.equals(boolean.class)) {
					value=getBoolean(rs, fieldName);
					if(value==null) {
						value=false;
					}
				} else if (fieldType.equals(Boolean.class)) {
					value=getBoolean(rs, fieldName);
				} else if (fieldType.equals(BigDecimal.class)) {
					value = rs.getBigDecimal(fieldName);
				} else if (fieldType.equals(byte[].class)) {
					Blob bb = rs.getBlob(fieldName);
					if (bb != null) {
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						copy(bb.getBinaryStream(), bos);
						value = bos.toByteArray();
					}
				} else if (Types.ARRAY_TYPES.contains(fieldType)&&
						(getDatabaseType().equals(DatabaseType.POSTGRESQL)||
						getDatabaseType().equals(DatabaseType.KINGBASE))
						) {
					Array array=rs.getArray(fieldName);
					if(array!=null) {
						value = rs.getArray(fieldName).getArray();
					}
				} else {
					if(columnNames.contains(fieldName)) {
						String strValue=rs.getString(fieldName);
						if(strValue!=null){
							Type genericType=field.getGenericType();
							if ( genericType instanceof ParameterizedType ) {  
								 Type[] typeArguments = ((ParameterizedType)genericType).getActualTypeArguments();  
								 if(typeArguments.length==1) {
									 if(List.class.isAssignableFrom(fieldType) && (typeArguments[0] instanceof Class)) {
										 value=JSONUtil.fromJsonList(strValue,(Class<?>) typeArguments[0]);
									 }else if(Set.class.isAssignableFrom(fieldType) && (typeArguments[0] instanceof Class)) {
										 value=JSONUtil.fromJsonSet(strValue,(Class<?>) typeArguments[0]);
									 }
								 }else if(typeArguments.length==2) {
									 if(Map.class.isAssignableFrom(fieldType) && (typeArguments[0] instanceof Class) && (typeArguments[1] instanceof Class)) {
										 value=JSONUtil.fromJsonMap(strValue,(Class<?>) typeArguments[0],(Class<?>) typeArguments[1]);
									 }else if(Map.class.isAssignableFrom(fieldType) && (typeArguments[0] instanceof Class) && (typeArguments[1] instanceof ParameterizedType)){
										 Type[] typeArguments2 = ((ParameterizedType)typeArguments[1]).getActualTypeArguments();
										 if(typeArguments2.length==1) {
											 Type rowType=((ParameterizedType)typeArguments[1]).getRawType();
											 if(rowType.equals(List.class) && (typeArguments2[0] instanceof Class)) {
												 value=JSONUtil.fromJsonMapListValue(strValue,(Class<?>) typeArguments[0],(Class<?>) typeArguments2[0]);
											 }else if(rowType.equals(Set.class) && (typeArguments2[0] instanceof Class)) {
												 value=JSONUtil.fromJsonMapSetValue(strValue,(Class<?>) typeArguments[0],(Class<?>) typeArguments2[0]);
											 }
										 }
									 }
								 }
							 }else {
								 value=JSONUtil.fromJson(strValue,fieldType);
							 }
						}
					}else {
						Type genericType=field.getGenericType();
						if ( genericType instanceof Class) { //only support Class
							Class<?> subClass=(Class<?>)genericType;
							value=subClass.getDeclaredConstructor().newInstance();
							String subPreAliasField=f.getName()+"$";
							convertBean(value, subPreAliasField, rs, excludeFields);
						}
					}
				}
				if (value != null) {
					field.set(o, value);
				}
			} catch (Exception e) {
				logger.error("convertBean failed.entityClass:{} fieldName:{}",entityClass,f.getName());
				throw new SmartJdbcException(e.getMessage(),e);
			}
			
		}
	}
	//
	private static long copy(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[4096];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
	//
	/**
	 * 
	 * @param name
	 * @return
	 */
	protected  String convertFieldNameToColumnName(String name) {
		return getSmartDataSource().convertFieldNameToColumnName(null, name);
	}
	/**
	 * 
	 * @param entityClass
	 * @param name
	 * @return
	 */
	protected  String convertFieldNameToColumnName(Class<?> entityClass, String name) {
		return getSmartDataSource().convertFieldNameToColumnName(entityClass, name);
	}
	//
	/**
	 * 
	 * @param sql
	 * @param parameters
	 * @return
	 */
	public int executeUpdate(String sql,Object... parameters) {
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return super.executeUpdate(sqlBean.sql, sqlBean.parameters);
	}
	//
	public boolean queryForBoolean(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForBoolean(sqlBean.sql, sqlBean.parameters);
	}
	//
	public String queryForString(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForString(sqlBean.sql, sqlBean.parameters);
	}
	//
	public double queryForDouble(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForDouble(sqlBean.sql, sqlBean.parameters);
	}
	//
	public float queryForFloat(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForFloat(sqlBean.sql, sqlBean.parameters);
	}
	//
	public Integer queryForInteger(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForInteger(sqlBean.sql, sqlBean.parameters);
	}
	//
	public int queryForInt(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForInt(sqlBean.sql, sqlBean.parameters);
	}
	//
	public long queryForLong(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForLong(sqlBean.sql, sqlBean.parameters);
	}
	//
	public short queryForShort(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForShort(sqlBean.sql, sqlBean.parameters);
	}
	//
	public BigDecimal queryForBigDecimal(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForBigDecimal(sqlBean.sql, sqlBean.parameters);
	}
	//
	public byte queryForByte(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForByte(sqlBean.sql, sqlBean.parameters);
	}
	//
	public Date queryForDate(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForDate(sqlBean.sql, sqlBean.parameters);
	}
	//
	public List<Boolean> queryForBooleans(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForBooleans(sqlBean.sql, sqlBean.parameters);
	}
	//
	public List<String> queryForStrings(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForStrings(sqlBean.sql, sqlBean.parameters);
	}
	//
	public List<Double> queryForDoubles(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForDoubles(sqlBean.sql, sqlBean.parameters);
	}
	//
	public List<Float> queryForFloats(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForFloats(sqlBean.sql, sqlBean.parameters);
	}
	//
	public List<Integer> queryForIntegers(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForIntegers(sqlBean.sql, sqlBean.parameters);
	}
	//
	public List<Long> queryForLongs(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForLongs(sqlBean.sql, sqlBean.parameters);
	}
	//
	public List<Short> queryForShorts(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForShorts(sqlBean.sql, sqlBean.parameters);
	}
	//
	public List<BigDecimal> queryForBigDecimals(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForBigDecimals(sqlBean.sql, sqlBean.parameters);
	}
	//
	public List<Byte> queryForBytes(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForBytes(sqlBean.sql, sqlBean.parameters);
	}
	//
	public List<Date> queryForDates(String sql,Object ...parameters){
		SqlBean sqlBean=SqlBean.build(sql, parameters);
		return  super.queryForDates(sqlBean.sql, sqlBean.parameters);
	}
	//
	//
	public InsertProvider insertProvider() {
		return InsertProviderFactory.create(getSmartDataSource());
	}
	//
	public UpdateProvider updateProvider() {
		return UpdateProviderFactory.create(getSmartDataSource());
	}
	//
	public DeleteProvider deleteProvider() {
		return DeleteProviderFactory.create(getSmartDataSource());
	}
	//
	public SelectProvider selectProvider() {
		return SelectProviderFactory.create(getSmartDataSource());
	}
}
