package io.itit.smartjdbc.provider.where;

import java.util.LinkedList;
import java.util.List;

import io.itit.smartjdbc.enums.ConditionType;
import io.itit.smartjdbc.enums.DatabaseType;
import io.itit.smartjdbc.provider.where.QueryWhere.WhereStatment;
import io.itit.smartjdbc.provider.where.Where.Condition;
import io.itit.smartjdbc.provider.where.operator.Operator;
import io.itit.smartjdbc.provider.where.operator.OperatorBuilder;
import io.itit.smartjdbc.provider.where.operator.OperatorContext;
import io.itit.smartjdbc.provider.where.operator.WhereSqlOperator;
import io.itit.smartjdbc.util.StringUtil;

/**
 * 
 * @author skydu
 *
 */
public class WhereSqlBuilder {
	//
	private QueryWhere queryWhere;
	//
	public WhereSqlBuilder(QueryWhere queryWhere) {
		this.queryWhere=queryWhere;
	}
	//
	public boolean haveCondition(Where w) {
		if (w != null && w.conditionList != null && w.conditionList.size() > 0) {
			return true;
		}
		if (w != null && w.children != null) {
			for (Where child : w.children) {
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
	 * @param databaseType
	 * @param where
	 * @return
	 */
	public WhereStatment whereStatement(DatabaseType databaseType){
		WhereStatment statment=new WhereStatment();
		List<Object>values=new LinkedList<Object>();
		StringBuilder sql=new StringBuilder();
		sql.append("\nwhere 1=1 ");
		appendWhereSql(databaseType, sql, values, queryWhere.where, null);
		sql.append(" ");
		statment.sql=sql.toString();
		statment.values=values.toArray();
		return statment;
	}
	
	private String getConditionTypeSql(ConditionType conditionType) {
		return " "+conditionType.name().toLowerCase()+" ";
	}
	
	/**
	 * 
	 * @param databaseType
	 * @param sql
	 * @param valueList
	 * @param where
	 */
	public void appendWhereSql(DatabaseType databaseType, StringBuilder sql, 
			List<Object> valueList, Where where, Where parentWhere) {
		boolean haveCondition = haveCondition(where);
		if (!haveCondition) {
			return;
		}
		if(parentWhere!=null) {
			sql.append(getConditionTypeSql(parentWhere.conditionType)).append("( ");
		}else {
			sql.append(" and(  ");
		}
		List<Condition> conditions = where.conditionList;
		if (conditions.size() == 0) {
			sql.append(" 1=1  ");
		}
		if (conditions.size() > 0) {
			int index = 0;
			OperatorContext ctx = new OperatorContext(databaseType);
			ctx.setParameters(valueList);
			for (Condition c : conditions) {
				if (index > 0) {
					sql.append(getConditionTypeSql(where.conditionType));
				}
				String operatorSql = null;
				if (c.key != null) {
					ctx.setCondition(c);
					Operator operator = OperatorBuilder.build(ctx);
					operatorSql = operator.build();
				} else if (c.whereSql != null) {
					WhereSqlOperator whereSqlOperator = new WhereSqlOperator(ctx, c);
					operatorSql = whereSqlOperator.build();
				}
				if (StringUtil.isEmpty(operatorSql)) {
					operatorSql = "1=1";
				}
				sql.append(operatorSql);
				index++;
			} // for
		}
		//
		if (where.children != null && where.children.size() > 0) {
			for (Where w : where.children) {
				appendWhereSql(databaseType, sql, valueList, w, where);
			}
		}
		//
		sql.append("  )  ");
	}
}
