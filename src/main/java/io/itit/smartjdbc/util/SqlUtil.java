package io.itit.smartjdbc.util;

import java.util.regex.Pattern;

import io.itit.smartjdbc.Config;
import io.itit.smartjdbc.SmartJdbcException;

/**
 * 
 * @author skydu
 *
 */
public class SqlUtil {
	//
	/**
	 * 
	 * @param field
	 */
	public static void checkColumnName(String field) {
		String regex=Config.getColumnNameRegex();
		if(StringUtil.isEmpty(regex)) {
			return;
		}
		if(!Pattern.matches(regex, field)) {
			throw new SmartJdbcException("SQL错误 columnName invalid "+field+
					"\n配置查看Config.getColumnNameRegex()");
		}
    }
}
