package io.itit.smartjdbc.provider.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.itit.smartjdbc.enums.ColumnType;
import io.itit.smartjdbc.enums.DatabaseType;
import io.itit.smartjdbc.enums.JoinType;
import io.itit.smartjdbc.util.StringUtil;

/**
 * 
 * @author skydu
 *
 */
public class EntitySelect extends Entity {

	public String alias;

	public List<EntitySelectField> selectFields;

	public Joins leftJoins;
	
	public Joins innerJoins;

	public QueryInfo queryInfo;

	public List<Aggregation> aggregationList;

	public List<EntitySelectField> groupBys;

	//
	public EntitySelect(DatabaseType databaseType, String tableName) {
		super(databaseType, tableName);
		selectFields = new ArrayList<>();
		aggregationList = new ArrayList<>();
		groupBys = new ArrayList<>();
	}

	//
	public static class EntitySelectField {

		public String alias;

		public String column;

		public String asName;

		/** 统计函数 eg:select min() */
		public String formula;

		public Object value;

		public ColumnType columnType;

		//
		public EntitySelectField(String column, Object value, ColumnType columnType) {
			this.column = column;
			this.value = value;
			this.columnType = columnType;
		}

		public String info() {
			StringBuilder info = new StringBuilder();
			info.append("\nField[" + column + "," + alias + ",");
			if (asName != null) {
				info.append(",").append(asName);
			}
			return info.toString();
		}
	}

	//
	public static class Joins {
		//
//		private static Logger logger=LoggerFactory.get(Joins.class);
		//
		public String prefix;
		public List<Join> joinList;

		//
		public Joins(String prefix) {
			this.prefix = prefix;
			joinList = new ArrayList<>();
		}

		//
		private Join getSameJoin(String table1, String table2, String table1Alias, String[] table1Fields,
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
		public Join addJoin(JoinType joinType, String table1, String table2, String table1Alias,
				String table2Alias, String[] table1Fields, String[] table2Fields) {
			Join join = getSameJoin(table1, table2, table1Alias, table1Fields, table2Fields);
			if (join != null) {
				return join;
			}
			join = new Join(joinType);
			join.table1 = table1;
			join.table2 = table2;
			join.table1Fields = table1Fields;
			join.table2Fields = table2Fields;
			join.table1Alias = table1Alias;
			if (StringUtil.isEmpty(table2Alias)) {
				join.table2Alias = prefix + (joinList.size() + 1);
			} else {
				join.table2Alias = table2Alias;
			}
			joinList.add(join);
			return join;
		}

		//
		public String info() {
			StringBuilder info = new StringBuilder();
			info.append("\nJoins[");
			for (Join join : joinList) {
				info.append(join.info());
			}
			info.append("]");
			return info.toString();
		}
	}

	//
	//
	public static class Join {
		public JoinType joinType;
		public String table1Alias;
		public String table1;
		public String table2Alias;
		public String table2;
		public String[] table1Fields;
		public String[] table2Fields;

		//
		public Join(JoinType joinType) {
			this.joinType = joinType;
		}

		//
		public String info() {
			StringBuilder info = new StringBuilder();
			info.append("\n");
			info.append(table1).append(" ");
			info.append(joinType.name()).append(" ").append(table2).append(" ").append(table2Alias);
			if (table1Fields != null && table2Fields != null) {
				info.append(" on ");
				for (int i = 0; i < table1Fields.length; i++) {
					info.append(table1Alias).append(".").append(table1Fields[i]).append("=").append(table2Alias)
							.append(".").append(table2Fields[i]);
				}
			}
			return info.toString();
		}
	}

}
