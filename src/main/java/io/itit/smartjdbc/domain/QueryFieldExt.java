package io.itit.smartjdbc.domain;

import java.util.List;

/**
 * 
 * @author skydu
 *
 */
public class QueryFieldExt {
	
	private String fieldId;//
	
	private JoinSetting join;
	
	private List<JoinSetting> joins;
	
	private QueryFieldSetting queryField;
	
	private List<JoinSetting> joinsList;
	
	private String tableAlias;
	
	private String tableName;//可能是关联表
	//

	/**
	 * @return the fieldId
	 */
	public String getFieldId() {
		return fieldId;
	}

	/**
	 * @param fieldId the fieldId to set
	 */
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	/**
	 * @return the join
	 */
	public JoinSetting getJoin() {
		return join;
	}

	/**
	 * @param join the join to set
	 */
	public void setJoin(JoinSetting join) {
		this.join = join;
	}

	/**
	 * @return the joins
	 */
	public List<JoinSetting> getJoins() {
		return joins;
	}

	/**
	 * @param joins the joins to set
	 */
	public void setJoins(List<JoinSetting> joins) {
		this.joins = joins;
	}

	/**
	 * @return the queryField
	 */
	public QueryFieldSetting getQueryField() {
		return queryField;
	}

	/**
	 * @param queryField the queryField to set
	 */
	public void setQueryField(QueryFieldSetting queryField) {
		this.queryField = queryField;
	}

	/**
	 * @return the joinsList
	 */
	public List<JoinSetting> getJoinsList() {
		return joinsList;
	}

	/**
	 * @param joinsList the joinsList to set
	 */
	public void setJoinsList(List<JoinSetting> joinsList) {
		this.joinsList = joinsList;
	}

	/**
	 * @return the tableAlias
	 */
	public String getTableAlias() {
		return tableAlias;
	}

	/**
	 * @param tableAlias the tableAlias to set
	 */
	public void setTableAlias(String tableAlias) {
		this.tableAlias = tableAlias;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
}
