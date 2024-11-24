package io.itit.smartjdbc.provider;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.Query.OrderBy;
import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.Types;
import io.itit.smartjdbc.annotations.EntityField;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.cache.CacheManager;
import io.itit.smartjdbc.domain.EntityFieldInfo;
import io.itit.smartjdbc.domain.EntityInfo;
import io.itit.smartjdbc.domain.QueryFieldInfo;
import io.itit.smartjdbc.domain.QueryInfo;
import io.itit.smartjdbc.domain.SmartJdbcCondition;
import io.itit.smartjdbc.domain.SmartJdbcFilter;
import io.itit.smartjdbc.enums.AggregationFunction;
import io.itit.smartjdbc.enums.ColumnType;
import io.itit.smartjdbc.enums.ConditionType;
import io.itit.smartjdbc.enums.JoinType;
import io.itit.smartjdbc.enums.OrderByType;
import io.itit.smartjdbc.enums.SqlOperator;
import io.itit.smartjdbc.provider.entity.Aggregation;
import io.itit.smartjdbc.provider.entity.Join;
import io.itit.smartjdbc.provider.entity.Joins;
import io.itit.smartjdbc.provider.entity.SelectSql;
import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.QueryWhere;
import io.itit.smartjdbc.provider.where.QueryWhere.WhereStatment;
import io.itit.smartjdbc.provider.where.Where;
import io.itit.smartjdbc.provider.where.WhereSqlBuilder;
import io.itit.smartjdbc.util.ArrayUtils;
import io.itit.smartjdbc.util.ClassUtils;
import io.itit.smartjdbc.util.SmartJdbcUtils;

/**
 * 
 * @author skydu
 *
 */
