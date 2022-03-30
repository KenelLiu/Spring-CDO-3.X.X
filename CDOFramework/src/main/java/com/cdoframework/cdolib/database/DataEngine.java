package com.cdoframework.cdolib.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cdo.field.Field;
import com.cdo.field.FieldType;
import com.cdo.util.sql.AnalyzedPreparedStatementSQL;
import com.cdo.util.sql.SQLUtil;
import com.cdoframework.cdolib.data.cdo.CDO;
import com.cdoframework.cdolib.data.cdo.CDOArrayField;

/**
 * 
 * @author Kenel
 *
 */
public class DataEngine implements IDataEngine{
	
	// 内部对象,所有在本类中创建并使用的对象在此声明--------------------------------------------------------------
	protected DBPool dbPool;   
	private static Log logger=LogFactory.getLog(DataEngine.class);

	/**
	 * 获取一个数据库连接
	 * 
	 * @return
	 */
	public Connection getConnection() throws SQLException
	{
		if(!dbPool.isOpened()){
			return null;
		}
		return dbPool.getConnection();
	}
	public void setDBPool(DBPool dbPool){
		this.dbPool=dbPool;
	}
	
	public DBPool getDBPool(){
		return this.dbPool;
	}
	
  //==============================连接池相关 END========================//
	
	
	// 引用对象,所有在外部创建并传入使用的对象在此声明并提供set方法-----------------------------------------------

	// 内部方法,所有仅在本类或派生类中使用的函数在此定义为protected方法-------------------------------------------
	protected void callOnException(String strText,AnalyzedPreparedStatementSQL analyzedSQL,CDO cdoRequest,Exception e)
	{
		try{
			onException(strText,analyzedSQL,cdoRequest,e);
		}catch(Exception ex){
		}
	}
	/**
	 * 
	 * @param conn
	 * @param strSourceSQL 含有{}变量符的原始SQL
	 * @param cdoRequest
	 * @return
	 * @throws SQLException
	 */
	@Override
	public AnalyzedPreparedStatementSQL prepareStatement(Connection conn,String strSourceSQL,CDO cdoRequest) throws SQLException{
		String strCharset=this.dbPool.getCharset();//系统字符编码
		return SQLUtil.analyzedSQL(conn, strSourceSQL, cdoRequest, strCharset);
	}
	
