package io.itit.smartjdbc.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import io.itit.smartjdbc.enums.DatabaseType;

/**
 * 
 * @author skydu
 *
 */
public class SmartJdbcUtils {

	private static Logger logger=LoggerFactory.getLogger(SmartJdbcUtils.class);
	
	/**
	 * 
	 * @param type
	 */
	public static boolean isBasePostgresql(DatabaseType type) {
		if (type.equals(DatabaseType.POSTGRESQL)||
				type.equals(DatabaseType.KINGBASE)
				) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public static String identifier(DatabaseType type) {
		if(type.equals(DatabaseType.MYSQL)) {
			return "`";
		}
		if(isBasePostgresql(type)) {
			return "\"";
		}
		return "";
	}
	/**
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isEmpty(String input) {
		if (input == null || input.length() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isEmptyWithTrim(String input) {
		if (input == null || input.length() == 0) {
			return true;
		}
		input = input.trim();
		if (input == null || input.length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param sql
	 */
	public static void checkSqlValidity(String sql) {
		if (isEmpty(sql)) {
			return;
		}
		if (sql.indexOf(";") == -1 && sql.indexOf("--") == -1) {
			return;
		}
		sql = sql.trim();
		if (sql.endsWith(";")) {
			sql = sql.substring(0, sql.length() - 1);
		}
		ParseResult parser = parse(sql);
		List<String> childSqlList = parser.getChildSqlList();
		childSqlList.forEach(childSql -> {
			if (childSql.contains(";")) {
				throw new RuntimeException("sql not support");
			}
			if (childSql.contains("--")) {
				throw new RuntimeException("sql not support");
			}
		});

	}

	/**
	 * 解析并返回对应结果
	 * 
	 * @param sql
	 * @return
	 */
	public static ParseResult parse(String sql) {
		ParseResult build = ParseResult.build();
		List<String> childSqlList = build.getChildSqlList();
		List<String> charSqlList = build.getCharSqlList();
		StringBuilder childSql = new StringBuilder();
		StringBuilder charSql = new StringBuilder();
		boolean isOpen = false;
		int length = sql.length();
		// 最后一个值 不校验
		for (int i = 0; i < length; i++) {
			String val = String.valueOf(sql.charAt(i));
			if (isOpen) {
				if ("'".equals(val)) {
					isOpen = false;
					charSqlList.add(charSql.toString());
					charSql.delete(0, charSql.length());
				} else {
					charSql.append(val);
				}
			} else {
				if ("'".equals(val)) {
					isOpen = true;
					childSqlList.add(childSql.toString());
					childSql.delete(0, childSql.length());
				} else {
					childSql.append(val);
					if (i == sql.length() - 1) {
						childSqlList.add(childSql.toString());
					}
				}
			}
		}
		return build;
	}

	private static class ParseResult {
		private List<String> childSqlList = Lists.newArrayList();
		private List<String> charSqlList = Lists.newArrayList();

		private static ParseResult build() {
			return new ParseResult();
		}

		public List<String> getChildSqlList() {
			return this.childSqlList;
		}

		public List<String> getCharSqlList() {
			return this.charSqlList;
		}
	}

	public static void close(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException se) {
			logger.error(se.getMessage(), se);
		}
	}

	//
	public static void close(PreparedStatement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException se) {
			logger.error(se.getMessage(), se);
		}
	}

	//
	public static void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException se) {
			logger.error(se.getMessage(), se);
		}
	}
	//
	public static boolean isChanged(Object oldV, Object newV) {
		if(oldV==null&&newV==null) {
			return false;
		}
		if(oldV==null||newV==null) {
			return true;
		}
		if(oldV.equals(newV)) {
			return false;
		}
		return true;
	}
	//
	public static Set<String> getAliasList(String sql){
		if(sql==null) {
			return Collections.emptySet();
		}
		String[] ss=sql.split("\\.");
		if(ss.length<2) {
			return Collections.emptySet();
		}
		Set<String> tableAlias=new HashSet<>();
		for(int i=0;i<ss.length-1;i++) {
			String e=ss[i];
			if(e==null||e.isBlank()) {
				continue;
			}
			e=e.trim();
			int charLength=e.length();
			StringBuilder alias=new StringBuilder();
			for(int j=0;j<charLength;j++) {
				char c=e.charAt(j);
				if(Character.isLetter(c)||Character.isDigit(c)) {
					alias.append(c);
				}else {
					alias.setLength(0);
				}
			}
			if(alias.length()>0) {
				tableAlias.add(alias.toString());
			}
		}
		return tableAlias;
	} 
}
