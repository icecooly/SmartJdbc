package io.itit.smartjdbc.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.serializer.SerializerFeature;

import io.itit.smartjdbc.ResultSetHandler;
import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.SmartJdbc;
import io.itit.smartjdbc.SqlInterceptor;
import io.itit.smartjdbc.Types;
import io.itit.smartjdbc.enums.DatabaseType;
import io.itit.smartjdbc.session.SqlSession;
import io.itit.smartjdbc.stat.DBStat;
import io.itit.smartjdbc.type.Ltree;
import io.itit.smartjdbc.util.JSONUtil;
import io.itit.smartjdbc.util.SmartJdbcUtils;

/**
 * 
 * @author skydu
 *
 */
public abstract class BaseDAO{
	//
	private static final Logger logger=LoggerFactory.getLogger(BaseDAO.class);
	//
	protected String datasourceIndex;
	//
	public static long SLOW_SQL_MIN_USE_TIME=1000;//1s
	//
	public BaseDAO() {
		datasourceIndex=SmartJdbc.DEFAULT_DATASOURCE_INDEX;
	}
	//
	protected void beforeExcute(String sql,Object ... parameters) {
		for (SqlInterceptor interceptor : getSmartDataSource().getSqlInterceptors()) {
			interceptor.beforeExcute(sql,parameters);
		}
	}
	//
	protected void afterExcute(String sql,Object ... parameters) {
		for (SqlInterceptor interceptor : getSmartDataSource().getSqlInterceptors()) {
			interceptor.afterExcute(sql,parameters);
		}
	}
	//
	protected int executeUpdate(Connection conn,PreparedStatement ps,boolean returnAutoGeneratedKeys)
	throws SQLException{
		ResultSet rs=null;
		try {
			int rowCount = ps.executeUpdate();
			if(returnAutoGeneratedKeys){//
				int autoIncrease = -1;
				rs = ps.getGeneratedKeys();
				if (rs.next()) {
					autoIncrease = rs.getInt(1);
				}
				return autoIncrease;
			}
			return rowCount;
		} catch (SQLException e) {
			throw e;
		}finally {
			close(ps,rs);
		}
	}

