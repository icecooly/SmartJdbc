package io.itit.smartjdbc.provider.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.itit.smartjdbc.enums.JoinType;
import io.itit.smartjdbc.util.SmartJdbcUtils;

/**
 * 
 * @author skydu
 *
 */
public class Joins {
	private List<Join> joinList=new ArrayList<>();
	private Map<String,Join> joinMap=new HashMap<>();
	private String prefix="l";
	//
	public Joins() {
	}
	//
	public Joins(String prefix) {
		this.prefix = prefix;
	}
	//
	private Join getSameJoin(JoinType joinType, Class<?> table1, Class<?> table2, String table1Alias, String[] table1Fields,
			String[] table2Fields) {
		for (Join j : joinList) {
			if(!j.getJoinType().equals(joinType)) {
				continue;
			}
			if (!Arrays.equals(j.getTable1Fields(), table1Fields)) {
				continue;
			}
			if (!Arrays.equals(j.getTable2Fields(), table2Fields)) {
				continue;
			}
			if (!j.getTable1().equals(table1)) {
				continue;
			}
			if (!j.getTable2().equals(table2)) {
				continue;
			}
			if (!j.getTable1Alias().equals(table1Alias)) {
				continue;
			}
			return j;
		}
		return null;

	}
	//
	public Join addJoin(JoinType joinType,
			Class<?> table1, Class<?> table2, 
			String table1Name, String table2Name,
			String table1Alias, String table2Alias, 
			String[] table1Fields, String[] table2Fields) {
		Join join=getSameJoin(joinType, table1, table2, table1Alias, table1Fields, table2Fields);
		if(join!=null) {
			return join;
		}
		join = new Join(joinType);
		join.setTable1(table1);
		join.setTable2(table2);
		join.setTable1Fields(table1Fields);
		join.setTable2Fields(table2Fields);
		join.setTable1Alias(table1Alias);
		if(SmartJdbcUtils.isEmpty(table2Alias)) {
			join.setTable2Alias(prefix+(joinList.size()+1));
		}else {
			join.setTable2Alias(table2Alias);
		}
		joinList.add(join);
		joinMap.put(join.getTable2Alias(), join);
		return join;
	}
	//
	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}
	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	/**
	 * @return the joinList
	 */
	public List<Join> getJoinList() {
		return joinList;
	}
	/**
	 * @param joinList the joinList to set
	 */
	public void setJoinList(List<Join> joinList) {
		this.joinList = joinList;
	}
	/**
	 * @return the joinMap
	 */
	public Map<String, Join> getJoinMap() {
		return joinMap;
	}
	/**
	 * @param joinMap the joinMap to set
	 */
	public void setJoinMap(Map<String, Join> joinMap) {
		this.joinMap = joinMap;
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
