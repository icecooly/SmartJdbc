package io.itit.smartjdbc.provider.entity;

import java.util.ArrayList;
import java.util.List;

import io.itit.smartjdbc.enums.JoinType;
import io.itit.smartjdbc.enums.SqlOperator;

/**
 * 
 * @author skydu
 *
 */
public class Join {
	
	public JoinType type;

	public String alias;
	
	public List<JoinOn> onList;
	//
	public Join(String alias, JoinType type) {
		this.alias=alias;
		this.type=type;
		onList=new ArrayList<>();
	}
	//
	public static class JoinOn{
		public String table1Field;
		public String table1FieldName;
		public SqlOperator opt;
		public String table2Field;
		public String table2FieldName;
		//
		public JoinOn(String table1Field, String table2Field, String table1FieldName, String table2FieldName) {
			this(table1Field, SqlOperator.EQ, table2Field, table1FieldName, table2FieldName);
		}
		public JoinOn(String table1Field, SqlOperator opt, String table2Field, 
				String table1FieldName, String table2FieldName) {
			this.table1Field=table1Field;
			this.opt=opt;
			this.table2Field=table2Field;
			this.table1FieldName=table1FieldName;
			this.table2FieldName=table2FieldName;
		}
	}
}