	/**
	 * 读取当前的记录数据
	 * @param rs
	 * @param strsFieldName 字段名 @ResultSetMetaData.getColumnLabel(i+1)
	 * @param naFieldType 字段类型 @ResultSetMetaData..getColumnType(i+1)
	 * @param nsPrecision @ResultSetMetaData.getPrecision(i+1)
	 * @param nsScale @ResultSetMetaData.getScale(i+1);
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public CDO readRecord(ResultSet rs,String[] strsFieldName,int[] naFieldType,int[] nsPrecision,int[] nsScale) throws SQLException,IOException{
		CDO cdoRecord=new CDO();
		if(readRecord(rs,strsFieldName,naFieldType,nsPrecision,nsScale,cdoRecord)==0){
			return null;
		}
		return cdoRecord;
	}

	/**
	 * 读取当前的记录数据
	 * @param rs
	 * @param strsFieldName 字段名 @ResultSetMetaData.getColumnLabel(i+1)
	 * @param naFieldType 字段类型 @ResultSetMetaData..getColumnType(i+1)
	 * @param nsPrecision @ResultSetMetaData.getPrecision(i+1)
	 * @param nsScale @ResultSetMetaData.getScale(i+1);
	 * @param cdoRecord
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public int readRecord(ResultSet rs,String[] strsFieldName,int[] naFieldType,int[] nsPrecision,int[] nsScale,CDO cdoRecord) throws SQLException,IOException
	{
		String strCharset=this.dbPool.getCharset();//系统字符编码
		return SQLUtil.readRecord(rs, strsFieldName, naFieldType, nsPrecision, nsScale, cdoRecord, strCharset);
	}

	/**
	 * 查询并输出第一条记录的第一个字段
	 * @param conn
	 * @param strSourceSQL 含有{}变量符的原始SQL
	 * @param cdoRequest
	 * @param cdoResponse
	 * @return
	 * @throws Exception
	 */
	public Field executeQueryField(Connection conn,String strSourceSQL,CDO cdoRequest) throws SQLException,IOException{
		// 准备JDBC语句
		AnalyzedPreparedStatementSQL analyzedSQL=prepareStatement(conn,strSourceSQL,cdoRequest);
		PreparedStatement ps=analyzedSQL.ps;
		// 输出查询结果
		ResultSet rs=null;
		try{
			// 执行查询
			rs=ps.executeQuery();
			// 读取记录信息
			ResultSetMetaData meta=rs.getMetaData();
			String[] strsFieldName=new String[1];
			int[] nsFieldType=new int[1];
			int[] nsPrecision=new int[1];
			int[] nsScale=new int[1];
			for(int i=0;i<strsFieldName.length;i++)
			{
				strsFieldName[i]=meta.getColumnLabel(i+1);
				nsFieldType[i]=meta.getColumnType(i+1);
				nsPrecision[i]=meta.getPrecision(i+1);
				nsScale[i]=meta.getScale(i+1);
			}

			CDO cdoRecord=readRecord(rs,strsFieldName,nsFieldType,nsPrecision,nsScale);
			if(cdoRecord==null){
				return null;
			}
			// 输出
			if(cdoRecord.exists(strsFieldName[0]))
			{
				return cdoRecord.getObject(strsFieldName[0]);
			}
			else
			{
				return null;
			}
		}catch(SQLException e){
			callOnException("executeQueryField Exception: "+strSourceSQL,analyzedSQL,cdoRequest,e);
			throw e;
		}finally{
			analyzedSQL=null;
			SQLUtil.closeResultSet(rs);
			SQLUtil.closePreparedStatement(ps);
		}
	}

	
	/**
	 * 查询并输出第一条记录
	 * 
	 * @param conn
	 * @param strSourceSQL 含有{}变量符的原始SQL
	 * @param cdoRequest
	 * @param cdoResponse
	 * @return
	 * @throws Exception
	 */
	public int executeQueryRecord(Connection conn,String strSourceSQL,CDO cdoRequest,CDO cdoResponse) throws SQLException,
					IOException
	{
		// 准备JDBC语句 执行sql查询记录
		AnalyzedPreparedStatementSQL analyzedSQL=prepareStatement(conn,strSourceSQL,cdoRequest);
		PreparedStatement ps=analyzedSQL.ps;
		// 输出查询结果
		ResultSet rs=null;
		try{
			// 执行查询
			rs=ps.executeQuery();
			ResultSetMetaData meta=rs.getMetaData();
			String[] strsFieldName=new String[meta.getColumnCount()];
			int[] nsFieldType=new int[strsFieldName.length];
			int[] nsPrecision=new int[strsFieldName.length];
			int[] nsScale=new int[strsFieldName.length];
			for(int i=0;i<strsFieldName.length;i++)
			{
				/**支持JDBC4**/
				strsFieldName[i]=meta.getColumnLabel(i+1);
				nsFieldType[i]=meta.getColumnType(i+1);
				nsPrecision[i]=meta.getPrecision(i+1);
				nsScale[i]=meta.getScale(i+1);
			}
			// 读取记录信息
			int nRecordCount=readRecord(rs,strsFieldName,nsFieldType,nsPrecision,nsScale,cdoResponse);
			//统计查询
			//int nCount=executeCount(conn, strSourceSQL, cdoRequest);
			return nRecordCount;
			
		}catch(SQLException e){
			callOnException("executeQueryRecord Exception: "+strSourceSQL,analyzedSQL,cdoRequest,e);
			throw e;
		}finally{
			analyzedSQL=null;
			SQLUtil.closeResultSet(rs);
			SQLUtil.closePreparedStatement(ps);
		}
	}

