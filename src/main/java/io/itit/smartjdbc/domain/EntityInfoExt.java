package io.itit.smartjdbc.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.itit.smartjdbc.enums.JoinType;
import io.itit.smartjdbc.provider.SqlProvider;
import io.itit.smartjdbc.provider.entity.Join;
import io.itit.smartjdbc.provider.entity.Joins;
import io.itit.smartjdbc.util.SmartJdbcUtils;

/**
 * 
 * @author skydu
 *
 */
public class EntityInfoExt {

	private String tableName;
	
	private String tableComment;
	
	private List<EntityFieldExt> fieldList;
	
	private Map<String,EntityFieldExt> fieldMap;
	
	private Joins joins;//left join tableName alias on 
	
	//
	public EntityInfoExt() {
		joins=new Joins();
		fieldList=new ArrayList<>();
		fieldMap=new HashMap<>();
	}
	//
	public static EntityInfoExt create(String tableName) {
		EntityInfoExt info=new EntityInfoExt();
		info.tableName=tableName;
		return info;
	}
	//
	//
	public void addJoins(Map<String, EntityInfoExt> allEntity) {
		for (EntityFieldExt fieldInfo : fieldList) {
			EntityFieldSetting entityField = fieldInfo.getEntityField();
			JoinSetting leftJoin=fieldInfo.getLeftJoin();
			if(leftJoin!=null) {
				Join join=addJoin(JoinType.LEFT_JOIN,SqlProvider.MAIN_TABLE_ALIAS,
						tableName,leftJoin.table2Name,
						leftJoin.table1Fields,leftJoin.table2Fields);
				fieldInfo.setTableAlias(join.getTable2Alias());
			}else if(entityField!=null&&!SmartJdbcUtils.isEmpty(entityField.foreignKeyFields)) {
				String foreignKeyId = entityField.foreignKeyFields;
				String[] foreignKeyIds=foreignKeyId.split(",");
				EntityInfoExt table1=this;
				String table1Alias=SqlProvider.MAIN_TABLE_ALIAS;
				Join join=null;
				for (String id : foreignKeyIds) {
					EntityFieldExt foreignKeyField=table1.getExistedField(id);
					ForeignKeySetting foreignKey=foreignKeyField.getForeignKey();
					if(foreignKey==null) {
						throw new IllegalArgumentException("ForeignKey not found in "+
								table1.tableName+"."+foreignKeyField.getName());
					}
					EntityInfoExt table2=allEntity.get(foreignKey.tableName);
					if(table2==null) {
						throw new IllegalArgumentException("ForeignKey table not found in "+
								table1.tableName+"."+foreignKeyField.getName()+",tableName:"+foreignKey.tableName);
					}
					join=addJoin(JoinType.LEFT_JOIN,table1Alias,table1.tableName, table2.tableName,
							new String[] {id},
							new String[]{foreignKey.field});
					table1=table2;
					table1Alias=join.getTable2Alias();
				}
				fieldInfo.setTableAlias(join.getTable2Alias());
			}else {
				fieldInfo.setTableAlias(SqlProvider.MAIN_TABLE_ALIAS);//
			}
		}
	}
	//
	private EntityFieldExt getExistedField(String id) {
		EntityFieldExt ext=fieldMap.get(id);
		if(ext==null) {
			throw new IllegalArgumentException("field not found "+id);
		}
		return ext;
	}
	//
	protected Join addJoin(JoinType type,String table1Alias,String table1,String table2,
			String[] table1Fields, String[] table2Fields) {
		return joins.addJoin(type, null, null, table1, table2, 
				table1Alias, null, table1Fields, table2Fields);
	}
	//
	public EntityFieldExt getField(String fieldName) {
		return fieldMap.get(fieldName);
	}
	//
	public String info() {
		StringBuilder info=new StringBuilder();
		info.append("\nEntityInfo[\n").append(tableName);
		for (EntityFieldExt field : fieldList) {
			info.append(field.info());
		}
		info.append(joins.info());
		info.append("\n]");
		return info.toString();
	}
	//
	/**
	 * @return the joins
	 */
	public Joins getJoins() {
		return joins;
	}
	/**
	 * @param joins the joins to set
	 */
	public void setJoins(Joins joins) {
		this.joins = joins;
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
	/**
	 * @return the fieldList
	 */
	public List<EntityFieldExt> getFieldList() {
		return fieldList;
	}
	/**
	 * @param fieldList the fieldList to set
	 */
	public void setFieldList(List<EntityFieldExt> fieldList) {
		this.fieldList = fieldList;
	}
	/**
	 * @return the fieldMap
	 */
	public Map<String, EntityFieldExt> getFieldMap() {
		return fieldMap;
	}
	/**
	 * @param fieldMap the fieldMap to set
	 */
	public void setFieldMap(Map<String, EntityFieldExt> fieldMap) {
		this.fieldMap = fieldMap;
	}
	/**
	 * @return the tableComment
	 */
	public String getTableComment() {
		return tableComment;
	}
	/**
	 * @param tableComment the tableComment to set
	 */
	public void setTableComment(String tableComment) {
		this.tableComment = tableComment;
	}
	
}
