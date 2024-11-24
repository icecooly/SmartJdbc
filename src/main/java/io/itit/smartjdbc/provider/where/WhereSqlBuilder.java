package io.itit.smartjdbc.provider.where;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.enums.ConditionType;
import io.itit.smartjdbc.enums.DatabaseType;
import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.QueryWhere.WhereStatment;
import io.itit.smartjdbc.provider.where.Where.Condition;
import io.itit.smartjdbc.provider.where.operator.IOperator;
import io.itit.smartjdbc.provider.where.operator.IOperatorFactory;
import io.itit.smartjdbc.provider.where.operator.OperatorContext;
import io.itit.smartjdbc.provider.where.operator.WhereSqlOperator;
import io.itit.smartjdbc.provider.where.operator.mysql.MysqlOperatorFactory;
import io.itit.smartjdbc.provider.where.operator.pgsql.PgsqlOperatorFactory;
import io.itit.smartjdbc.util.JSONUtil;
import io.itit.smartjdbc.util.SmartJdbcUtils;

/**
 * 
 * @author skydu
 *
 */
public class WhereSqlBuilder {
	//
	private static Logger log=LoggerFactory.getLogger(WhereSqlBuilder.class);
	//
	private QueryWhere queryWhere;
	private DatabaseType databaseType;
	private StringBuilder sql;
	private List<Object> values;
	//
	public WhereSqlBuilder(DatabaseType databaseType, QueryWhere queryWhere) {
		this.databaseType=databaseType;
		this.queryWhere=queryWhere;
		this.sql=new StringBuilder();
		this.values=new ArrayList<>(0);
	}
	//

	/**
	 * 
	 * @return
	 */
	public WhereStatment build(){
		WhereStatment statment=new WhereStatment();
		StringBuilder finalSql=new StringBuilder();
		boolean haveCondtion=appendWhereSql(queryWhere.where);
		if(haveCondtion) {
			finalSql.append("\nwhere ");
		}
		finalSql.append(sql.toString());
		finalSql.append(" ");
		statment.sql=finalSql.toString();
		statment.values=values.toArray();
		return statment;
	}
	
	/**
	 * 
	 * @param conditionType
	 * @return
	 */
	private String getConditionTypeSql(ConditionType conditionType) {
		return " "+conditionType.name().toLowerCase()+" ";
	}
	
	private IOperatorFactory getOperatorFactory(OperatorContext ctx) {
		DatabaseType type=ctx.getDatabaseType();
		switch (type) {
		case MYSQL:
			return new MysqlOperatorFactory();
		case POSTGRESQL:
		case KINGBASE:
			return new PgsqlOperatorFactory();
		default:
			return null;
		}
	}
	//
	public static class WhereCondition{
		public ConditionType conditionType;
		public List<WhereCondition> children=new ArrayList<>(0);
		public List<SqlBean> conditionList=new ArrayList<>(0);
	}
	//
	private WhereCondition buildWhere(Where where, List<Object> parameters) {
		WhereCondition wc=new WhereCondition();
		wc.conditionType=where.conditionType;
		List<Condition> conditions = where.conditionList;
		if (conditions.size() > 0) {
			OperatorContext ctx = new OperatorContext(databaseType);
			ctx.setParameters(parameters);
			IOperatorFactory operatorFactory=getOperatorFactory(ctx);
			for (Condition c : conditions) {
				SqlBean operatorSql = null;
				if (c.key!= null) {
					if(c.value==null&&c.ignoreNull) {
						continue;//忽略空值 20240306
					}
					ctx.setCondition(c);
					IOperator operator = operatorFactory.create(ctx);
					operatorSql = operator.build(ctx);
				} else if (c.whereSql != null) {
					WhereSqlOperator whereSqlOperator = new WhereSqlOperator(ctx, c);
					operatorSql = SqlBean.build(whereSqlOperator.build());
				}
				if (operatorSql==null||SmartJdbcUtils.isEmpty(operatorSql.sql)) {
					if(!c.ignoreNull) {
						operatorSql=new SqlBean();
						operatorSql.sql=" 1<>1 ";
						log.warn("operatorSql is null Condition:{}", JSONUtil.dump(c));
					}else {
						continue;
					}
				}
				wc.conditionList.add(operatorSql);
			} // for
		}
		//
		if (where.children!=null && where.children.size()> 0) {
			for (Where w : where.children) {
				WhereCondition child=buildWhere(w, parameters);
				wc.children.add(child);
			}
		}
		return wc;
	}
	//
	public boolean appendWhereSql(Where where) {
		List<Object> parameters=new ArrayList<>();
		WhereCondition wc=buildWhere(where, parameters);
		for (Object parameter : parameters) {
			values.add(parameter);
		}
		return appendWhereSql0(wc, null);
	}
	
	private boolean haveCondition(WhereCondition w) {
		if (w != null && w.conditionList != null && w.conditionList.size() > 0) {
			return true;
		}
		if (w != null && w.children != null) {
			for (WhereCondition child : w.children) {
				boolean childHaveCondition = haveCondition(child);
				if (childHaveCondition) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param where
	 * @param parentWhere
	 */
	public boolean appendWhereSql0(WhereCondition where, WhereCondition parentWhere) {
		boolean haveCondition = haveCondition(where);
		if (!haveCondition) {
			return false;
		}
		ConditionType conditionType=where.conditionType;
		if(parentWhere!=null) {
			sql.append(getConditionTypeSql(parentWhere.conditionType));
		}
		sql.append("(");//and ( 或 or(
		List<SqlBean> conditions = where.conditionList;
		if (conditions.size() > 0) {
			int index = 0;
			for (SqlBean c : conditions) {
				if (index>0) {
					sql.append(getConditionTypeSql(conditionType));
				}
				sql.append(c.sql);
				index++;
			} // for
		}
		if (conditions.size()==0) {
			if(conditionType.equals(ConditionType.AND)) {
				sql.append(" 1=1 ");
			}else {
				sql.append(" 1<>1 ");//false
			}
		}
		//
		if (where.children!=null && where.children.size()> 0) {
			for (WhereCondition w : where.children) {
				appendWhereSql0(w, where);
			}
		}
		//
		sql.append(")");
		return true;
	}
}
