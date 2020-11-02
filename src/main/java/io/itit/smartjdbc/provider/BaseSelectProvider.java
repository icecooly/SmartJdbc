package io.itit.smartjdbc.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.Query.OrderBy;
import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.enums.AggregationFunction;
import io.itit.smartjdbc.enums.ConditionType;
import io.itit.smartjdbc.enums.SqlOperator;
import io.itit.smartjdbc.provider.entity.Aggregation;
import io.itit.smartjdbc.provider.entity.EntitySelect;
import io.itit.smartjdbc.provider.entity.EntitySelect.EntitySelectField;
import io.itit.smartjdbc.provider.entity.EntitySelect.Join;
import io.itit.smartjdbc.provider.entity.QueryFieldInfo;
import io.itit.smartjdbc.provider.entity.QueryInfo;
import io.itit.smartjdbc.provider.entity.SelectSql;
import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.QueryWhere;
import io.itit.smartjdbc.provider.where.QueryWhere.WhereStatment;
import io.itit.smartjdbc.provider.where.Where;
import io.itit.smartjdbc.util.ArrayUtils;
import io.itit.smartjdbc.util.StringUtil;

/**
 * 
 * @author skydu
 *
 */
public class BaseSelectProvider extends SqlProvider{

	private static Logger logger=LoggerFactory.getLogger(BaseSelectProvider.class);
	//
	protected EntitySelect entity;
	protected QueryInfo query;
	protected boolean isSelectCount;
	protected boolean needPaging;
	protected boolean needOrderBy;
	protected QueryWhere qw;
	protected List<EntitySelectField> groupBys;
	protected List<Aggregation> aggregationList;
	//
	public BaseSelectProvider(SmartDataSource smartDataSource) {
		super(smartDataSource);
		this.qw=QueryWhere.create();
		this.groupBys=new ArrayList<>();
		this.aggregationList=new ArrayList<>();
		this.needOrderBy=true;
	}
	//
	public BaseSelectProvider setEntity(EntitySelect entity) {
		this.entity=entity;
		this.query=entity.queryInfo;
		this.aggregationList=entity.aggregationList;
		this.groupBys=entity.groupBys;
		return this;
	}
	//
	public BaseSelectProvider selectWhere(QueryWhere sw) {
		this.qw=sw;
		return this;
	}
	//
	public BaseSelectProvider selectCount() {
		this.isSelectCount=true;
		return this;
	}
	//
	public BaseSelectProvider needPaging(boolean needPaging) {
		this.needPaging=needPaging;
		return this;
	}
	//
	public BaseSelectProvider needOrderBy(boolean needOrderBy) {
		this.needOrderBy=needOrderBy;
		return this;
	}
	//
	public BaseSelectProvider orderBy(String orderBy){
		qw.orderBy(orderBy);
		return this;
	}
	//
	public BaseSelectProvider limit(int start,int limit){
		qw.limit(start, limit);
		return this;
	}
	//
	public BaseSelectProvider limit(int end){
		qw.limit(end);
		return this;
	}
	//
	public BaseSelectProvider forUpdate(){
		qw.forUpdate();
		return this;
	}
	//聚合函数
	protected void addAggregation(StringBuilder sql) {
		this.needPaging=false;
		if(groupBys.size()==0) {//如果没有分组则不能排序
			this.needOrderBy=false;
		}
		//
		for (EntitySelectField field : groupBys) {
			sql.append(getSelectFieldSql(field));
			sql.append(",");
		}
		//
		for (Aggregation aggregation : aggregationList) {
			sql.append("");
			String func=aggregation.func.trim();
			if(func.equalsIgnoreCase(AggregationFunction.COUNT.name())) {
				sql.append(" count(1) ");
			}
			if(func.equalsIgnoreCase(AggregationFunction.AVG.name())) {
				sql.append(" avg("+getExpr(aggregation)+") ");
			}
			if(func.equalsIgnoreCase(AggregationFunction.MAX.name())) {
				sql.append(" max("+getExpr(aggregation)+") ");
			}
			if(func.equalsIgnoreCase(AggregationFunction.MIN.name())) {
				sql.append(" min("+getExpr(aggregation)+") ");
			}
			if(func.equalsIgnoreCase(AggregationFunction.SUM.name())) {
				sql.append(" sum("+getExpr(aggregation)+") ");
			}
			if(aggregation.asName!=null) {
				sql.append(" as "+addIdentifier(aggregation.asName)).append(" ");
			}
			sql.append(",");
		}
		sql.deleteCharAt(sql.length()-1);
	}
	//
	private String getExpr(Aggregation aggregation) {
		StringBuilder sql=new StringBuilder();
		if(aggregation.distinct) {
			sql.append("distinct( ");
		}
		sql.append(aggregation.expr).append(" ");
		if(aggregation.distinct) {
			sql.append(") ");
		}
		return sql.toString();
	}
	
