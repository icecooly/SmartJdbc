package test;

import io.itit.smartjdbc.domain.EntityFieldExt;
import io.itit.smartjdbc.domain.EntityInfoExt;

public class SelectBuilderTestCase {
	
	//
	private static final String MAIN_TABLE="t_test";
	
	public EntityInfoExt createEntityInfoExt() {
		EntityInfoExt info=new EntityInfoExt();
		info.setTableName("t_task");
		info.setTableComment("任务");
		
		EntityFieldExt idfield=new EntityFieldExt();
		idfield.setUniqId("id");
		idfield.getEntityField().column="id";
		idfield.getEntityField().comment="id";
		idfield.getEntityField().persistent=true;
		info.getFieldList().add(idfield);
	
		EntityFieldExt namefield=new EntityFieldExt();
		namefield.setUniqId("name");
		namefield.getEntityField().column="name";
		namefield.getEntityField().comment="名称";
		namefield.getEntityField().persistent=true;
		info.getFieldList().add(namefield);
		//
		return info;
	}
	//
	//
	public static void main(String[] args) {
		SelectBuilderTestCase testCase=new SelectBuilderTestCase();	
	}
}
