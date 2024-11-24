package io.itit.smartjdbc.service;

/**
 * 
 * @author skydu
 *
 */
public interface IConvertFieldNameService {

	/**
	 * 
	 * @param entityClass
	 * @param fieldName
	 * @return
	 */
	String convert(Class<?> entityClass, String fieldName);
}
