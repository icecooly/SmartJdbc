package io.itit.smartjdbc.provider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.Config;
import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.QueryWhere;
import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.SqlBean;
import io.itit.smartjdbc.Where;
import io.itit.smartjdbc.annotations.EntityField;
import io.itit.smartjdbc.annotations.ForeignKey;
import io.itit.smartjdbc.annotations.InnerJoin;
import io.itit.smartjdbc.annotations.InnerJoins;
import io.itit.smartjdbc.annotations.LeftJoin;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.cache.Caches;
import io.itit.smartjdbc.cache.EntityFieldInfo;
import io.itit.smartjdbc.cache.EntityInfo;
import io.itit.smartjdbc.cache.QueryFieldInfo;
import io.itit.smartjdbc.cache.QueryInfo;
import io.itit.smartjdbc.enums.OrderBy;
import io.itit.smartjdbc.enums.SqlOperator;
import io.itit.smartjdbc.util.JSONUtil;
import io.itit.smartjdbc.util.SqlUtil;
import io.itit.smartjdbc.util.StringUtil;

/**
 * 
 * @author skydu
 *
 */
public class SelectProvider extends SqlProvider{
	//
	private static Logger logger=LoggerFactory.getLogger(SelectProvider.class);
	//
	public static class SelectField{//select tableAlias.field as asField
		public String tableAlias;
		public String field;
		public String preAsField;
		public String asField;
		public boolean distinct;
		public String statFunction;
	}
	//
	public static class GroupByField{
		public String tableAlias;
		public String field;
	}
	//
	public static class Join{
		public String key;
		public String table1Alias;
		public Class<?> table1;
		public String table2Alias;
		public Class<?> table2;
		public String[] table1Fields;
		public String[] table2Fields;
		public List<Join> joins;
		//
		public Join() {
			joins=new ArrayList<>();
		}
	}
	//
	public static class SortField {
		public String fieldName;
		public OrderBy sortType;
		public int order;
	}
	//
	protected Class<?> entityClass;
	protected Query<?> query;
	protected boolean isSelectCount;
	protected boolean needPaging;
	protected boolean needOrderBy;
	protected boolean isForUpdate;
	protected List<SelectField> selectFields;
	protected boolean ingoreSelectFileds;
	protected Set<String> includeFields;
	protected Set<String> excludeFields;//userName not user_name
	protected Map<String,Join> innerJoinMap;
	protected Map<String,String> innerJoinFieldAliasMap;
	protected List<Join> innerJoins;//inner join tableName alias on 
	protected List<Join> leftJoins;//left join tableName alias on 
	protected QueryWhere qw;
	protected List<GroupByField> groupBys;
	//
	public SelectProvider(Class<?> entityClass) {
		this.entityClass=entityClass;
		this.selectFields=new ArrayList<>();
		this.includeFields=new LinkedHashSet<>();
		this.excludeFields=new LinkedHashSet<>();
		this.qw=QueryWhere.create();
		this.groupBys=new ArrayList<>();
		this.leftJoins=new ArrayList<>();
		this.innerJoins=new ArrayList<>();
		this.needOrderBy=true;
	}
	//
	public SelectProvider selectCount() {
		this.isSelectCount=true;
		return this;
	}
	//
	public SelectProvider sum(String sumField) {
		sum(MAIN_TABLE_ALIAS, sumField, sumField);
		return this;
	}
	//
	public SelectProvider sum(String alias,String field,String asField) {
		select(alias, field,null,asField, false, "sum");
		return this;
	}
	//
	public SelectProvider ingoreSelectFileds() {
		this.ingoreSelectFileds=true;
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
		return this;
	}
	//
	public SelectProvider select(String field) {
		return select(null, field);
	}
	//
	public SelectProvider select(String tableAlias,String field) {
		return select(tableAlias,field,null);
	}
	//
	public SelectProvider select(String tableAlias,String field,String asAlias) {
		return select(tableAlias, field,null,asAlias, false, null);
	}
	//
	public SelectProvider select(String tableAlias,String field,
			String preAsField,String asField,boolean distinct,String statFunction) {
		selectFields.add(createSelectField(tableAlias, field, 
				preAsField,asField, distinct, statFunction));
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
	protected SelectField createSelectField(String tableAlias,String field,
			String preAsField,String asField,boolean distinct,String statFunction) {
		SelectField sf=new SelectField();
		sf.tableAlias=tableAlias;
		sf.field=field;
		sf.preAsField=preAsField;
		sf.asField=asField;
		sf.distinct=distinct;
		sf.statFunction=statFunction;
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
	public SelectProvider whereSql(String sql,Object ...values){
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
	protected GroupByField createGroupByField(String tableAlias,String field) {
		GroupByField groupByField=new GroupByField();
		groupByField.tableAlias=tableAlias;
		groupByField.field=field;
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
		this.isForUpdate=true;
		return this;
	}
	//
	protected void getQueryFields(List<QueryFieldInfo> fieldList,Object obj,QueryInfo queryInfo){
		for (QueryFieldInfo fieldInfo : queryInfo.fieldList) {
			try {
				Field field=fieldInfo.field;
				if(!field.isAccessible()) {
					field.setAccessible(true);
				}
				Object reallyValue = field.get(obj);
				if (reallyValue == null) {
					continue;
				}
				fieldList.add(fieldInfo);
			}catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new IllegalArgumentException(e);
			}
		}
		for (QueryInfo e : queryInfo.children) {
			try {
				if(!e.field.isAccessible()) {
					e.field.setAccessible(true);
				}
				Object reallyValue = e.field.get(obj);
				if (reallyValue == null) {
					continue;
				}
				getQueryFields(fieldList,reallyValue,e);
			} catch (Exception ex) {
				logger.error(ex.getMessage(),ex);
				throw new IllegalArgumentException(ex);
			}	
		}
	}
	//
	protected boolean isValidInnerJoin(InnerJoin innerJoin) {
		if(innerJoin==null) {
			return false;
		}
		if(innerJoin.table2().equals(void.class)) {
			throw new SmartJdbcException("@InnerJoin table2 cannot be null");
		}
		if(innerJoin.table1Fields().length==0) {
			throw new SmartJdbcException("@InnerJoin table1Fields cannot be null");
		}
		if(innerJoin.table2Fields().length==0) {
			throw new SmartJdbcException("@InnerJoin table2Fields cannot be null");
		}
		if(innerJoin.table1Fields().length!=innerJoin.table2Fields().length) {
			throw new SmartJdbcException("@InnerJoin table1Fields length not equal table2Fields length");
		}
		return true;
	}
	//
	protected String getInnerJoinKey(InnerJoin innerJoin) {
		StringBuilder key=new StringBuilder();
		for (String table1Field : innerJoin.table1Fields()) {
			key.append(table1Field).append("-");
		}
		key.append(innerJoin.table2().getName()).append("-");
		for (String table2Field : innerJoin.table2Fields()) {
			key.append(table2Field).append("-");
		}
		key.deleteCharAt(key.length()-1);
		return key.toString();
	}
	//
	protected Map<String, Join> getInnerJoins(Query<?> query) {
		Map<String, Join> map = new LinkedHashMap<>();
		if(query==null) {
			return map;
		}
		List<QueryFieldInfo> fieldInfos=new ArrayList<>();
		QueryInfo info=Caches.getQueryInfo(query.getClass());
		getQueryFields(fieldInfos,query,info);
		int index = 1;
		innerJoinFieldAliasMap=new HashMap<>();
		for (QueryFieldInfo fieldInfo : fieldInfos) {
			InnerJoin innerJoin=fieldInfo.innerJoin;
			InnerJoins innerJoins=fieldInfo.innerJoins;
			QueryField queryField=fieldInfo.queryField;
			String foreignKeyFields="";
			if(queryField!=null) {
				foreignKeyFields=queryField.foreignKeyFields();
			}
			if(innerJoin==null&&innerJoins==null&&StringUtil.isEmpty(foreignKeyFields)) {
				continue;
			}
			List<InnerJoin> innerJoinsList=new ArrayList<>();
			if(isValidInnerJoin(innerJoin)) {
				innerJoinsList.add(innerJoin);
			}
			if(innerJoins!=null&&innerJoins.joins()!=null) {
				for (InnerJoin join : innerJoins.joins()) {
					if(isValidInnerJoin(join)) {
						innerJoinsList.add(join);
					}
				}
			}
			if(innerJoinsList.size()>0) {//use annotation
				Join join=null;
				Class<?> table1=entityClass;
				String table1Alias=MAIN_TABLE_ALIAS;
				for (InnerJoin j: innerJoinsList) {
					String key=getInnerJoinKey(j);
					if(join==null) {
						join = map.get(key);
						if(join==null) {
							String table2Alias=StringUtil.isEmpty(j.table2Alias())?"i"+(index++):j.table2Alias();
							join=createInnerJoin(key,table1Alias,table2Alias,table1,j.table2(),
									j.table1Fields(),j.table2Fields());
							map.put(key, join);
						}
					}else {
						Join childJoin=getJoin(key, join.joins);
						if(childJoin==null) {
							String table2Alias=StringUtil.isEmpty(j.table2Alias())?"i"+(index++):j.table2Alias();
							childJoin=createInnerJoin(key,table1Alias,table2Alias,table1,j.table2(),
									j.table1Fields(),j.table2Fields());
							join.joins.add(childJoin);
						}
						join=childJoin;
					}
					table1=join.table2;
					table1Alias=join.table2Alias;
				}
				innerJoinFieldAliasMap.put(fieldInfo.fieldName, table1Alias);
			}else if(!StringUtil.isEmpty(foreignKeyFields)) {
				String[] foreignKeyIds=foreignKeyFields.split(",");
				Class<?> table1=entityClass;
				String table1Alias=MAIN_TABLE_ALIAS;
				Join join=null;
				for (String id : foreignKeyIds) {
					Field foreignKeyField=null;
					try {
						foreignKeyField=table1.getDeclaredField(id);
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
						throw new IllegalArgumentException(e.getMessage()+"/"+table1.getSimpleName());
					}
					ForeignKey foreignKey=foreignKeyField.getAnnotation(ForeignKey.class);
					if(foreignKey==null) {
						throw new IllegalArgumentException("@ForeignKey not found in "+
									entityClass.getSimpleName()+"."+foreignKeyField.getName());
					}
					Class<?> table2=foreignKey.entityClass();
					String table2Field=foreignKey.field();
					String key=id+"-"+table2.getName()+"-"+table2Field;
					if(join==null) {
						join = map.get(key);
						if(join==null) {
							join=createInnerJoin(key,table1Alias,"i"+(index++),table1, table2,new String[] {id},new String[] {table2Field});
							map.put(key, join);
						}
					}else {
						Join childJoin=getJoin(key, join.joins);
						if(childJoin==null) {
							childJoin=createInnerJoin(key,table1Alias,"i"+(index++),table1,table2,new String[] {id},new String[] {table2Field});
							join.joins.add(childJoin);
						}
						join=childJoin;
					}
					table1=table2;
					table1Alias=join.table2Alias;
				}
				innerJoinFieldAliasMap.put(fieldInfo.fieldName, table1Alias);
			}else {
				continue;
			}
		}
		return map;
	}
	//
	protected Join createInnerJoin(String key,String table1Alias,String table2Alias,
			Class<?> table1,Class<?> table2,String[] table1Fields,String[] table2Fields) {
		Join join=new Join();
		join.key=key;
		join.table1Alias=table1Alias;
		join.table2Alias=table2Alias;
		join.table1Fields=table1Fields;
		join.table2Fields=table2Fields;
		join.table1=table1;
		join.table2=table2;
		this.innerJoins.add(join);
		return join;
	}
	//
	protected QueryInfo createQueryInfo(Query<?> query){
		QueryInfo queryInfo = Caches.getQueryInfo(query.getClass());
		return queryInfo;
	}
	/**
	 * 
	 * @param q
	 */
	protected void addWheres(Query<?> q) {
		if(q==null) {
			return;
		}
		QueryInfo queryInfo=createQueryInfo(q);
		Map<String,Object> paraMap=new HashMap<>();
		if(!q.params.isEmpty()) {
			paraMap.putAll(q.params);
		}
		addWheres(qw.getWhere(), paraMap, q, queryInfo);
		logger.error("xxxxxx:{}",JSONUtil.toJson(qw));
	}
	//
	protected void addWheres(Where w,Map<String,Object> paraMap,Object obj,QueryInfo queryInfo) {
		try {
			for (QueryFieldInfo info : queryInfo.fieldList) {
				Field field=info.field;
				Object value=field.get(obj);
				if(value==null) {
					continue;
				}
				QueryField queryField=field.getAnnotation(QueryField.class);
				String alias = MAIN_TABLE_ALIAS;
				if(!StringUtil.isEmpty(queryField.alias())) {
					alias=queryField.alias();
				}
				InnerJoins innerJoins=field.getAnnotation(InnerJoins.class);
				InnerJoin innerJoin=field.getAnnotation(InnerJoin.class);
				if(innerJoin!=null||(innerJoins!=null&&innerJoins.joins()!=null)||
						(queryField!=null&&!StringUtil.isEmpty(queryField.foreignKeyFields()))) {
					alias=innerJoinFieldAliasMap.get(info.fieldName);
				}
				//
				if(queryField!=null&&!StringUtil.isEmpty(queryField.whereSql())) {//whereSql check first
					String whereSql=queryField.whereSql();
					SqlBean sqlBean=parseSql(whereSql, paraMap);//eg:userName like #{userName}
					w.whereSql(sqlBean.sql,sqlBean.parameters);
				}else {
					String dbFieldName=convertFieldName(field.getName());
					if(queryField!=null&&(!StringUtil.isEmpty(queryField.field()))) {
						dbFieldName=convertFieldName(queryField.field());
					}
					SqlOperator operator=SqlOperator.EQ;//default eq
					if(queryField!=null) {
						operator=queryField.operator();
					}
					w.where(alias,dbFieldName,operator,value);
				}
			}
			//
			for (QueryInfo child : queryInfo.children) {
				Object childObj=child.field.get(obj);
				if(childObj==null) {
					continue;
				}
				if(w.children.isEmpty()) {
					w.conditionType=child.conditionType;
					addWheres(w, paraMap, childObj, child);
				}else {
					Where childWhere=new Where(child.conditionType);
					w.children.add(childWhere);
					addWheres(childWhere, paraMap, childObj, child);
				}
				
				
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	//
	public static boolean preParseSql(String sql) {
		String[] regexs= {"\\#\\{[a-zA-Z_$][a-zA-Z0-9_$]*\\}","\\$\\{[a-zA-Z_$][a-zA-Z0-9_$]*\\}"};
		for (String regex : regexs) {
			Pattern p=Pattern.compile(regex);
			Matcher m = p.matcher(sql);
			if(m.find()) { 
			    return true;
			}
		}
		return false;
	}
	//
	/**
	 * 
	 * @param data
	 * @param regex
	 * @return
	 */
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
	public static SqlBean parseSql(String sql,Map<String,Object> paraMap) {
		Pattern p=Pattern.compile("\\#\\{([a-zA-Z_][a-zA-Z0-9_]*)\\}");
		Matcher m = p.matcher(sql);
		Pattern $p=Pattern.compile("\\$\\{([a-zA-Z_][a-zA-Z0-9_]*)\\}");
		Matcher $m = $p.matcher(sql);
		String newSql=sql;
		Object[] values=null;
		List<String> groups=new ArrayList<>();
		while(m.find()) { 
		    groups.add(m.group(1));
		}
		newSql=m.replaceAll("?");
		values=new Object[groups.size()];
		int i=0;
		for (String group : groups) {
			Object value=paraMap.get(group);
			if(value==null) {
				throw new SmartJdbcException(group+" not found.\nsql:"+sql+
						"\nall can choose paras is:"+paraMap.keySet()); 
			}
			values[i++]=value;
		}
		//
		while($m.find()) { 
			String group=$m.group(1);
			String replaceGroup="\\$\\{"+group+"\\}";
			Object value=paraMap.get(group);
			if(value==null) {
				throw new SmartJdbcException(group+" not found.\nsql:"+sql+
						"\nall can choose paras is:"+paraMap.keySet()); 
			}
			if(value instanceof String) {
				newSql=newSql.replaceAll(replaceGroup,"'"+value.toString()+"'");
			}else if(value instanceof Collection<?>){
				StringBuilder in=new StringBuilder();
				Collection<?> array=(Collection<?>)value;
				in.append("(");
				for (Object v : array.toArray()) {
					in.append(v).append(",");
				}
				if(in.length()>1) {
					in.deleteCharAt(in.length()-1);
				}
				in.append(")");
				newSql=newSql.replaceAll(replaceGroup,in.toString());
			}else {
				newSql=newSql.replaceAll(replaceGroup,value.toString());
			}
		}
		return new SqlBean(newSql,values);
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
		if(query==null||query.orderBys==null||query.orderBys.isEmpty()) {
			return orderByList;
		}
		for (Map.Entry<String,OrderBy> entry : query.orderBys.entrySet()) {
			String fieldName=entry.getKey();
			if(fieldName==null) {
				continue;
			}
			SqlUtil.checkColumnName(fieldName);
			OrderBy orderBy=entry.getValue();
			String dbField=Config.convertFieldName(fieldName);
			if(orderBy.equals(OrderBy.ASC)) {
				orderByList.add(dbField+" asc");
			}else if(orderBy.equals(OrderBy.DESC)) {
				orderByList.add(dbField+" desc");
			}
		}
		return orderByList;
	}
	//
	protected void addPaging(Query<?> query) {
		if(query==null) {
			return;
		}
		this.limit(query.getStartPageIndex(),query.pageSize);
	}
	//
	protected void buildSelectFields(){
		int index=1;
		Map<String, Join> map = new LinkedHashMap<>();
		EntityInfo info=Caches.getEntityInfo(entityClass);
		for (EntityFieldInfo fieldInfo : info.fieldList) {
			Field field=fieldInfo.field;
			if (Modifier.isStatic(field.getModifiers())|| Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			if(includeFields!=null&&!includeFields.isEmpty()&&(!includeFields.contains(field.getName()))){
				continue;
			}
			if(excludeFields.contains(field.getName())){
				continue;
			}	
			EntityField entityField = fieldInfo.entityField;
			if(entityField!=null&&entityField.ignoreWhenSelect()) {
				continue;
			}
			if(entityField==null) {
				select(MAIN_TABLE_ALIAS, field.getName());
				continue;
			}
			boolean distinct=entityField.distinct();
			String statFunc=entityField.statFunc();
			String reallyName=field.getName();
			if(!StringUtil.isEmpty(entityField.field())) {
				reallyName=entityField.field();
			}
			//
			LeftJoin leftJoin=fieldInfo.leftJoin;
			if(leftJoin!=null) {
				Join join=createLeftJoin(field.getName(),MAIN_TABLE_ALIAS,"l"+(index++),
						entityClass,leftJoin.table2(),leftJoin.table1Fields(),leftJoin.table2Fields());
				select(join.table2Alias,reallyName,null,field.getName(),distinct,statFunc);
			}else if(!StringUtil.isEmpty(entityField.foreignKeyFields())) {
				String foreignKeyId = entityField.foreignKeyFields();
				String[] foreignKeyIds=foreignKeyId.split(",");
				Class<?> table1=entityClass;
				String table1Alias=MAIN_TABLE_ALIAS;
				Join join=null;
				for (String id : foreignKeyIds) {
					Field foreignKeyField=null;
					try {
						foreignKeyField=table1.getDeclaredField(id);
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
						throw new IllegalArgumentException(e.getMessage()+"/"+table1.getSimpleName());
					}
					ForeignKey foreignKey=foreignKeyField.getAnnotation(ForeignKey.class);
					if(foreignKey==null) {
						throw new IllegalArgumentException("@ForeignKey not found in "+
									entityClass.getSimpleName()+"."+foreignKeyField.getName());
					}
					Class<?> table2=foreignKey.entityClass();
					String key=id;
					if(join==null) {
						join = map.get(key);
						if(join==null) {
							join=createLeftJoin(key,table1Alias,"l"+(index++),table1, table2,new String[] {id});
							map.put(key, join);
						}
					}else {
						Join childJoin=getJoin(key, join.joins);
						if(childJoin==null) {
							childJoin=createLeftJoin(key,table1Alias,"l"+(index++),table1,table2,new String[] {id});
							join.joins.add(childJoin);
						}
						join=childJoin;
					}
					table1=table2;
					table1Alias=join.table2Alias;
				}
				if(WRAP_TYPES.contains(field.getType())){
					addSelect(join.table2Alias, field, entityField);
				}else if(field.getGenericType() instanceof ParameterizedType){
					addSelect(join.table2Alias, field, entityField);
				}else {
					List<Field> subClassFields=getPersistentFields((Class<?>)field.getGenericType());
					for (Field subClassField : subClassFields) {
						select(join.table2Alias,subClassField.getName(),field.getName()+"_",
								subClassField.getName(),distinct,statFunc);
					}
				}
			}else {
				addSelect(MAIN_TABLE_ALIAS, field, entityField);
				continue;
			}
		}
	}
	//
	protected void addSelect(String tableAlias,Field field,EntityField entityField) {
		String selectField=field.getName();
		String asField=null;
		if(!StringUtil.isEmpty(entityField.field())) {
			asField=field.getName();
			selectField=entityField.field();
		}
		if(!StringUtil.isEmpty(entityField.statFunc())) {
			asField=field.getName();
		}
		select(tableAlias,selectField,null,asField,entityField.distinct(),entityField.statFunc());
	}
	//
	protected String getSinglePrimaryKey(Class<?> clazz) {
		List<Field> list=SqlProvider.getPrimaryKey(clazz);
		if(list.size()>1||list.size()==0) {
			throw new SmartJdbcException(clazz.getName()+" primaryKey column can only be one.");
		}
		return list.get(0).getName();
	}
	//
	protected Join getJoin(String key,List<Join> list) {
		for (Join join : list) {
			if(join.key.equals(key)) {
				return join;
			}
		}
		return null;
	}
	//
	protected Join createLeftJoin(String key,String table1Alias,String table2Alias,Class<?> table1,Class<?> table2,
			String[] table1Fields) {
		return createLeftJoin(key, table1Alias, table2Alias, table1, table2, table1Fields, new String[] {getSinglePrimaryKey(table2)});
	}
	//
	protected Join createLeftJoin(String key,String table1Alias,String table2Alias,Class<?> table1,Class<?> table2,
			String[] table1Fields,String[] table2Fields) {
		Join join = new Join();
		join.key=key;
		join.table1Alias=table1Alias;
		join.table2Alias=table2Alias;
		join.table1=table1;
		join.table2 = table2;
		join.table1Fields=table1Fields;
		join.table2Fields= table2Fields;
		leftJoins.add(join);
		return join;
	}
	
	/**
	 * 
	 * @return
	 */
	protected SqlBean queryCount() {
		StringBuilder sql = new StringBuilder();
		sql.append("\nselect count(1) \n");
		this.needPaging=false;
		return build(sql);
	}
	
	/**
	 * 
	 * @return
	 */
	protected SqlBean query() {
		StringBuilder sql = new StringBuilder();
		if(!ingoreSelectFileds) {
			buildSelectFields();
		}
		sql.append("\nselect ");
		if(selectFields.size()==0) {
			throw new IllegalArgumentException("no select field found in "+entityClass.getName());
		}
		addSelectFields(sql);
		return build(sql);
	}
	
	protected void addSelectFields(StringBuilder sql) {
		for (SelectField field : selectFields) {
			if(field.distinct) {
				sql.append(" distinct ");
			}
			if(StringUtil.isEmpty(field.statFunction)) {
				sql.append(field.tableAlias).append(".`");
				sql.append(convertFieldName(field.field)).append("`");
			}else {
				sql.append(field.statFunction);
				sql.append("(");
				sql.append(field.tableAlias).append(".`");
				sql.append(convertFieldName(field.field)).append("`");
				sql.append(")");
			}
			if(field.asField!=null) {
				String asField=convertFieldName(field.asField);
				if(field.preAsField!=null) {
					asField=field.preAsField+asField;
				}
				sql.append(" as `").append(asField).append("`");
			}
			sql.append(",");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append("\n");
	}
	//
	//
	protected String getFromSql() {
		StringBuilder sql=new StringBuilder();
		sql.append("from ").append(getTableName(entityClass)).append(" ").append(MAIN_TABLE_ALIAS).append(" \n");
		//inner join
		this.innerJoinMap=getInnerJoins(query);
		for (Join join : innerJoins) {
			sql.append("inner join  ");
			addJoin(sql, join);
		}
		//left join
		for (Join join : leftJoins) {
			sql.append("left join  ");
			addJoin(sql, join);
		}
		return sql.toString();
	}
	//
	protected void addJoin(StringBuilder sql,Join join) {
		sql.append(getTableName(join.table2)).append(" ").append(join.table2Alias);
		sql.append(" on ");
		for(int i=0;i<join.table1Fields.length;i++) {
			sql.append(join.table1Alias).append(".`"+convertFieldName(join.table1Fields[i])+"`=").
			append(join.table2Alias).append(".`").append(convertFieldName(join.table2Fields[i])).append("`");
			if(i<join.table1Fields.length-1) {
				sql.append(" and ");
			}
		}
		sql.append("\n");
	}
	//
	protected String getWhereSql() {
		StringBuilder sql=new StringBuilder();
		addWheres(query);
		sql.append("where 1=1 ");
		sql.append(qw.whereStatement().sql);
		sql.append("\n");
		return sql.toString();
	}
	//
	protected String getGroupBySql() {
		StringBuilder sql=new StringBuilder();
		if(groupBys.size()>0) {
			sql.append("group by ");
			for (GroupByField field : groupBys) {
				if(!StringUtil.isEmpty(field.tableAlias)) {
					sql.append(field.tableAlias).append(".");
				}
				sql.append(convertFieldName(field.field)).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append("\n");
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
			addOrderBy(query);
			if (qw.getOrderBys().size()>0) {
				sql.append("order by ");
				for (String orderBy : qw.getOrderBys()) {
					sql.append(orderBy).append(",");
				}
				sql.deleteCharAt(sql.length()-1);
				sql.append("\n");
			}
		}
		return sql.toString();
	}
	//
	protected String getLimitSql() {
		if(isSelectCount) {
			return "";
		}
		StringBuilder sql=new StringBuilder();
		addPaging(query);	
		if(qw.getLimitEnd()!=-1) {
			sql.append("limit ").append(qw.getLimitStart()).append(",").append(qw.getLimitEnd()).append("\n");
		}
		return sql.toString();
	}
	//
	protected String getForUpdateSql() {
		if(isForUpdate) {
			return "for update \n";
		}
		return "";
	}
	//
	protected SqlBean build(StringBuilder selectSql) {
		SqlBean bean=new SqlBean();
		bean.selectSql=selectSql.toString();
		bean.fromSql=getFromSql();
		bean.whereSql=getWhereSql();
		bean.groupBySql=getGroupBySql();
		bean.orderBySql=getOrderBySql();
		bean.limitSql=getLimitSql();
		bean.forUpdateSql=getForUpdateSql();
		bean.sql=bean.toSql();
		bean.parameters=qw.whereValues();
		return bean;
	}
		
	public Class<?> getEntityClass() {
		return entityClass;
	}

	@Override
	public SqlBean build() {
		if(isSelectCount) {
			return queryCount();
		}
		return query();
	}
	
	public List<SelectField> getSelectFields() {
		return selectFields;
	}
	
	public void setSelectFields(List<SelectField> selectFields) {
		this.selectFields = selectFields;
	}
}