	//
	protected void addOrderBy() {
		List<String> orderByList=addOrderByList();
		for (String e : orderByList) {
			orderBy(e);
		}
	}
	//
	public List<String> addOrderByList() {
		List<String> orderByList=new ArrayList<>();
		if(query==null) {
			return orderByList;
		}
		if(query.orderByList==null||query.orderByList.isEmpty()) {
			return orderByList;
		}
		for (OrderBy orderBy : query.orderByList) {
			if(orderBy==null||orderBy.field==null||orderBy.type==null) {
				continue;
			}
			String fieldId=orderBy.field;
			String[] tmp=fieldId.split("\\.");
			String alias=null;
			if(tmp.length==2) {
				alias=tmp[0];
				fieldId=tmp[1];
			}
			//如果有了group by 则orderby里的字段必须是groupby里的
			if(groupBys.size()>0) {
				if(!ArrayUtils.contains(groupBys, "name", fieldId)) {
					continue;
				}
			}
			String type=null;
			if(orderBy.type.trim().equalsIgnoreCase("ASC")) {
				type="asc";
			}else if(orderBy.type.trim().equalsIgnoreCase("DESC")) {
				type="desc";
			}else {
				continue;
			}
			StringBuilder sb=new StringBuilder();
			if(!StringUtil.isEmpty(alias)) {
				sb.append(alias).append(".");
			}
			sb.append(addIdentifier(fieldId));
			sb.append(" ");
			sb.append(type);
			orderByList.add(sb.toString());
		}
		return orderByList;
	}
	//
	protected void addPaging() {
		if(query==null) {
			return;
		}
		this.limit(query.getStartPageIndex(),query.pageSize);
	}
	//
	//
	protected void addSelectFields(StringBuilder sql) {
		for (EntitySelectField field : entity.selectFields) {
			sql.append(getSelectFieldSql(field));
			sql.append(",");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(" ");
	}
	//
	private String getSelectFieldSql(EntitySelectField field) {
		StringBuilder sql=new StringBuilder();
		if(StringUtil.isEmpty(field.formula)) {
			sql.append(field.alias).append(".");
			sql.append(addIdentifier(field.column)).append("");
		}else {
			sql.append(field.formula);
		}
		if(field.asName!=null) {
			sql.append(" as ").append(addIdentifier(field.asName));
		}
		return sql.toString();
	}
	//
	protected String getFromSql() {
		StringBuilder sql=new StringBuilder();
		sql.append("\nfrom ").append(addIdentifier(entity.tableName)).append(" ").append(entity.alias).append(" ");
		//inner join
		for (Join join : entity.innerJoins.joinList) {
			sql.append("\ninner join  ");
			addJoin(sql, join);
		}
		//left join
		if(entity!=null&&entity.leftJoins!=null) {
			for (Join join : entity.leftJoins.joinList) {
				sql.append("\nleft join  ");
				addJoin(sql, join);
			}
		}
		return sql.toString();
	}
	//
	protected void addJoin(StringBuilder sql,Join join) {
		sql.append(addIdentifier(join.table2)).append(" ").append(join.table2Alias);
		sql.append(" on ");
		for(int i=0;i<join.table1Fields.length;i++) {
			sql.append(join.table1Alias).append("."+(addIdentifier(join.table1Fields[i]))+"=").
			append(join.table2Alias).append(".").append((addIdentifier(join.table2Fields[i]))).append("");
			if(i<join.table1Fields.length-1) {
				sql.append(" and ");
			}
		}
		sql.append(" ");
	}
	//
	protected WhereStatment getWhereSql() {
		addWheres();
		WhereStatment ws=qw.whereStatement(getDatabaseType());
		return ws;
	}
	//
	protected void addWheres() {
		if(query==null) {
			return;
		}
		Map<String,Object> paraMap=new HashMap<>();
		if(!query.params.isEmpty()) {
			paraMap.putAll(query.params);
		}
		createParaMap(paraMap, query);
		addWheres(qw.getWhere(),paraMap,query);
	}
	//
	private void createParaMap(Map<String,Object> paraMap,QueryInfo queryInfo) {
		try {
			List<QueryFieldInfo> fields=queryInfo.fieldList;
			for (QueryFieldInfo info : fields) {
				try {
					Object value=info.value;
					paraMap.put(info.column, value);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					throw new SmartJdbcException(e.getMessage());
				}
			}
			for (QueryInfo child : queryInfo.children) {
				createParaMap(paraMap, child);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	//
	protected void addWheres(Where parent,Map<String,Object> paraMap,QueryInfo query) {
		try {
			for (QueryFieldInfo field : query.fieldList) {
				Object value=field.value;
				String alias = field.tableAlias;
				String dbFieldName=field.column;
				SqlOperator operator=field.operator;
				parent.where(alias, dbFieldName, operator, value, field.isColumn, field.jsonContain, field.columnCast);
			}
			//
			for (QueryInfo child : query.children) {
				if(child==null) {
					continue;
				}
				Where childWhere=new Where(child.conditionType);
				if(child.conditionType.equals(ConditionType.AND)) {
					parent.and(childWhere);
				}
				if(child.conditionType.equals(ConditionType.OR)) {
					parent.or(childWhere);
				}
				addWheres(childWhere, paraMap, child);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	//
	public static List<String> matchs(String data, String regex) {
		List<String> result = new ArrayList<>();
		if (data == null || regex == null) {
			return result;
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(data);
		while (matcher.find()) {
			result.add(matcher.group(1));
		}
		return result;
	}
	//
	protected String getGroupBySql() {
		StringBuilder sql=new StringBuilder();
		if(groupBys.size()>0) {
			sql.append("\ngroup by ");
			for (EntitySelectField field : groupBys) {
				if(!StringUtil.isEmpty(field.alias)) {
					sql.append(field.alias).append(".");
				}
				sql.append((addIdentifier(field.column))).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(" ");
		}
		return sql.toString();
	}
	//
	protected String getOrderBySql() {
		if(isSelectCount) {
			return "";
		}
		StringBuilder sql=new StringBuilder();
		if(needOrderBy) {
			addOrderBy();
			if (qw.getOrderBys().size()>0) {
				sql.append("\norder by ");
				for (String orderBy : qw.getOrderBys()) {
					sql.append(orderBy).append(",");
				}
				sql.deleteCharAt(sql.length()-1);
				sql.append(" ");
			}
		}
		return sql.toString();
	}
	//
	protected String getLimitSql() {
		if(isSelectCount) {
			return "";
		}
		if(!needPaging) {
			return "";
		}
		StringBuilder sql=new StringBuilder();
		addPaging();	
		if(qw.getLimitEnd()!=-1) {
			sql.append("\nlimit ").append(qw.getLimitStart()).append(",").append(qw.getLimitEnd()).append(" ");
		}
		return sql.toString();
	}
	//
	protected String getForUpdateSql() {
		if(qw.isForUpdate()) {
			return "\nfor update ";
		}
		return "";
	}
	//
	protected SqlBean query() {
		StringBuilder sql = new StringBuilder();
		sql.append("\nselect ");
		if(aggregationList.size()==0&&groupBys.size()==0) {
			addSelectFields(sql);
		}else {
			addAggregation(sql);
		}
		return build(sql);
	}
	//
	protected SqlBean queryCount() {
		StringBuilder sql = new StringBuilder();
		sql.append("\nselect count(1) ");
		this.needPaging=false;
		return build(sql);
	}
	//
	protected SelectSql build(StringBuilder selectSql) {
		SelectSql bean=new SelectSql();
		bean.selectSql=selectSql.toString();
		bean.fromSql=getFromSql();
		WhereStatment ws=getWhereSql();
		bean.whereSql=ws.sql;
		bean.groupBySql=getGroupBySql();
		bean.orderBySql=getOrderBySql();
		bean.limitSql=getLimitSql();
		bean.forUpdateSql=getForUpdateSql();
		bean.sql=bean.toSql();
		bean.parameters=ws.values;
		return bean;
	}
	@Override
	public SqlBean build() {
		if(isSelectCount) {
			return queryCount();
		}
		return query();
	}
}
