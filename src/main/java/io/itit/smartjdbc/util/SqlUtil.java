package io.itit.smartjdbc.util;

import io.itit.smartjdbc.enums.DatabaseType;

/**
 * 
 * @author skydu
 *
 */
public class SqlUtil {

	/*
	 * 
	 */
	public static String identifier(DatabaseType type) {
		if(type.equals(DatabaseType.MYSQL)) {
			return "`";
		}
		if(type.equals(DatabaseType.POSTGRESQL)) {
			return "\"";
		}
		if(type.equals(DatabaseType.KINGBASE)) {
			return "\"";
		}
		return "";
	}
}
