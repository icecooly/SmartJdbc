package io.itit.smartjdbc.provider.entity;

/**
 * 
 * @author skydu
 *
 */
public abstract class EntityField {
	//
	public abstract String expr();
	
	//
	public static class EntityFieldImpl extends EntityField{
		
		public String alias;
		
		public String colName;
		
		public String asName;
		
		public String remark;
		
		public EntityJoin entityJoin;
		
		public EntityFieldImpl(String alias, String colName,String remark) {
			this(alias, colName, null, remark);
		}
		
		public EntityFieldImpl(String alias, String colName, String asName, String remark) {
			this.alias=alias;
			this.colName=colName;
			this.asName=asName;
			this.remark=remark;
		}
		
		@Override
		public String expr() {
			StringBuilder expr=new StringBuilder();
			if(alias!=null) {
				expr.append(alias).append(".");
			}
			if(colName!=null) {
				expr.append("`").append(colName).append("` ");
			}
			if(asName!=null) {
				expr.append("as ").append(asName).append(" ");
			}
			return expr.toString();
		}
		
	}
}