public class SelectProvider extends SqlProvider{
	//
	protected EntityInfo entity;
	protected String tableName;
	protected QueryInfo queryInfo;
	protected Class<?> entityClass;
	protected Query<?> query;
	protected boolean isSelectCount;
	protected boolean needPaging;
	protected boolean needOrderBy;
	protected List<EntityFieldInfo> selectFields;
	protected Set<String> includeFields;
	protected Set<String> excludeFields;//userName not user_name
	protected QueryWhere qw;
	protected List<EntityFieldInfo> groupBys;
	protected List<Aggregation> aggregationList;
	protected Set<Join> joinList;
	//
	public SelectProvider(SmartDataSource smartDataSource) {
		super(smartDataSource);
		this.selectFields=new ArrayList<>();
		this.includeFields=new LinkedHashSet<>();
		this.excludeFields=new LinkedHashSet<>();
		this.qw=QueryWhere.create();
		this.groupBys=new ArrayList<>();
		this.aggregationList=new ArrayList<>();
		this.needOrderBy=true;
	}
	//
	public SelectProvider entityClass(Class<?> entityClass) {
		this.entityClass=entityClass;
		return this;
	}
	//
	public SelectProvider selectCount() {
		this.isSelectCount=true;
		return this;
	}
	//
	public SelectProvider needPaging(boolean needPaging) {
		this.needPaging=needPaging;
		return this;
	}
	//
	public SelectProvider needOrderBy(boolean needOrderBy) {
		this.needOrderBy=needOrderBy;
		return this;
	}
	//
	public SelectProvider query(Query<?> query) {
		this.query=query;
		return this;
	}
	//
	public SelectProvider query(QueryWhere qw) {
		this.qw=qw;
		if(qw.getLimitEnd()>0) {
			needPaging(true);
		}
		return this;
	}
	//
	public SelectProvider tableName(String tableName) {
		this.tableName=tableName;
		return this;
	}
	//
	public SelectProvider select(String field) {
		return select(null, field);
	}
	//
	public SelectProvider select(String tableAlias,String name) {
		return select(tableAlias,name,null);
	}
	//
	public SelectProvider select(String tableAlias,String name, String asAlias) {
		return select(tableAlias, name,asAlias, false, null);
	}
	//
	public SelectProvider select(String tableAlias,String name,String asName,boolean distinct,String statFunction) {
		selectFields.add(createSelectField(tableAlias, name, 
				asName, distinct, statFunction));
		return this;
	}
	//
	public SelectProvider includeFields(Set<String> fields){
		if(fields!=null) {
			for (String field : fields) {
				includeFields.add(field);
			}
		}
		return this;
	}
	//
	public SelectProvider excludeFields(String ... fields){
		if(fields!=null) {
			for (String field : fields) {
				excludeFields.add(field);
			}
		}
		return this;
	}
	//
	public SelectProvider excludeFields(Set<String> fields){
		if(fields!=null) {
			for (String field : fields) {
				excludeFields.add(field);
			}
		}
		return this;
	}
	//
	protected EntityFieldInfo createSelectField(String tableAlias,String name,String asName,boolean distinct,String statFunction) {
		EntityFieldInfo sf=new EntityFieldInfo();
		sf.setTableAlias(tableAlias);
		sf.setName(name);
		sf.setAsName(asName);
		sf.setDistinct(distinct);
		sf.setStatFunction(statFunction);
		return sf;
	}
	//
	public SelectProvider where(String alias,String key,Object value){
		return this.where(alias,key, SqlOperator.EQ, value);
	}
	//
	public SelectProvider where(String key,Object value){
		return this.where(MAIN_TABLE_ALIAS,key, SqlOperator.EQ, value);
	}
	//
	public SelectProvider where(String alias,String key,SqlOperator op,Object value){
		qw.where(alias, key, op, value);
		return this;
	}
	//
	public SelectProvider whereSql(String sql,Map<String,Object> values){
		qw.whereSql(sql, values);
		return this;
	}
	//
	public SelectProvider groupBy(String field) {
		groupBy(MAIN_TABLE_ALIAS, field);
		return this;
	}
	//
	public SelectProvider groupBy(String tableAlias,String field) {
		groupBys.add(createGroupByField(tableAlias, field));
		return this;
	}
	//
	public SelectProvider groupBy(List<EntityFieldInfo> groupBys) {
		this.groupBys=groupBys;
		return this;
	}
	//
	public SelectProvider aggregationList(List<Aggregation> aggregationList) {
		this.aggregationList=aggregationList;
		return this;
	}
	//
	//
	protected EntityFieldInfo createGroupByField(String tableAlias,String name) {
		EntityFieldInfo groupByField=new EntityFieldInfo();
		groupByField.setTableAlias(tableAlias);
		groupByField.setName(name);
		return groupByField;
	}
	//
	public SelectProvider orderBy(String orderBy){
		qw.orderBy(orderBy);
		return this;
	}
	//
	public SelectProvider limit(int start,int limit){
		qw.limit(start, limit);
		return this;
	}
	//
	public SelectProvider limit(int end){
		qw.limit(end);
		return this;
	}
	//
	public SelectProvider forUpdate(){
		qw.setForUpdate(true);
		return this;
	}
	//
	public SelectProvider of(String of){
		qw.setOf(of);
		return this;
	}
	//
	//聚合函数
	protected void addAggregation(StringBuilder sql) {
		this.needPaging=false;
		if(groupBys.size()==0) {//如果没有分组则不能排序
			this.needOrderBy=false;
		}
		//
		for (EntityFieldInfo field : groupBys) {
			sql.append(getSelectFieldSql(field));
			sql.append(",");
		}
		//
		for (Aggregation aggregation : aggregationList) {
			sql.append("");
			String func=aggregation.getFunc().trim();
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
			if(aggregation.getAsName()!=null) {
				sql.append(" as "+addIdentifier(aggregation.getAsName())).append(" ");
			}
			sql.append(",");
		}
		sql.deleteCharAt(sql.length()-1);
	}
	//
	private String getExpr(Aggregation aggregation) {
		StringBuilder sql=new StringBuilder();
		if(aggregation.isDistinct()) {
			sql.append("distinct( ");
			sql.append(aggregation.getExpr()).append(" ");
			sql.append(") ");
		}else {
			sql.append(aggregation.getExpr()).append(" ");
		}
		return sql.toString();
	}
	//
	@SuppressWarnings("deprecation")
	protected void getQueryFields(List<QueryFieldInfo> fieldList,Object obj,QueryInfo queryInfo){
		List<QueryFieldInfo> queryFieldList=queryInfo.getFieldList();
		for (QueryFieldInfo fieldInfo : queryFieldList) {
			try {
				Field field=fieldInfo.getField();
				if(!field.isAccessible()) {
					field.setAccessible(true);
				}
				Object reallyValue = field.get(obj);
				if (reallyValue == null) {
					continue;
				}
				fieldList.add(fieldInfo);
			}catch (Exception e) {
				throw new IllegalArgumentException(e.getMessage(),e);
			}
		}
		List<QueryInfo> children=queryInfo.getChildren();
		for (QueryInfo e : children) {
			try {
				Field field=e.getField();
				if(!field.isAccessible()) {
					field.setAccessible(true);
				}
				Object reallyValue = field.get(obj);
				if (reallyValue == null) {
					continue;
				}
				getQueryFields(fieldList,reallyValue,e);
			} catch (Exception ex) {
				throw new IllegalArgumentException(ex.getMessage(),ex);
			}	
		}
	}
	//
	protected void createJoins() {
		Joins joins=null;
		if(queryInfo!=null) {
			joins=queryInfo.getJoins();
		}else{
			joins=entity.getJoins();
		}
		//
		//只留下需要的join
		Set<String> aliasSet=new HashSet<>();
		for (EntityFieldInfo field : selectFields) {
			aliasSet.add(field.getTableAlias());
		}
		if(query!=null) {
			List<QueryFieldInfo> queryFields=new ArrayList<>();
			getQueryFields(queryFields,query,queryInfo);
			for (QueryFieldInfo field : queryFields) {
				aliasSet.add(field.getTableAlias());
			}
		}
		aliasSet.remove(SqlProvider.MAIN_TABLE_ALIAS);
		Set<Join> joinList=new LinkedHashSet<>();//去重
		for (String aliasName : aliasSet) {
			getUseJoins(joinList, joins, aliasName);
		}
		//
		this.joinList=joinList;
	}
	//
	private void getUseJoins(Set<Join> joinList, Joins joins, String table2Alias) {
		Join join=joins.getJoinMap().get(table2Alias);
		joinList.add(join);
		if(!join.getTable1Alias().equals(SqlProvider.MAIN_TABLE_ALIAS)) {
			getUseJoins(joinList, joins, join.getTable1Alias());
		}
	}
	//
	/**
	 * 
	 * @param query
	 */
	protected void addWheres(Query<?> query) {
		if(query==null) {
			return;
		}
		Map<String,Object> paraMap=new HashMap<>();
		if(!query.getParams().isEmpty()) {
			paraMap.putAll(query.getParams());
		}
		createParaMap(paraMap, query, queryInfo);
		addWheres(qw.getWhere(), paraMap, query, queryInfo);
		SmartJdbcFilter filter=query.getFilter();
		if(filter!=null) {
			SmartJdbcFilter rootfilter=new SmartJdbcFilter();
			if(filter.opt==null) {
				filter.opt=SmartJdbcFilter.OPT_AND;
			}
			rootfilter.children.add(filter);
			Map<String,EntityFieldInfo> fieldMap=entity.getFieldMap();
			Map<String,String> fieldColumnMap=new HashMap<>();//key userName value user_name
			fieldMap.forEach((k,v)->{
				fieldColumnMap.put(k, convertFieldNameToColumnName(entityClass,k));
			});
			addCondtions(fieldColumnMap, qw.getWhere(), rootfilter);
		}
	}
	//
	private void addCondtions(Map<String,String> fieldMap, Where where, SmartJdbcFilter filter) {
		where.conditionType=ConditionType.valueOf(filter.opt.toUpperCase());
		if(filter.conditionList!=null) {
			for (SmartJdbcCondition cond : filter.conditionList) {
				if(cond.fieldId==null||cond.opt==null) {
					continue;
				}
				String fieldColumn=fieldMap.get(cond.fieldId);
				if(fieldColumn==null) {
					throw new RuntimeException("过滤字段不存在"+cond.fieldId);
				}
				String opt=cond.opt;
				if(opt.equals(SmartJdbcCondition.OPT_等于)) {
					where.eq(fieldColumn, cond.value);
				}
				if(opt.equals(SmartJdbcCondition.OPT_不等于)) {
					where.ne(fieldColumn, cond.value);
				}
				if(opt.equals(SmartJdbcCondition.OPT_大于)) {
					where.gt(fieldColumn, cond.value);
				}
				if(opt.equals(SmartJdbcCondition.OPT_大于等于)) {
					where.ge(fieldColumn, cond.value);
				}
				if(opt.equals(SmartJdbcCondition.OPT_小于)) {
					where.lt(fieldColumn, cond.value);
				}
				if(opt.equals(SmartJdbcCondition.OPT_小于等于)) {
					where.le(fieldColumn, cond.value);
				}
				if(opt.equals(SmartJdbcCondition.OPT_包含)) {
					where.like(fieldColumn, cond.value);
				}
				if(opt.equals(SmartJdbcCondition.OPT_不包含)) {
					where.notLike(fieldColumn, cond.value);
				}
				if(opt.equals(SmartJdbcCondition.OPT_在列表中)) {
					where.in(fieldColumn, cond.value);
				}
				if(opt.equals(SmartJdbcCondition.OPT_不在列表中)) {
					where.notin(fieldColumn, cond.value);
				}
				if(opt.equals(SmartJdbcCondition.OPT_开始是)) {
					where.likeRight(fieldColumn, cond.value);
				}
				if(opt.equals(SmartJdbcCondition.OPT_结尾是)) {
					where.likeLeft(fieldColumn, cond.value);
				}
				if(opt.equals(SmartJdbcCondition.OPT_已设置)) {
					where.isNotNull(fieldColumn);
				}
				if(opt.equals(SmartJdbcCondition.OPT_未设置)) {
					where.isNull(fieldColumn);
				}
				if(opt.equals(SmartJdbcCondition.OPT_范围内)) {
					where.betweenAnd(fieldColumn, cond.value);
				}
				if(opt.equals(SmartJdbcCondition.OPT_不在范围内)) {
					where.notBetweenAnd(fieldColumn, cond.value);
				}
			}
		}
		if(filter.children!=null) {
			for (SmartJdbcFilter child : filter.children) {
				Where childWhere=new Where();
				where.addWhere(childWhere);
				addCondtions(fieldMap, childWhere, child);
			}
		}
	}
	//
	@SuppressWarnings("deprecation")
	private void createParaMap(Map<String,Object> paraMap,Object obj,QueryInfo queryInfo) {
		try {
			List<QueryFieldInfo> fields=queryInfo.getFieldList();
			for (QueryFieldInfo info : fields) {
				Field field=info.getField();
				try {
					if(!field.isAccessible()) {
						field.setAccessible(true);
					}
					Object value=field.get(obj);
					paraMap.put(info.getFieldName(), value);
				} catch (Exception e) {
					throw new SmartJdbcException(e.getMessage(),e);
				}
			}
			List<QueryInfo> children=queryInfo.getChildren();
			for (QueryInfo child : children) {
				Field field=child.getField();
				if(!field.isAccessible()) {
					field.setAccessible(true);
				}
				Object childObj=field.get(obj);
				if(childObj==null) {
					continue;
				}
				createParaMap(paraMap, childObj, child);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(),e);
		}
		
	}
	//
	@SuppressWarnings("deprecation")
	protected void addWheres(Where w,Map<String,Object> paraMap,Object obj,QueryInfo queryInfo) {
		try {
			List<QueryFieldInfo> fields=queryInfo.getFieldList();
			for (QueryFieldInfo fieldInfo : fields) {
				Field field=fieldInfo.getField();
				if(!field.isAccessible()) {
					field.setAccessible(true);
				}
				Object value=field.get(obj);
				if(value==null) {
					continue;
				}
				if(ArrayUtils.isArrayType(field)&&ArrayUtils.isArrayEmpty(value)) {
					continue;
				}
				if(field.getType().equals(String.class)&&SmartJdbcUtils.isEmpty(value.toString())) {//字符串 忽略空串
					continue;
				}
				QueryField queryField=field.getAnnotation(QueryField.class);
				String alias = fieldInfo.getTableAlias();
				//
				if(queryField!=null&&!SmartJdbcUtils.isEmpty(queryField.whereSql())) {//whereSql check first
					String whereSql=queryField.whereSql();
					w.whereSql(whereSql,paraMap);
				}else {
					String dbFieldName=null;
					if(queryField!=null&&(!SmartJdbcUtils.isEmpty(queryField.field()))) {
						dbFieldName=convertFieldNameToColumnName(fieldInfo.getEntityClass(),queryField.field());
					}else {
						dbFieldName=convertFieldNameToColumnName(fieldInfo.getEntityClass(),field.getName());
					}
					SqlOperator operator=SqlOperator.EQ;//default eq
					if(queryField!=null) {
						operator=queryField.operator();
					}
					w.where(alias,dbFieldName,operator,value);
				}
			}
			//
			List<QueryInfo> children=queryInfo.getChildren();
			for (QueryInfo child : children) {
				Field field=child.getField();
				if(!field.isAccessible()) {
					field.setAccessible(true);
				}
				Object childObj=field.get(obj);
				if(childObj==null) {
					continue;
				}
				Where wc=new Where();
				if(child.getConditionType().equals(ConditionType.AND)) {
					w.and(wc);
				}else {
					w.or(wc);
				}
				addWheres(wc, paraMap, childObj, child);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(),e);
		}
	}
	//
	protected int getSortFieldOrder(String[] sortFields,String fieldName) {
		if(sortFields==null||sortFields.length==0) {
			return 0;
		}
		int order=0;
		for (String field : sortFields) {
			order++;
			if(field.equals(fieldName)) {
				return order;
			}
		}
		return 0;
	}
	//
	protected void addOrderBy(Query<?> query) {
		List<String> orderByList=addOrderByList(query);
		for (String e : orderByList) {
			orderBy(e);
		}
	}
	//
	public List<String> addOrderByList(Query<?> query) {
		List<String> orderByList=new ArrayList<>();
		if(query==null) {
			return orderByList;
		}
		if(query.getOrderByList()==null||query.getOrderByList().isEmpty()) {
			return orderByList;
			
		}
		for (OrderBy order : query.getOrderByList()) {
			String fieldName=order.field;
			if(fieldName==null) {
				continue;
			}
			StringBuilder orderby=new StringBuilder();
			if(!SmartJdbcUtils.isEmpty(order.tableAlias)) {
				orderby.append(order.tableAlias).append(".");
			}
			EntityFieldInfo field=entity.getField(order.field);
			String dbField=fieldName;
			if(field!=null) {
				dbField=convertFieldNameToColumnName(entityClass,field.getName());
			}
			String orderBy=order.type;
			orderby.append(dbField);
			if(orderBy.equalsIgnoreCase(OrderByType.ASC.name())) {
				orderby.append(" asc ");
			}else if(orderBy.equalsIgnoreCase(OrderByType.DESC.name())) {
				orderby.append(" desc ");
			}
			orderByList.add(orderby.toString());
		}
		return orderByList;
	}
	//
	protected void addPaging() {
		if(query==null) {
			return;
		}
		this.limit(query.getStartPageIndex(),query.getPageSize());
	}
	//
	protected void buildSelectFields(){
		List<EntityFieldInfo> fieldList=entity.getFieldList();
		for (EntityFieldInfo fieldInfo : fieldList) {
			Field field=fieldInfo.getField();
			if(includeFields!=null&&!includeFields.isEmpty()&&(!includeFields.contains(field.getName()))){
				continue;
			}
			if(excludeFields.contains(field.getName())){
				continue;
			}	
			EntityField entityField = fieldInfo.getEntityField();
			if(entityField!=null&&entityField.ignoreWhenSelect()) {
				continue;
			}
			if(entityField==null) {
				selectFields.add(fieldInfo);
			}else if(Types.WRAP_TYPES.contains(field.getType())){//基本字段类型
				selectFields.add(fieldInfo);
			}else if(field.getGenericType() instanceof ParameterizedType){//List Set Map
				selectFields.add(fieldInfo);
			}else {//查询对象字段列表
				List<Field> subClassFields=ClassUtils.getPersistentFields((Class<?>)field.getGenericType());
				for (Field subClassField : subClassFields) {
					selectFields.add(createSelectField(fieldInfo.getTableAlias(), subClassField.getName(), 
							field.getName()+"$"+subClassField.getName(), false, null));
				}
			}
		}
	}
	//
	protected void addSelectFields(StringBuilder sql) {
		for (EntityFieldInfo field : selectFields) {
			sql.append(getSelectFieldSql(field));
			sql.append(",");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(" ");
	}
	//
	protected String getSelectFieldSql(EntityFieldInfo field) {
		StringBuilder sql=new StringBuilder();
		if(field.isDistinct()) {
			sql.append(" distinct ");
		}
		if(SmartJdbcUtils.isEmpty(field.getStatFunction())) {
			sql.append(field.getTableAlias()).append(".");
			sql.append(addIdentifier(convertFieldNameToColumnName(field.getEntityClass(),field.getName()))).append("");
			if(field.getEntityField()!=null&&
					field.getEntityField().columnType().equals(ColumnType.JSONB)
					) {
				sql.append("::text"); 
			}
		}else {
			sql.append(field.getStatFunction());
		}
		if(field.getAsName()!=null) {
			sql.append(" as ").append(addIdentifier(convertFieldNameToColumnName(entityClass,field.getAsName())));
		}
		return sql.toString();
	}
	//
	protected String getTableName(Class<?> clazz) {
		if(entityClass.equals(clazz)) {
			if(tableName!=null) {
				return addIdentifier(tableName);
			}
		}
		return addIdentifier(smartDataSource.getTableName(clazz));
	}
	//
	protected String getFromSql() {
		StringBuilder sql=new StringBuilder();
		sql.append("\nfrom ").append(getTableName(entityClass)).append(" ").
				append(MAIN_TABLE_ALIAS).append(" ");
		return sql.toString();
	}
	//
	protected String getJoinSql() {
		StringBuilder sql=new StringBuilder();
		createJoins();
		if(joinList!=null) {
			for (Join join : joinList) {
				JoinType joinType=join.getJoinType();
				if(joinType.equals(JoinType.INNER_JOIN)) {
					sql.append("\ninner join  ");
				}
				if(joinType.equals(JoinType.LEFT_JOIN)) {
					sql.append("\nleft join  ");
				}
				if(joinType.equals(JoinType.RIGHT_JOIN)) {
					sql.append("\nright join  ");
				}
				addJoinSql(sql, join);
			}
		}
		return sql.toString();
	}
	//
	protected void addJoinSql(StringBuilder sql,Join join) {
		sql.append(getTableName(join.getTable2())).append(" ").append(join.getTable2Alias());
		sql.append(" on ");
		for(int i=0;i<join.getTable1Fields().length;i++) {
			sql.append(join.getTable1Alias()).append("."+addIdentifier(convertFieldNameToColumnName(join.getTable1(),join.getTable1Fields()[i]))+"=");
			sql.append(join.getTable2Alias()).append(".").append(addIdentifier(convertFieldNameToColumnName(join.getTable2(),join.getTable2Fields()[i])));
			if(i<join.getTable1Fields().length-1) {
				sql.append(" and ");
			}
		}
		sql.append(" ");
	}
	//
	protected WhereStatment getWhereSql() {
		addWheres(query);
		return new WhereSqlBuilder(getDatabaseType(),qw).build();
	}
	//
	protected String getGroupBySql() {
		StringBuilder sql=new StringBuilder();
		if(groupBys.size()>0) {
			sql.append("\ngroup by ");
			for (EntityFieldInfo field : groupBys) {
				if(!SmartJdbcUtils.isEmpty(field.getTableAlias())) {
					sql.append(field.getTableAlias()).append(".");
				}
				sql.append(convertFieldNameToColumnName(field.getEntityClass(),field.getName())).append(",");
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
		if(!needOrderBy) {
			return "";
		}
		StringBuilder sql=new StringBuilder();
		addOrderBy(query);
		if (qw.getOrderBys().size()>0) {
			sql.append("\norder by ");
			for (String orderBy : qw.getOrderBys()) {
				sql.append(orderBy).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(" ");
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
	protected String getNowaitSql() {
		if(qw.isNowait()) {
			return "\nnowait ";
		}
		return "";
	}
	//
	public Class<?> getEntityClass() {
		return entityClass;
	}
	
	public List<EntityFieldInfo> getSelectFields() {
		return selectFields;
	}
	
	public void setSelectFields(List<EntityFieldInfo> selectFields) {
		this.selectFields = selectFields;
	}
	
	//
	protected SqlBean build(StringBuilder selectSql) {
		SelectSql bean=new SelectSql();
		bean.selectSql=selectSql.toString();
		bean.fromSql=getFromSql();
		bean.joinSql=getJoinSql();
		WhereStatment ws=getWhereSql();
		bean.whereSql=ws.sql;
		bean.groupBySql=getGroupBySql();
		bean.orderBySql=getOrderBySql();
		bean.limitSql=getLimitSql();
		bean.forUpdateSql=getForUpdateSql();
		bean.nowaitSql=getNowaitSql();
		bean.sql=bean.toSql();
		bean.parameters=ws.values;
		return bean;
	}
	//
	/**
	 * 
	 * @return
	 */
	protected SqlBean queryCount() {
		StringBuilder sql = new StringBuilder();
		sql.append("\nselect count(1) ");
		this.needPaging=false;
		return build(sql);
	}
	
	/**
	 * 
	 * @return
	 */
	protected SqlBean query() {
		StringBuilder sql = new StringBuilder();
		buildSelectFields();
		sql.append("\nselect ");
		if(aggregationList.size()==0&&groupBys.size()==0) {
			addSelectFields(sql);
		}else {
			addAggregation(sql);
		}
		return build(sql);
	}
		
	@Override
	public SqlBean build() {
		entity=CacheManager.getEntityInfo(entityClass);
		if(query!=null) {
			queryInfo=CacheManager.getQueryInfo(query.getClass());
		}
		if(isSelectCount) {
			return queryCount();
		}else {
			return query();
		}
	}
}
