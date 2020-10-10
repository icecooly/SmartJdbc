package io.itit.smartjdbc.provider.entity;

/**
 * 
 * @author skydu
 *
 */
public class Join {
	public String table1Alias;
	public Class<?> table1;
	public String table2Alias;
	public Class<?> table2;
	public String[] table1Fields;
	public String[] table2Fields;
}