package io.itit.smartjdbc.provider.where.operator.pgsql;

import java.util.ArrayList;
import java.util.List;

import io.itit.smartjdbc.provider.entity.SqlBean;
import io.itit.smartjdbc.provider.where.Where.Condition;
import io.itit.smartjdbc.provider.where.operator.ColumnOperator;
import io.itit.smartjdbc.provider.where.operator.OperatorContext;
import io.itit.smartjdbc.util.JSONUtil;

/**
 * 
 * @author skydu
 *
 */
public class PgsqlJsonArrayNotContainsAnyOperator extends ColumnOperator {

	@Override
	public SqlBean build(OperatorContext ctx) {
		Condition c=ctx.getCondition();
		String column = c.key;
		@SuppressWarnings("unchecked")
		List<List<String>> values = (List<List<String>>) c.value;
		if (column == null || values == null|| values.size()==0) {
			return null;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("not( ");
		String columnSql=getColumnSql(ctx);
		sql.append(columnSql).append(" is not null ");
		sql.append(" and (");	
		for (int i=0;i<values.size();i++) {
			sql.append(columnSql).append("@>?::jsonb ");
			List<List<String>> item=new ArrayList<>();
			item.add(values.get(i));
			ctx.addParameter(JSONUtil.toJson(item));
			if(i!=values.size()-1) {
				sql.append(" or ");
			}
		}
		sql.append(")) ");
		return SqlBean.build(sql.toString());
	}

}