	/**
	 * 查询并输出所有记录
	 * 
	 * @param conn
	 * @param strSourceSQL 含有{}变量符的原始SQL
	 * @param cdoRequest
	 * @param cafRecordSet
	 * @return
	 * @throws Exception
	 */
	public int executeQueryRecordSet(Connection conn,String strSourceSQL,CDO cdoRequest,CDOArrayField cdoArrayField)
					throws SQLException,IOException
	{
		// 准备JDBC语句 执行记录查询
		AnalyzedPreparedStatementSQL analyzedSQL=prepareStatement(conn,strSourceSQL,cdoRequest);
		PreparedStatement ps=analyzedSQL.ps;		
		// 输出查询结果
		ResultSet rs=null;
		try{
			// 执行查询
			rs=ps.executeQuery();
			// 读取Meta信息
			ResultSetMetaData meta=rs.getMetaData();
			String[] strsFieldName=new String[meta.getColumnCount()];
			int[] nsFieldType=new int[strsFieldName.length];
			int[] nsPrecision=new int[strsFieldName.length];
			int[] nsScale=new int[strsFieldName.length];
		
			for(int i=0;i<strsFieldName.length;i++){
				
				strsFieldName[i]=meta.getColumnLabel(i+1);
				nsFieldType[i]=meta.getColumnType(i+1);
				nsPrecision[i]=meta.getPrecision(i+1);
				nsScale[i]=meta.getScale(i+1);
			}
			// 读取记录
			ArrayList<CDO> alRecord=new ArrayList<CDO>();
			while(true)
			{
				// 读取记录信息
				CDO cdoRecord=readRecord(rs,strsFieldName,nsFieldType,nsPrecision,nsScale);
				if(cdoRecord==null)
				{
					break;
				}
				alRecord.add(cdoRecord);
			}
			cdoArrayField.setValue(alRecord);		
			return alRecord.size();
		}catch(SQLException e){
			callOnException("executeQueryRecordSet Exception: "+strSourceSQL,analyzedSQL,cdoRequest,e);
			throw e;
		}finally{
			analyzedSQL=null;
			SQLUtil.closeResultSet(rs);
			SQLUtil.closePreparedStatement(ps);
		}
	}	
	
	/**
	 *  执行数据库插入,更新,删除语句
	 * 
	 * @param conn
	 * @param strSourceSQL 含有{}变量符的原始SQL
	 * @param cdoRequest
	 * @return
	 * @throws Exception
	 */
	public int executeUpdate(Connection conn,String strSourceSQL,CDO cdoRequest) throws SQLException
	{
		// 准备JDBC语句
		AnalyzedPreparedStatementSQL analyzedSQL=prepareStatement(conn,strSourceSQL,cdoRequest);
		PreparedStatement ps=analyzedSQL.ps;
		// 输出查询结果
		try{
			return ps.executeUpdate();
		}catch(SQLException e){
			callOnException("executeUpdate Exception: "+strSourceSQL,analyzedSQL,cdoRequest,e);
			throw e;
		}finally{
			analyzedSQL=null;
			SQLUtil.closePreparedStatement(ps);
		}
	}
	
	/**
	 * 执行数据库插入,更新,删除语句
	 * 
	 * @param conn
	 * @param strSourceSQL 含有{}变量符的原始SQL
	 * @param cdoRequest
	 * @return
	 * @throws Exception
	 */
	public void executeSQL(Connection conn,String strSourceSQL,CDO cdoRequest) throws SQLException
	{
		// 准备JDBC语句
		AnalyzedPreparedStatementSQL analyzedSQL=prepareStatement(conn,strSourceSQL,cdoRequest);
		PreparedStatement ps=analyzedSQL.ps;
		try{
			ps.execute();
		}catch(SQLException e){
			callOnException("executeUpdate Exception: "+strSourceSQL,analyzedSQL,cdoRequest,e);
			throw e;
		}finally{
			analyzedSQL=null;
			SQLUtil.closePreparedStatement(ps);
		}
	}	

	
	// 事件定义,所有在本类中定义并调用，由派生类实现或重载的事件类方法(一般为on...ed)在此定义---------------------
	public void onException(String strText,AnalyzedPreparedStatementSQL analyzedSQL,CDO cdoRequest,Exception e)
	{
		if(analyzedSQL==null || analyzedSQL.anaSQL==null){
			logger.error("Database Exception: "+strText+"\r\n e:"+e);
			return ;
		}
		String strSQL=analyzedSQL.anaSQL.strSQL;
		ArrayList<String> alParaName=analyzedSQL.anaSQL.alParaName;		
		StringBuilder val = new StringBuilder("{");
		if(alParaName!=null){
			for (int i = 0; i < alParaName.size(); i++) {
				Field object = cdoRequest.getObject(alParaName.get(i));
				Object objValue = object.getObjectValue();
				int nType = object.getFieldType().getType();				
				if(i>0){
					val.append(",");
				}
				val.append(nType==FieldType.BYTE_ARRAY_TYPE?new String((byte[]) objValue):objValue);				
			}						
		}
		val.append('}');
		logger.error("Database Exception: "+strText+"\r\n analyzedSQL strSQL:"+strSQL+"\r\n "+val+"\r\n e:"+e);
	}


	// 构造函数,所有构造函数在此定义------------------------------------------------------------------------------

	public DataEngine()
	{

		//strSystemCharset=System.getProperty("sun.jnu.encoding");
	}
	
}
