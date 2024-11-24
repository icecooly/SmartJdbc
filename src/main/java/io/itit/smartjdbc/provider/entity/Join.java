package io.itit.smartjdbc.provider.entity;

import io.itit.smartjdbc.enums.JoinType;

/**
 * 
 * @author skydu
 *
 */
public class Join {
	private JoinType joinType;
	private String table1Alias;
	private Class<?> table1;
	private String table1Name;
	private String table2Alias;
	private Class<?> table2;
	private String table2Name;
	private String[] table1Fields;
	private String[] table2Fields;
	//
	public Join() {
		
	}
	//
	public Join(JoinType joinType) {
		this.joinType=joinType;
	}
	//
	public String info() {
		StringBuilder info=new StringBuilder();
		info.append("\n");
		info.append(table1.getSimpleName()).append(" ");
		info.append(joinType.name()).append(" ").append(table2.getSimpleName()).append(" ").append(table2Alias);
		if(table1Fields!=null&&table2Fields!=null) {
			info.append(" on ");
			for(int i=0;i<table1Fields.length;i++) {
				info.append(table1Alias).append(".").append(table1Fields[i]).append("=").append(table2Alias).append(".").append(table2Fields[i]);
			}
		}
		return info.toString();
	}
	//
	/**
	 * @return the joinType
	 */
	public JoinType getJoinType() {
		return joinType;
	}
	/**
	 * @param joinType the joinType to set
	 */
	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}
	/**
	 * @return the table1Alias
	 */
	public String getTable1Alias() {
		return table1Alias;
	}
	/**
	 * @param table1Alias the table1Alias to set
	 */
	public void setTable1Alias(String table1Alias) {
		this.table1Alias = table1Alias;
	}
	/**
	 * @return the table1
	 */
	public Class<?> getTable1() {
		return table1;
	}
	/**
	 * @param table1 the table1 to set
	 */
	public void setTable1(Class<?> table1) {
		this.table1 = table1;
	}
	/**
	 * @return the table2Alias
	 */
	public String getTable2Alias() {
		return table2Alias;
	}
	/**
	 * @param table2Alias the table2Alias to set
	 */
	public void setTable2Alias(String table2Alias) {
		this.table2Alias = table2Alias;
	}
	/**
	 * @return the table2
	 */
	public Class<?> getTable2() {
		return table2;
	}
	/**
	 * @param table2 the table2 to set
	 */
	public void setTable2(Class<?> table2) {
		this.table2 = table2;
	}
	/**
	 * @return the table1Fields
	 */
	public String[] getTable1Fields() {
		return table1Fields;
	}
	/**
	 * @param table1Fields the table1Fields to set
	 */
	public void setTable1Fields(String[] table1Fields) {
		this.table1Fields = table1Fields;
	}
	/**
	 * @return the table2Fields
	 */
	public String[] getTable2Fields() {
		return table2Fields;
	}
	/**
	 * @param table2Fields the table2Fields to set
	 */
	public void setTable2Fields(String[] table2Fields) {
		this.table2Fields = table2Fields;
	}
	/**
	 * @return the table1Name
	 */
	public String getTable1Name() {
		return table1Name;
	}
	/**
	 * @param table1Name the table1Name to set
	 */
	public void setTable1Name(String table1Name) {
		this.table1Name = table1Name;
	}
	/**
	 * @return the table2Name
	 */
	public String getTable2Name() {
		return table2Name;
	}
	/**
	 * @param table2Name the table2Name to set
	 */
	public void setTable2Name(String table2Name) {
		this.table2Name = table2Name;
	}
	
}