	//
	protected int[] executeBatch(Connection conn,PreparedStatement ps)
	throws SQLException{
				ResultSet rs=null;
				try {
					return ps.executeBatch();
				} catch (SQLException e) {
					throw e;
				}finally {
					close(ps,rs);
				}
			}
	//
	protected int executeWithGenKey(String sql,Object ... parameters){
		return executeUpdate0(sql, true, parameters);
	}
	//
	protected int executeUpdate0(String sql,boolean returnAutoGeneratedKeys,Object ... parameters){
		int result=-1;
		long startTime=System.currentTimeMillis();
		long useTime=0;
		boolean isException=false;
		SqlSession session=null;
		try {
			beforeExcute(sql, parameters);
			session=getSession();
			Connection conn=session.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql.toString(),
					returnAutoGeneratedKeys ? Statement.RETURN_GENERATED_KEYS:Statement.NO_GENERATED_KEYS);
			set(conn, ps, parameters);
			result=executeUpdate(conn, ps,returnAutoGeneratedKeys);
			useTime=System.currentTimeMillis()-startTime;
			return result;
		} catch (Throwable e) {
			isException=true;
			logger.error("sql:{} returnAutoGeneratedKeys:{}",sql, returnAutoGeneratedKeys);
			throw new RuntimeException(e.getMessage(),e);
		}finally {
			if(logger.isDebugEnabled()){
				logger.debug("executeUpdate \nisException:{} \nuseTime:{}ms \nresult:{} \nsql:{} \nparameters:{}\n{}",
						isException,
						useTime,
						result,
						sql,
						parameters==null?0:parameters.length,
						dumpParameters(parameters));
			}
			closeSession(session);
			afterExcute(sql, parameters);
			DBStat.stat(sql,useTime,isException);//stat
			if(!isException&&useTime>=SLOW_SQL_MIN_USE_TIME){
				logger.warn("SlowSQL executeUpdate  \nuseTime:{}ms \nresult:{} \nsql:{} \nparameters:{}\n{}",
						useTime,
						result,
						sql,
						parameters==null?0:parameters.length,
						dumpParameters(parameters));
			}
		}
	}
	//
	protected void closeSession(SqlSession session) {
		if(session!=null) {
			session.close();
		}
	}
	//
	protected long executeLargeUpdate(Connection conn,PreparedStatement ps,boolean autoGeneratedKeys)
	throws SQLException{
		ResultSet rs=null;
		try {
			long rowCount = ps.executeLargeUpdate();
			if(autoGeneratedKeys){//
				long autoIncrease = -1;
				rs = ps.getGeneratedKeys();
				if (rs.next()) {
					autoIncrease = rs.getLong(1);
				}
				return autoIncrease;
			}
			return rowCount;
		} catch (SQLException e) {
			throw e;
		}finally {
			close(ps,rs);
		}
	}
	//
	protected long executeLargeUpdate(String sql,boolean returnAutoGeneratedKeys,Object ... parameters){
		try {
			SqlSession session=getSession();
			Connection conn=session.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql.toString(),
					returnAutoGeneratedKeys ? Statement.RETURN_GENERATED_KEYS:Statement.NO_GENERATED_KEYS);
			set(conn, ps, parameters);
			beforeExcute(sql, parameters);
			return executeLargeUpdate(conn, ps,returnAutoGeneratedKeys);
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(),e);
		}finally {
			afterExcute(sql, parameters);
		}
	}
	//
	protected int[] executeBatch(List<Integer> sqlTypeList, String sql, List<Object[]> parameters){
		int[] result=null;
		long startTime=System.currentTimeMillis();
		long useTime=0;
		boolean isException=false;
		SqlSession session=null;
		try {
			session=getSession();
			Connection conn=session.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql.toString(),
					ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			if(parameters!=null){
				for (Object[] parameter : parameters) {
					set(conn, ps, sqlTypeList, parameter);
					ps.addBatch();
				}
			}
			beforeExcute(sql, parameters);
			result=executeBatch(conn,ps);
			useTime=System.currentTimeMillis()-startTime;
			return result;
		} catch (Throwable e) {
			isException=true;
			throw new RuntimeException(e.getMessage(),e);
		}finally {
			if(logger.isDebugEnabled()){
				logger.debug("executeBatch \nisException:{} \nuseTime:{}ms \nresult:{} \nsql:{} \nparameters:{}\n{}",
						isException,
						useTime,
						result,
						sql,
						parameters==null?0:parameters.size(),
						JSONUtil.dump(parameters));
			}
			closeSession(session);
			afterExcute(sql, parameters);
			DBStat.stat(sql,useTime,isException);//stat
			if(!isException&&useTime>=SLOW_SQL_MIN_USE_TIME){
				logger.warn("SlowSQL executeBatch  \nuseTime:{}ms \nresult:{} \nsql:{} \nparameters:{}\n{}",
						useTime,
						result,
						sql,
						parameters==null?0:parameters.size(),
								JSONUtil.dump(parameters));
			}
		}
	}
	//
	public long largeInsert(String sql,boolean returnAutoGeneratedKeys,Object ... parameters){
		return executeLargeUpdate(sql, returnAutoGeneratedKeys, parameters);
	}
	//
	public int insert(String sql,boolean returnAutoGeneratedKeys,Object ... parameters){
		return executeUpdate0(sql, returnAutoGeneratedKeys, parameters);
	}
	//
	protected int executeUpdate(String sql,Object ... parameters){
		return executeUpdate0(sql,false,parameters);
	}
	//
	protected <T> T queryForObject(String sql,ResultSetHandler<T> handler,Object ... parameters){
		T bean=null;
		boolean isException=false;
		long startTime=System.currentTimeMillis();
		long useTime=0;
		String errorMessage=null;
		SqlSession session=null;
		try {
			SmartJdbcUtils.checkSqlValidity(sql);
			session=getSession();
			Connection conn=session.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql.toString(),
					ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			set(conn, ps, parameters);
			beforeExcute(sql, parameters);
			bean=query(conn, ps, handler);
			useTime=System.currentTimeMillis()-startTime;
			return bean;
		} catch (Throwable e) {
			isException=true;
			errorMessage=e.getMessage();
			throw new RuntimeException(e.getMessage(),e);
		}finally {
			if(isException) {
				logger.warn("queryForObject errorMessag:{}\nuseTime:{}ms \nsql:{}\nparameters:{}\nresult:{}",
						errorMessage,
						useTime,
						sql,
						dumpParameters(parameters),
					bean
					);
			}else {
				if(logger.isDebugEnabled()){
					logger.debug("queryForObject {}\nisException:{} \nuseTime:{}ms \nsql:{} \nparameters:{}\nresult:{}",
							bean==null?"":bean.getClass().getSimpleName(),
							isException,
							useTime,
							sql,
							dumpParameters(parameters),
							bean);
				}
			}
			closeSession(session);
			afterExcute(sql, parameters);
			DBStat.stat(sql,useTime,isException);//stat
			if(!isException&&useTime>=SLOW_SQL_MIN_USE_TIME){
				logger.warn("SlowSQL query \nisException:{} \nuseTime:{}ms \nsql:{} \nparameters:{} \nresult:{}",
						isException,
						useTime,
						sql,
						dumpParameters(parameters),
						bean);
			}
		}
	}
	//
	protected <T> List<T> queryForList(String sql,ResultSetHandler<T> handler,Object ... parameters){
		List<T> list=null;
		boolean isException=false;
		String errorMessage=null;
		long startTime=System.currentTimeMillis();
		long useTime=0;
		SqlSession session=null;
		try {
			SmartJdbcUtils.checkSqlValidity(sql);
			session=getSession();
			Connection conn=session.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql.toString(),
					ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			set(conn, ps, parameters);
			beforeExcute(sql, parameters);
			list=queryList(conn, ps, handler);
			useTime=System.currentTimeMillis()-startTime;
			return list;
		} catch (Throwable e) {
			isException=true;
			errorMessage=e.getMessage();
			throw new RuntimeException(e.getMessage(),e);
		}finally {
			if(isException) {
				logger.warn("queryList errorMessage:{}\nuseTime:{}ms \nsql:{}\nparameters:{}\nresult:{}",
						errorMessage,
						useTime,
						sql,
						dumpParameters(parameters),
						list==null?0:list.size()
						);
			}else {
				if(logger.isDebugEnabled()){
					logger.debug("queryList \nisException:{} \nuseTime:{}ms \nsql:{}\nparameters:{}\nresult:{}",
							isException,
							useTime,
							sql,
							dumpParameters(parameters),
							list==null?0:list.size()
							);
				}
			}
			closeSession(session);
			afterExcute(sql, parameters);
			DBStat.stat(sql,useTime,isException);//stat
			if(!isException&&useTime>=SLOW_SQL_MIN_USE_TIME){
				logger.warn("SlowSQL queryList \nisException:{} \nuseTime:{}ms \nsql:{}\nparameters:{}\nresult:{}",
						isException,
						useTime,
						sql,
						dumpParameters(parameters),
						list==null?0:list.size()
						);
			}
		}
	}
	//
	protected String dumpParameters(Object[] parameters) {
		return JSONUtil.dump(parameters, SerializerFeature.DisableCircularReferenceDetect);
	}
	//
	protected <T> T query(Connection conn,PreparedStatement ps,ResultSetHandler<T> handler) 
	throws Exception{
		ResultSet rs=null;
		try {
			rs = ps.executeQuery();
			if(rs!=null){
				while(rs.next()){
					return handler.handleRow(rs); 
				}
			}
			return null;
		} finally {
			close(ps,rs);
		}
	}
	//
	protected <T> List<T> queryList(Connection conn,PreparedStatement ps,ResultSetHandler<T> handler) 
	throws Exception{
		ResultSet rs=null;
		try {
			rs = ps.executeQuery();
			List<T> list=new ArrayList<>();
			if(rs!=null){
				while(rs.next()){
					list.add(handler.handleRow(rs)); 
				}
			}
			return list;
		} finally {
			close(ps,rs);
		}
	}
	
	//
	protected boolean queryForBoolean(String sql,Object ...parameters){
		return  queryForObject(sql,rs->rs.getBoolean(1), parameters);
	}
	//
	protected String queryForString(String sql,Object ...parameters){
		return  queryForObject(sql,rs->rs.getString(1), parameters);
	}
	//
	protected double queryForDouble(String sql,Object ...parameters){
		return  queryForObject(sql,rs->rs.getDouble(1), parameters);
	}
	//
	protected float queryForFloat(String sql,Object ...parameters){
		return  queryForObject(sql,rs->rs.getFloat(1), parameters);
	}
	//
	protected Integer queryForInteger(String sql,Object ...parameters){
		return  (Integer) queryForObject(sql,rs->rs.getObject(1), parameters);
	}
	//
	protected int queryForInt(String sql,Object ...parameters){
		return  queryForObject(sql,rs->rs.getInt(1), parameters);
	}
	//
	protected long queryForLong(String sql,Object ...parameters){
		return  queryForObject(sql,rs->rs.getLong(1), parameters);
	}
	//
	protected short queryForShort(String sql,Object ...parameters){
		return  queryForObject(sql,rs->rs.getShort(1), parameters);
	}
	//
	protected BigDecimal queryForBigDecimal(String sql,Object ...parameters){
		return  queryForObject(sql,rs->rs.getBigDecimal(1), parameters);
	}
	//
	protected byte queryForByte(String sql,Object ...parameters){
		return  queryForObject(sql,rs->rs.getByte(1), parameters);
	}
	//
	protected Date queryForDate(String sql,Object ...parameters){
		return  queryForObject(sql,rs->rs.getTimestamp(1), parameters);
	}
	//
	protected List<Boolean> queryForBooleans(String sql,Object ...parameters){
		return  queryForList(sql,rs->rs.getBoolean(1), parameters);
	}
	//
	protected List<String> queryForStrings(String sql,Object ...parameters){
		return  queryForList(sql,rs->rs.getString(1), parameters);
	}
	//
	protected List<Double> queryForDoubles(String sql,Object ...parameters){
		return  queryForList(sql,rs->rs.getDouble(1), parameters);
	}
	//
	protected List<Float> queryForFloats(String sql,Object ...parameters){
		return  queryForList(sql,rs->rs.getFloat(1), parameters);
	}
	//
	protected List<Integer> queryForIntegers(String sql,Object ...parameters){
		return  queryForList(sql,rs->rs.getInt(1), parameters);
	}
	//
	protected List<Long> queryForLongs(String sql,Object ...parameters){
		return  queryForList(sql,rs->rs.getLong(1), parameters);
	}
	//
	protected List<Short> queryForShorts(String sql,Object ...parameters){
		return  queryForList(sql,rs->rs.getShort(1), parameters);	
	}
	//
	protected List<BigDecimal> queryForBigDecimals(String sql,Object ...parameters){
		return  queryForList(sql,rs->rs.getBigDecimal(1), parameters);
	}
	//
	protected List<Byte> queryForBytes(String sql,Object ...parameters){
		return  queryForList(sql,rs->rs.getByte(1), parameters);
	}
	//
	protected List<Date> queryForDates(String sql,Object ...parameters){
		return  queryForList(sql,rs->rs.getTimestamp(1), parameters);
	}
	//
	public String getDatasourceIndex() {
		return datasourceIndex;
	}
	//
	public DatabaseType getDatabaseType() {
		return getSmartDataSource().getDatabaseType();
	}
	//
	public SmartDataSource getSmartDataSource() {
		return SmartJdbc.getDatasource(getDatasourceIndex());
	}
	//
	public SqlSession getSession() throws SQLException {
		return getSmartDataSource().getSession();
	}
	//
	public void close(Statement stmt,ResultSet rs) throws SQLException {
		close(rs);
		close(stmt);
	}
	//
	/**
	 * 
	 * @param stmt
	 */
	private void close(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			}
			catch (SQLException ex) {
				logger.trace("Could not close JDBC Statement", ex);
			}
			catch (Throwable ex) {
				logger.trace("Unexpected exception on closing JDBC Statement", ex);
			}
		}
	}
	
	/**
	 * 
	 * @param rs
	 */
	private void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			}
			catch (SQLException ex) {
				logger.trace("Could not close JDBC ResultSet", ex);
			}
			catch (Throwable ex) {
				logger.trace("Unexpected exception on closing JDBC ResultSet", ex);
			}
		}
	}
	//
	public List<SqlInterceptor> getSqlInterceptors() {
		return getSmartDataSource().getSqlInterceptors();
	}
	//
	protected void set(Connection conn,
			PreparedStatement ps, 
			Object... objs) throws SQLException {
		set(conn, ps, null, objs);
	}
	//
	protected void set(Connection conn,
			PreparedStatement ps, 
			List<Integer> sqlTypeList,
			Object... objs)
			throws SQLException {
		if (objs == null || objs.length == 0) {
			return;
		}
		int i = 1;
		for (Object o : objs) {
			if (o == null) {
				if(sqlTypeList!=null) {
					int sqlType=sqlTypeList.get(i-1);
					ps.setNull(i++, sqlType);
				}else {
					ps.setObject(i++, null);
				}
				continue;
			}
			Class<?> type=o.getClass();
			if (o instanceof String) {
				ps.setString(i++, ((String) o));
				continue;
			}
			if (o instanceof Date) {
				Date date = (Date) o;
				ps.setTimestamp(i++, new Timestamp(date.getTime()));
				continue;
			}
			// Integer
			if (o instanceof Integer) {
				ps.setInt(i++, ((Integer) o));
				continue;
			}
			if (o instanceof Double) {
				ps.setDouble(i++, ((Double) o));
				continue;
			}
			if (o instanceof Float) {
				ps.setFloat(i++, ((Float) o));
				continue;
			}
			if (o instanceof BigDecimal) {
				ps.setBigDecimal(i++, ((BigDecimal) o));
				continue;
			}
			if (o instanceof Long) {
				ps.setLong(i++, ((Long) o));
				continue;
			}
			if (o instanceof Short) {
				ps.setShort(i++, ((Short) o));
				continue;
			}
			if (o instanceof Byte) {
				ps.setByte(i++, ((Byte) o));
				continue;
			}
			if (o instanceof byte[]) {
				ps.setBytes(i++, ((byte[]) o));
				continue;
			}
			if (o instanceof Boolean) {
				ps.setBoolean(i++, ((Boolean) o));
				continue;
			}
			if(o instanceof Ltree) {
				ps.setObject(i++, ((Ltree)o).path, java.sql.Types.OTHER);
				continue;
			}
			if (Types.ARRAY_TYPES.contains(type)) {
				if(type.equals(String[].class)) {
					ps.setArray(i++, conn.createArrayOf("TEXT",(Object[]) o));
				}
				else if(type.equals(Short[].class)) {
					ps.setArray(i++, conn.createArrayOf("INTEGER",(Object[]) o));
				}
				else if(type.equals(Integer[].class)) {
					ps.setArray(i++, conn.createArrayOf("INTEGER",(Object[]) o));
				}
				else if(type.equals(Long[].class)) {
					ps.setArray(i++, conn.createArrayOf("BIGINT",(Object[]) o));
				}
				else if(type.equals(Float[].class)) {
					ps.setArray(i++, conn.createArrayOf("FLOAT",(Object[]) o));
				}
				else if(type.equals(Double[].class)) {
					ps.setArray(i++, conn.createArrayOf("DOUBLE",(Object[]) o));
				}
				else if(type.equals(Object[].class)) {
					ps.setArray(i++, conn.createArrayOf("TEXT",(Object[]) o));
				}
				continue;
			}
			throw new IllegalArgumentException("unsupport type:" + o.getClass());
		}
	}
}
