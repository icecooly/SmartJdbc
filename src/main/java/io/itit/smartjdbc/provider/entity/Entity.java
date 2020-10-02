package io.itit.smartjdbc.provider.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author skydu
 *
 */
public class Entity {
	
	public String tableName;
	
	public String remark;
	
	public String alias;

	public List<EntityField> fields;
	
	public List<EntityJoin> joins;
	//
	public Entity(String tableName, String remark) {
		this(tableName, "a", remark);
	}
	//
	public Entity(String tableName, String alias, String remark) {
		this.tableName=tableName;
		this.alias=alias;
		this.remark=remark;
		this.fields=new ArrayList<>();
		this.joins=new ArrayList<>();
	}
}
