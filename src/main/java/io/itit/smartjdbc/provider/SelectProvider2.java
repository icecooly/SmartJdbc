//package io.itit.smartjdbc.provider;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import io.itit.smartjdbc.SmartDataSource;
//import io.itit.smartjdbc.enums.AggregationFunction;
//import io.itit.smartjdbc.enums.JoinType;
//import io.itit.smartjdbc.enums.SqlOperator;
//import io.itit.smartjdbc.provider.SelectProvider.Aggregation;
//import io.itit.smartjdbc.provider.SelectProvider.GroupByField;
//import io.itit.smartjdbc.provider.entity.Entity;
//import io.itit.smartjdbc.provider.entity.EntityField;
//import io.itit.smartjdbc.provider.entity.EntityJoin;
//import io.itit.smartjdbc.provider.entity.Join;
//import io.itit.smartjdbc.provider.entity.Join.JoinOn;
//import io.itit.smartjdbc.provider.entity.SelectSql;
//import io.itit.smartjdbc.provider.entity.SqlBean;
//import io.itit.smartjdbc.provider.where.QueryWhere;
//import io.itit.smartjdbc.util.StringUtil;
//
///**
// * 
// * @author skydu
// *
// */
//public class SelectProvider2 extends SqlProvider{
//	//
//	//
//	public static final String MAIN_TABLE_ALIAS="a";
//	protected Entity entity;
//	protected QueryWhere queryWhere;
//	protected List<GroupByField> groupBys;
//	protected List<Aggregation> aggregationList;
//	protected boolean isSelectCount;
//	protected boolean needPaging;
//	protected boolean needOrderBy;
//	protected boolean isForUpdate;
//	//
//	public Map<String,Object> params;
//	//
//	public SelectProvider2(SmartDataSource smartDataSource) {
//		super(smartDataSource);
//		this.groupBys=new ArrayList<>();
//		this.aggregationList=new ArrayList<>();
//		this.needOrderBy=true;
//		this.params=new HashMap<>();
//	}
//	//
//	public SelectProvider2 setEntity(Entity entity) {
//		this.entity=entity;
//		return this;
//	}
//	//
//	public SelectProvider2 queryWhere(QueryWhere queryWhere) {
//		this.queryWhere=queryWhere;
//		return this;
//	}
//	//
//	public SelectProvider2 selectCount() {
//		this.isSelectCount=true;
//		return this;
//	}
//	//
//	public SelectProvider2 needPaging(boolean needPaging) {
//		this.needPaging=needPaging;
//		return this;
//	}
//	//
//	public SelectProvider2 needOrderBy(boolean needOrderBy) {
//		this.needOrderBy=needOrderBy;
//		return this;
//	}
//	//
//	public SelectProvider2 forUpdate(){
//		this.isForUpdate=true;
//		return this;
//	}
//	//
//	protected SqlBean queryCount() {
//		StringBuilder sql = new StringBuilder();
//		sql.append("\nselect count(1) ");
//		this.needPaging=false;
//		return build(sql);
//	}
//	
//	protected SqlBean query() {
//		StringBuilder sql = new StringBuilder();
//		sql.append("\nselect ");
//		getSelectSql(sql, entity);
//		sql.deleteCharAt(sql.length()-1);
//		return build(sql);
//	}
//	//
//	protected void addOrderBy() {
////		List<String> orderByList=addOrderByList();
////		for (String e : orderByList) {
////			orderBy(e);
////		}
//	}
//	//
//	//
//	protected void addPaging() {
////		this.limit(query.getStartPageIndex(),query.pageSize);
//	}
//	//
//	protected void getSelectSql(StringBuilder sql, Entity entity) {
//		for (EntityField field : entity.fields) {
//			sql.append((field.expr()));
//			sql.append(",");
//		}
//		//
//		for (EntityJoin join : entity.joins) {
//			getSelectSql(sql, join.entity);
//		}
//		addAggregation(sql);
//	}
//	//
//	protected void addAggregation(StringBuilder sql) {
//		if(aggregationList==null||aggregationList.size()==0) {
//			return;
//		}
//		this.needPaging=false;
//		for (Aggregation aggregation : aggregationList) {
//			sql.append(",");
//			if(aggregation.func.equalsIgnoreCase(AggregationFunction.COUNT.name())) {
//				sql.append(" count(1) ");
//			}
//			if(aggregation.func.equalsIgnoreCase(AggregationFunction.AVG.name())) {
//				sql.append(" avg("+getExpr(aggregation)+") ");
//			}
//			if(aggregation.func.equalsIgnoreCase(AggregationFunction.MAX.name())) {
//				sql.append(" max("+getExpr(aggregation)+") ");
//			}
//			if(aggregation.func.equalsIgnoreCase(AggregationFunction.MIN.name())) {
//				sql.append(" min("+getExpr(aggregation)+") ");
//			}
//			if(aggregation.func.equalsIgnoreCase(AggregationFunction.SUM.name())) {
//				sql.append(" sum("+getExpr(aggregation)+") ");
//			}
//			if(aggregation.asName!=null) {
//				sql.append(" as "+aggregation.asName).append(" ");
//			}
//		}
//	}
//	//
//	private String getExpr(Aggregation aggregation) {
//		StringBuilder sql=new StringBuilder();
//		if(aggregation.distinct) {
//			sql.append("distinct( ");
//		}
//		sql.append(aggregation.expr).append(" ");
//		if(aggregation.distinct) {
//			sql.append(") ");
//		}
//		return sql.toString();
//	}
//	//
//	protected String getFromSql() {
//		StringBuilder sql=new StringBuilder();
//		sql.append("\nfrom ").append(entity.tableName).append(" ").append(entity.alias).append(" ");
//		return sql.toString();
//	}
//	//
//	protected String getJoinSql(Entity entity) {
//		StringBuilder sql=new StringBuilder();
//		for(EntityJoin entityJoin: entity.joins) {
//			Join join=entityJoin.join;
//			Entity joinEntity=entityJoin.entity;
//			if(join.type.equals(JoinType.INNER)) {
//				sql.append("\ninner join ");
//			}
//			if(join.type.equals(JoinType.LEFT)) {
//				sql.append("\nleft join ");	
//			}
//			if(join.type.equals(JoinType.RIGHT)) {
//				sql.append("\nright join ");	
//			}
//			sql.append("`").append(joinEntity.tableName).append("` ").append(joinEntity.alias).append(" ");
//			if(join.onList!=null&&join.onList.size()>0) {
//				sql.append("on ");
//				for (JoinOn on : join.onList) {
//					sql.append(entity.alias).append(".`").append(on.table1Field).append("`");
//					if(on.opt.equals(SqlOperator.EQ)) {
//						sql.append("=");
//					}
//					sql.append(joinEntity.alias).append(".`").append(on.table2Field).append("` ");
//				}
//			}
//		}
//		return sql.toString();
//	}
//	//
//	protected String getWhereSql() {
//		StringBuilder sql=new StringBuilder();
//		sql.append("\nwhere 1=1 ");
//		sql.append(queryWhere.whereStatement(this,true).sql);
//		sql.append("\n");
//		return sql.toString();
//	}
//	//
//	protected String getGroupBySql() {
//		StringBuilder sql=new StringBuilder();
//		if(groupBys.size()>0) {
//			sql.append("group by ");
//			for (GroupByField field : groupBys) {
//				if(!StringUtil.isEmpty(field.tableAlias)) {
//					sql.append(field.tableAlias).append(".");
//				}
//				sql.append(convertFieldName(field.field)).append(",");
//			}
//			sql.deleteCharAt(sql.length()-1);
//			sql.append("\n");
//		}
//		return sql.toString();
//	}
//	//
//	protected String getOrderBySql() {
//		if(isSelectCount) {
//			return "";
//		}
//		if(!needOrderBy) {
//			return "";
//		}
//		StringBuilder sql=new StringBuilder();
//		if (queryWhere.getOrderBys().size()>0) {
//			sql.append("order by ");
//			for (String orderBy : queryWhere.getOrderBys()) {
//				sql.append(orderBy).append(",");
//			}
//			sql.deleteCharAt(sql.length()-1);
//			sql.append("\n");
//		}
//		return sql.toString();
//	}
//	//
//	protected String getLimitSql() {
//		if(isSelectCount) {
//			return "";
//		}
//		if(!needPaging) {
//			return "";
//		}
//		StringBuilder sql=new StringBuilder();
//		if(queryWhere.getLimitEnd()!=-1) {
//			sql.append("limit ").
//			append(queryWhere.getLimitStart()).append(",").
//			append(queryWhere.getLimitEnd()).append("\n");
//		}
//		return sql.toString();
//	}
//	//
//	protected String getForUpdateSql() {
//		if(isForUpdate) {
//			return "for update \n";
//		}
//		return "";
//	}
//	//
//	//
//	protected SqlBean build(StringBuilder selectSql) {
//		SelectSql bean=new SelectSql();
//		bean.selectSql=selectSql.toString();
//		bean.fromSql=getFromSql();
//		bean.joinSql=getJoinSql(entity);
//		bean.whereSql=getWhereSql();
//		bean.groupBySql=getGroupBySql();
//		bean.orderBySql=getOrderBySql();
//		bean.limitSql=getLimitSql();
//		bean.forUpdateSql=getForUpdateSql();
//		return SqlBean.build(bean.toSql(), queryWhere.whereValues(this));
//	}
//	@Override
//	public SqlBean build() {
//		if(isSelectCount) {
//			return queryCount();
//		}
//		return query();
//	}
//}
