package io.itit.smartjdbc.provider.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.itit.smartjdbc.enums.JoinType;
import io.itit.smartjdbc.util.StringUtil;

/**
 * 
 * @author skydu
 *
 */
public class Joins {
	//
	public String prefix;
	public List<Join> joinList;
	//
	public Joins(String prefix) {
		this.prefix = prefix;
		joinList = new ArrayList<>();
	}
	//
	private Join getSameJoin(Class<?> table1, Class<?> table2, String table1Alias, String[] table1Fields,
			String[] table2Fields) {
		for (Join j : joinList) {
			if (!Arrays.equals(j.table1Fields, table1Fields)) {
				continue;
			}
			if (!Arrays.equals(j.table2Fields, table2Fields)) {
				continue;
			}
			if (!j.table1.equals(table1)) {
				continue;
			}
			if (!j.table2.equals(table2)) {
				continue;
			}
			if (!j.table1Alias.equals(table1Alias)) {
				continue;
			}
			return j;
		}
		return null;

	}
	//
	public Join addJoin(JoinType joinType,Class<?> table1, Class<?> table2, String table1Alias, String table2Alias, 
			String[] table1Fields,
			String[] table2Fields) {
		Join join=getSameJoin(table1, table2, table1Alias, table1Fields, table2Fields);
		if(join!=null) {
			return join;
		}
		join = new Join(joinType);
		join.table1 = table1;
		join.table2 = table2;
		join.table1Fields = table1Fields;
		join.table2Fields = table2Fields;
		join.table1Alias = table1Alias;
		if(StringUtil.isEmpty(table2Alias)) {
			join.table2Alias=prefix+(joinList.size()+1);
		}else {
			join.table2Alias=table2Alias;
		}
		joinList.add(join);
		return join;
	}
	//
	public String info() {
		StringBuilder info=new StringBuilder();
		info.append("\nJoins[");
		for (Join join : joinList) {
			info.append(join.info());
		}
		info.append("]");
		return info.toString();
	}
}
