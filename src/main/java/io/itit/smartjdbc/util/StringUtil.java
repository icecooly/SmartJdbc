package io.itit.smartjdbc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author icecooly
 *
 */
public class StringUtil {

	/**
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isEmpty(String input){
		if(input==null||input.length()==0){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param values
	 * @return
	 */
	public static Object[] convert(String[] values) {
		if(values==null) {
			return null;
		}
		Object[] oValues=new Object[values.length];
		for(int i=0;i<values.length;i++) {
			oValues[i]=values[i];
		}
		return oValues;
	}
	
	/**
	 * 去除字符串中的空格、回车、换行符、制表符
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
}
