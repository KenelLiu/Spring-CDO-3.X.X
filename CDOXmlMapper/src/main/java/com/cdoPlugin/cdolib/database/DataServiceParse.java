package com.cdoPlugin.cdolib.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.cdo.field.Field;
import com.cdo.field.FieldType;
import com.cdo.spring.SpringContextUtil;
import com.cdoPlugin.cdolib.database.xsd.BlockType;
import com.cdoPlugin.cdolib.database.xsd.BlockTypeItem;
import com.cdoPlugin.cdolib.database.xsd.Case;
import com.cdoPlugin.cdolib.database.xsd.CaseNull;
import com.cdoPlugin.cdolib.database.xsd.Default;
import com.cdoPlugin.cdolib.database.xsd.Delete;
import com.cdoPlugin.cdolib.database.xsd.Else;
import com.cdoPlugin.cdolib.database.xsd.For;
import com.cdoPlugin.cdolib.database.xsd.If;
import com.cdoPlugin.cdolib.database.xsd.Insert;
import com.cdoPlugin.cdolib.database.xsd.NullSQLThen;
import com.cdoPlugin.cdolib.database.xsd.NullThen;
import com.cdoPlugin.cdolib.database.xsd.OnException;
import com.cdoPlugin.cdolib.database.xsd.ResetPage;
import com.cdoPlugin.cdolib.database.xsd.SQLBlockType;
import com.cdoPlugin.cdolib.database.xsd.SQLBlockTypeItem;
import com.cdoPlugin.cdolib.database.xsd.SQLCase;
import com.cdoPlugin.cdolib.database.xsd.SQLCaseNull;
import com.cdoPlugin.cdolib.database.xsd.SQLDefault;
import com.cdoPlugin.cdolib.database.xsd.SQLElse;
import com.cdoPlugin.cdolib.database.xsd.SQLFor;
import com.cdoPlugin.cdolib.database.xsd.SQLIf;
import com.cdoPlugin.cdolib.database.xsd.SQLSwitch;
import com.cdoPlugin.cdolib.database.xsd.SQLThen;
import com.cdoPlugin.cdolib.database.xsd.SQLTrans;
import com.cdoPlugin.cdolib.database.xsd.SQLTransChoiceItem;
import com.cdoPlugin.cdolib.database.xsd.SelectField;
import com.cdoPlugin.cdolib.database.xsd.SelectRecord;
import com.cdoPlugin.cdolib.database.xsd.SelectRecordSet;
import com.cdoPlugin.cdolib.database.xsd.SelectTable;
import com.cdoPlugin.cdolib.database.xsd.SetVar;
import com.cdoPlugin.cdolib.database.xsd.Switch;
import com.cdoPlugin.cdolib.database.xsd.Then;
import com.cdoPlugin.cdolib.database.xsd.Update;
import com.cdoframework.cdolib.base.Return;
import com.cdoframework.cdolib.data.cdo.CDO;
import com.cdoframework.cdolib.data.cdo.CDOArrayField;

/**
* @author KenelLiu
*/
public class DataServiceParse
{

	//内部类,所有内部类在此声明----------------------------------------------------------------------------------

	//静态对象,所有static在此声明并初始化------------------------------------------------------------------------	
	private static Log logger=LogFactory.getLog(DataServiceParse.class);
	/**
	 * 处理SQL语句中的If语句
	 * 
	 * @param sqlIf
	 * @param cdoRequest
	 * @return 0-自然执行完毕，1-碰到Break退出，2-碰到Return退出
	 * @throws Exception
	 */
	private int handleSQLIf(SQLIf sqlIf,CDO cdoRequest,StringBuilder strbSQL,Map<String,String> selTblMap)
	{
		//============处理NullThen========//
		NullSQLThen nullSqlThen=sqlIf.getNullSQLThen();
		if(nullSqlThen!=null){
			//========判断是否存在========//
			String strValue1=sqlIf.getValue1();
			strValue1=strValue1.substring(1,strValue1.length()-1);
			boolean isExist=cdoRequest.exists(strValue1);	
			if(isExist){
				//判断是否为NullField
				if(cdoRequest.getField(strValue1).getFieldType()==FieldType.type.NULL){
					isExist=false;
				}
			}
			if(!isExist){
				return handleSQLBlock(nullSqlThen,cdoRequest,strbSQL,selTblMap);
			}
		}
		//=============其它正常情况.检查执行条件==========//
		boolean bCondition=DataEngineHelp.checkCondition(sqlIf,cdoRequest);
		if(bCondition==true)
		{// Handle Then
			SQLThen sqlThen=sqlIf.getSQLThen();
			if(sqlThen==null){
				return 0;
			}
			return handleSQLBlock(sqlThen,cdoRequest,strbSQL,selTblMap);
		}
		else
		{// handle Else
			SQLElse sqlElse=sqlIf.getSQLElse();
			if(sqlElse==null)
			{// 自然完成
				return 0;
			}
			return handleSQLBlock(sqlElse,cdoRequest,strbSQL,selTblMap);
		}			
	}
	/**
	 * 处理SQL语句中的For语句
	 * 
	 * @param sqlFor
	 * @param cdoRequest
	 * @param strbSQL
	 * @return 0-自然执行完毕，1-碰到Break退出，2-碰到Return退出
	 * @throws Exception
	 */
	protected int handleSQLFor(SQLFor sqlFor,CDO cdoRequest,StringBuilder strbSQL,Map<String,String> selTblMap)
	{
		// 获取循环数据
		int nFromIndex=0;
		int nStep=1;
		int nCount=DataEngineHelp.getArrayLength(sqlFor.getArrKey(), cdoRequest);
		if(sqlFor.getFromIndex()!=null)
			nFromIndex=DataEngineHelp.getIntegerValue(sqlFor.getFromIndex(),cdoRequest);
		if(sqlFor.getStep()!=null)
			nStep=DataEngineHelp.getIntegerValue(sqlFor.getStep(),cdoRequest);
		if(sqlFor.getCount()!=null)
			nCount=DataEngineHelp.getIntegerValue(sqlFor.getCount(),cdoRequest);	
					
		String strIndexId=sqlFor.getIndexId();
		strIndexId=strIndexId.substring(1,strIndexId.length()-1);

		// 执行循环
		for(int i=nFromIndex;i<nFromIndex+nCount;i=nStep+i)
		{
			// 设置IndexId
			cdoRequest.setIntegerValue(strIndexId,i);

			// 执行Block
			int nResult=handleSQLBlock(sqlFor,cdoRequest,strbSQL,selTblMap);
			if(nResult==0)
			{// 自然执行完毕
				continue;
			}
			else if(nResult==1)
			{// 碰到Break
				break;
			}
			else
			{// 碰到Return
				return nResult;
			}
		}

		return 0;
	}
	/**
	 * 处理SQLBlock对象，得到输出的SQL语句
	 * 
	 * @param sqlBlock
	 * @return 0-自然执行完毕，1-碰到Break退出，2-碰到Return退出
	 */
	private int handleSQLSwitch(SQLSwitch sqlSwitch,CDO cdoRequest,StringBuilder strbSQL,Map<String,String> selTblMap){
		String var=sqlSwitch.getVar();		
		var=var.substring(1,var.length()-1);
		//=========处理为CaseNull的情况========//
		SQLCaseNull caseNull=sqlSwitch.getSQLCaseNull();
		if(caseNull!=null){
			//判断是否存在
			boolean isExist=cdoRequest.exists(var);	
			if(isExist){
				//判断是否为NullField
				if(cdoRequest.getField(var).getFieldType()==FieldType.type.NULL){
					isExist=false;
				}
			}
			if(!isExist)
				return handleSQLBlock(caseNull,cdoRequest,strbSQL,selTblMap);
		}
		//===========处理Case情况==========//
		String varValue=cdoRequest.getObjectValue(var).toString();		
		SQLCase[] cases=sqlSwitch.getSQLCase();
		if(cases!=null && cases.length>0){
			for(int i=0;i<cases.length;i++){
				String value=cases[i].getValue();
				if(value.startsWith("{") && value.endsWith("}")){
					value=value.substring(1,value.length()-1);
					value=cdoRequest.getObjectValue(value).toString();
				}
				//========值有分隔符===========//
				String separate=cases[i].getSeparate();
				if(separate!=null && !separate.equals("")){
					String[] args=value.split(separate);
					for(int k=0;k<args.length;k++){
						if(varValue.equals(args[k])){														
							return handleSQLBlock(cases[i],cdoRequest,strbSQL,selTblMap);					
						}	
					}
				}else{
					//========值没有分隔符=========//
					if(varValue.equals(value)){													
						return handleSQLBlock(cases[i],cdoRequest,strbSQL,selTblMap);			
					}	
				}					
				
			}
		}			
		//===========处理Case default情况==========//
		SQLDefault defaults=sqlSwitch.getSQLDefault();
		if(defaults!=null){			
			return handleSQLBlock(defaults,cdoRequest,strbSQL,selTblMap);					
		}			
		return 0;
	}
	/**
	 * 处理SQLBlock对象，得到输出的SQL语句
	 * 
	 * @param sqlBlock
	 * @return 0-自然执行完毕，1-碰到Break退出，2-碰到Return退出
	 */
	protected int handleSQLBlock(SQLBlockType sqlBlock,CDO cdoRequest,StringBuilder strbSQL,Map<String,String> selTblMap)
	{
		// 依次处理各个Item
		int nItemCount=sqlBlock.getSQLBlockTypeItemCount();
		for(int i=0;i<nItemCount;i++)
		{
			// 处理当前的Item
			SQLBlockTypeItem item=sqlBlock.getSQLBlockTypeItem(i);
			if(item.getOutputSQL()!=null)
			{// OutputSQL,直接输出源文本
				strbSQL.append(item.getOutputSQL());
			}
			else if(item.getOutputField()!=null)
			{// OutputField，输出文本代表的字段值
				String strOutputFieldId=item.getOutputField();
				strOutputFieldId=strOutputFieldId.substring(1,strOutputFieldId.length()-1);
				strbSQL.append(cdoRequest.getStringValue(strOutputFieldId));
			}
			else if(item.getOutputTable()!=null){
				// OutputTable，输出文本代表的字段值
				String outTblId=item.getOutputTable();
				outTblId=outTblId.substring(1,outTblId.length()-1);
				if(selTblMap==null || selTblMap.get(outTblId)==null){					
					throw new RuntimeException("在使用OutputTable前,未定义SelectTable.当前未找到["+outTblId+"]对应的SelectTable");
				}else{
					strbSQL.append(selTblMap.get(outTblId));
				}
			}
			else if(item.getSQLIf()!=null)
			{// SQLIf
				int nResult=handleSQLIf(item.getSQLIf(),cdoRequest,strbSQL,selTblMap);
				if(nResult==0)
				{// 自然执行完毕
					continue;
				}
				else
				{// 碰到Break或Return
					return nResult;
				}
			}
			else if(item.getSQLFor()!=null)
			{// SQLFor
				int nResult=handleSQLFor(item.getSQLFor(),cdoRequest,strbSQL,selTblMap);
				if(nResult==0)
				{// 自然执行完毕
					continue;
				}
				else
				{// 碰到Break或Return
					return nResult;
				}				
			}else if(item.getSQLSwitch()!=null){
				//SQLSwitch
				int nResult=handleSQLSwitch(item.getSQLSwitch(),cdoRequest,strbSQL,selTblMap);
				if(nResult==0)
				{// 自然执行完毕
					continue;
				}
				else
				{// 碰到Break或Return
					return nResult;
				}
			}
			else
			{
				continue;
			}
		}

		// 自然执行完毕
		return 0;
	}
	
	/**
	 * 处理If语句
	 * 
	 * @param ifItem
	 * @param cdoRequest
	 * @return 0-自然执行完毕，1-碰到Break退出，2-碰到Return退出
	 * @throws Exception
	 */
	private int handleIf(Connection connection,If ifItem,CDO cdoRequest,CDO cdoResponse,Return ret,Map<String,String> selTblMap) throws SQLException,IOException
	{
		//==============检查NullThen====================//
		NullThen nullThen=ifItem.getNullThen();
		if(nullThen!=null ){
			//========判断是否存在========//
			String strValue1=ifItem.getValue1();
			strValue1=strValue1.substring(1,strValue1.length()-1);
			boolean isExist=cdoRequest.exists(strValue1);	
			if(isExist){
				//判断是否为NullField
				if(cdoRequest.getField(strValue1).getFieldType()==FieldType.type.NULL){
					isExist=false;
				}
			}
			if(!isExist)
				return handleBlock(connection,nullThen,cdoRequest,cdoResponse,ret,selTblMap);
		}
		//============检查其它正常执行条件===============//
		boolean bCondition=DataEngineHelp.checkCondition(ifItem,cdoRequest);
		if(bCondition==true)
		{// Handle Then
			Then thenItem=ifItem.getThen();
			if(thenItem==null){
				return 0;
			}
			return handleBlock(connection,thenItem,cdoRequest,cdoResponse,ret,selTblMap);
		}
		else
		{// handle Else
			Else elseItem=ifItem.getElse();
			if(elseItem==null){
				return 0;
			}
			return handleBlock(connection,elseItem,cdoRequest,cdoResponse,ret,selTblMap);
		}
	}
	/**
	 * 处理For语句
	 * 
	 * @param sqlFor
	 * @param cdoRequest
	 * @param strbSQL
	 * @return 0-自然执行完毕，1-碰到Break退出，2-碰到Return退出
	 * @throws Exception
	 */
	private int handleFor(Connection connection,For forItem,CDO cdoRequest,CDO cdoResponse,Return ret,Map<String,String> selTblMap) throws SQLException,IOException
	{
		// 获取循环数据
		int nFromIndex=0;
		int nStep=1;
		int nCount=DataEngineHelp.getArrayLength(forItem.getArrKey(), cdoRequest);
		if(forItem.getFromIndex()!=null)
			nFromIndex=DataEngineHelp.getIntegerValue(forItem.getFromIndex(),cdoRequest);
		if(forItem.getStep()!=null)
			nStep=DataEngineHelp.getIntegerValue(forItem.getStep(),cdoRequest);
		if(forItem.getCount()!=null)
			nCount=DataEngineHelp.getIntegerValue(forItem.getCount(),cdoRequest);	
		
		String strIndexId=forItem.getIndexId();
		strIndexId=strIndexId.substring(1,strIndexId.length()-1);
		
		// 执行循环
		for(int i=nFromIndex;i<nFromIndex+nCount;i=i+nStep)
		{
			// 设置IndexId
			cdoRequest.setIntegerValue(strIndexId,i);

			// 执行Block
			int nResult=handleBlock(connection,forItem,cdoRequest,cdoResponse,ret,selTblMap);
			if(nResult==0)
			{// 自然执行完毕
				continue;
			}
			else if(nResult==1)
			{// 碰到Break
				break;
			}
			else
			{// 碰到Return
				return nResult;
			}
		}

		return 0;
	}
	/**
	 * 处理SQLBlock对象，得到输出的SQL语句
	 * 
	 * @param sqlBlock
	 * @return 0-自然执行完毕，1-碰到Break退出，2-碰到Return退出
	 */
	private int handleSwitch(Connection connection,Switch switchs,CDO cdoRequest,CDO cdoResponse,Return ret,Map<String,String> selTblMap) throws SQLException,IOException{
		String var=switchs.getVar();		
		var=var.substring(1,var.length()-1);
		//=======处理为CaseNull的情况=======//
		CaseNull caseNull=switchs.getCaseNull();
		if(caseNull!=null){
			//判断是否存在
			boolean isExist=cdoRequest.exists(var);	
			if(isExist){
				//判断是否为NullField
				if(cdoRequest.getField(var).getFieldType()==FieldType.type.NULL){
					isExist=false;
				}
			}
			if(!isExist)			
				return handleBlock(connection,caseNull,cdoRequest,cdoResponse,ret,selTblMap);
		}
		//=======处理Case情况=============//
		String varValue=cdoRequest.getObjectValue(var).toString();
		Case[] cases=switchs.getCase();
		if(cases!=null && cases.length>0){
			for(int i=0;i<cases.length;i++){
				String value=cases[i].getValue();
				if(value.startsWith("{") && value.endsWith("}")){
					value=value.substring(1,value.length()-1);
					value=cdoRequest.getObjectValue(value).toString();
				}
				String separate=cases[i].getSeparate();
				if(separate!=null && !separate.equals("")){
					String[] args=value.split(separate);
					for(int k=0;k<args.length;k++){
						if(varValue.equals(args[k])){
							return handleBlock(connection,cases[i],cdoRequest,cdoResponse,ret,selTblMap);				
						}	
					}
				}else{
					if(varValue.equals(value)){
						return handleBlock(connection,cases[i],cdoRequest,cdoResponse,ret,selTblMap);					
					}	
				}
			}
		}
		//===========处理Case Default情况==========//
		Default defaults=switchs.getDefault();
		if(defaults!=null){			
			return handleBlock(connection,defaults,cdoRequest,cdoResponse,ret,selTblMap);					
		}			
		return 0;
	}

	/*
	 * 处理每个block内容
	 * 
	 * @return 0-自然执行完毕，1-碰到Break退出，2-碰到Return退出
	 */
	private int handleBlock(Connection connection,BlockType block,
							CDO cdoRequest,CDO cdoResponse,Return ret,Map<String,String> selTblMap) throws SQLException,IOException
	{		
		int nItemCount=block.getBlockTypeItemCount();
		for(int i=0;i<nItemCount;i++)
		{
			BlockTypeItem blockItem=block.getBlockTypeItem(i);			
			if(blockItem.getSelectTable()!=null){
				// 获得将要执行的SQL
				SelectTable selectTable=(SelectTable)blockItem.getSelectTable();
				StringBuilder strbSQL=new StringBuilder();
				handleSQLBlock(selectTable,cdoRequest,strbSQL,selTblMap);
				String strSQL=strbSQL.toString();
				if(selTblMap==null)
					selTblMap=new HashMap<String,String>(5);
				
				String strOutputId=selectTable.getOutputId();
				strOutputId=strOutputId.substring(1,strOutputId.length()-1);
				selTblMap.put(strOutputId, strSQL);
			}else if(blockItem.getInsert()!=null)
			{ 
				//Insert  获得将要执行的SQL
				Insert insert=(Insert)blockItem.getInsert();
				StringBuilder strbSQL=new StringBuilder();
				handleSQLBlock(insert,cdoRequest,strbSQL,selTblMap);
				String strSQL=strbSQL.toString();
				// 执行SQL				
				dataEngine.executeUpdate(connection,strSQL,cdoRequest);	
			}
			else if(blockItem.getSelectRecord()!=null)
			{
				// 获得将要执行的SQL
				SelectRecord selectRecord=(SelectRecord)blockItem.getSelectRecord();
				StringBuilder strbSQL=new StringBuilder();
				handleSQLBlock(selectRecord,cdoRequest,strbSQL,selTblMap);
				String strSQL=strbSQL.toString();

				// 执行SQL
				CDO cdoRecord=new CDO();
				String strRecordCountId=selectRecord.getRecordCountId();				
				int nRecordCount=dataEngine.executeQueryRecord(connection,strSQL,cdoRequest,cdoRecord);
				if(strRecordCountId.length()>0)
				{// 输出受影响的记录数
					strRecordCountId=strRecordCountId.substring(1,strRecordCountId.length()-1);
					cdoRequest.setIntegerValue(strRecordCountId,nRecordCount);
				}

				String strOutputId=selectRecord.getOutputId();
				strOutputId=strOutputId.substring(1,strOutputId.length()-1);
				if(nRecordCount>0)
				{
					cdoRequest.setCDOValue(strOutputId,cdoRecord);
				}
			}
			else if(blockItem.getUpdate()!=null)
			{// Update
				// 获得将要执行的SQL
				Update update=(Update)blockItem.getUpdate();
				StringBuilder strbSQL=new StringBuilder();
				handleSQLBlock(update,cdoRequest,strbSQL,selTblMap);
				String strSQL=strbSQL.toString();

				// 执行SQL				
				int nRecordCount=dataEngine.executeUpdate(connection,strSQL,cdoRequest);	
				String strRecordCountId=update.getRecordCountId();
				if(strRecordCountId.length()>0)
				{// 输出受影响的记录数
					strRecordCountId=strRecordCountId.substring(1,strRecordCountId.length()-1);
					cdoRequest.setIntegerValue(strRecordCountId,nRecordCount);
				}
			}
			else if(blockItem.getDelete()!=null)
			{// Delete
				// 获得将要执行的SQL
				Delete delete=(Delete)blockItem.getDelete();
				StringBuilder strbSQL=new StringBuilder();
				handleSQLBlock(delete,cdoRequest,strbSQL,selTblMap);
				String strSQL=strbSQL.toString();

				// 执行SQL				
				int nRecordCount=dataEngine.executeUpdate(connection,strSQL,cdoRequest);
				String strRecordCountId=delete.getRecordCountId();
				if(strRecordCountId.length()>0)
				{// 输出受影响的记录数
					strRecordCountId=strRecordCountId.substring(1,strRecordCountId.length()-1);
					cdoRequest.setIntegerValue(strRecordCountId,nRecordCount);
				}
			}
			else if(blockItem.getSelectField()!=null)
			{
				// 获得将要执行的SQL
				SelectField selectField=(SelectField)blockItem.getSelectField();
				StringBuilder strbSQL=new StringBuilder();
				handleSQLBlock(selectField,cdoRequest,strbSQL,selTblMap);
				String strSQL=strbSQL.toString();

				// 执行SQL				
				Field objFieldValue=dataEngine.executeQueryFieldExt(connection,strSQL,cdoRequest);
				if(objFieldValue==null)
				{
					continue;
				}
				Object objValue=objFieldValue.getObjectValue();

				String strOutputId=selectField.getOutputId();
				strOutputId=strOutputId.substring(1,strOutputId.length()-1);
				//=====设置Field数据=====//
				DataEngineHelp.setFieldValue(cdoRequest, objFieldValue.getFieldType(), strOutputId, objValue);
				
			}
			else if(blockItem.getSelectRecordSet()!=null)
			{
				// SelectRecordSet 获得将要执行的SQL
				SelectRecordSet selectRecordSet=(SelectRecordSet)blockItem.getSelectRecordSet();
				StringBuilder strbSQL=new StringBuilder();
				handleSQLBlock(selectRecordSet,cdoRequest,strbSQL,selTblMap);
				String strSQL=strbSQL.toString();

				// 执行SQL
				CDOArrayField cdoArrayField=new CDOArrayField("");
				String strRecordCountId=selectRecordSet.getRecordCountId();
				int nRecordCount=dataEngine.executeQueryRecordSet(connection,strSQL,cdoRequest,cdoArrayField);
				if(strRecordCountId.length()>0)
				{// 输出受影响的记录数
					strRecordCountId=strRecordCountId.substring(1,strRecordCountId.length()-1);
					cdoRequest.setIntegerValue(strRecordCountId,nRecordCount);
				}
				
				String strOutputId=selectRecordSet.getOutputId();
				strOutputId=strOutputId.substring(1,strOutputId.length()-1);
				// RecordSet输出到数组
				cdoRequest.setCDOListValue(strOutputId, cdoArrayField.getValue());
			}
			else if(blockItem.getSetVar()!=null)
			{
				SetVar sv=blockItem.getSetVar();				
				DataEngineHelp.setVar(sv, cdoRequest);
			}
			else if(blockItem.getResetPage()!=null)
			{
				//======如果nPageIndex>totalPage,则进行重置分页========//
				ResetPage restPage=blockItem.getResetPage();
				DataEngineHelp.setResetPage(restPage, cdoRequest, cdoResponse);			
			}
			else if(blockItem.getIf()!=null)
			{
				int nResult=this.handleIf(connection,(If)blockItem.getIf(),cdoRequest,cdoResponse,ret,selTblMap);
				if(nResult==0)
				{// 自然执行完毕
					continue;
				}
				else
				{// 碰到Break或Return
					return nResult;
				}
			}
			else if(blockItem.getFor()!=null)
			{
				int nResult=this.handleFor(connection,(For)blockItem.getFor(),cdoRequest,cdoResponse,ret,selTblMap);
				if(nResult==0)
				{// 自然执行完毕
					continue;
				}
				else
				{// 碰到Break或Return
					return nResult;
				}
			}else if(blockItem.getSwitch()!=null){
				int nResult=this.handleSwitch(connection,(Switch)blockItem.getSwitch(),cdoRequest,cdoResponse,ret,selTblMap);
				if(nResult==0)
				{// 自然执行完毕
					continue;
				}
				else
				{// 碰到Break或Return
					return nResult;
				}				
			}else if(blockItem.getReturn()!=null){
				
				com.cdoPlugin.cdolib.database.xsd.Return returnObject=(com.cdoPlugin.cdolib.database.xsd.Return)blockItem.getReturn();
				this.handleReturn(returnObject,cdoRequest,cdoResponse,ret);

				return 2;
			}
			else if(blockItem.getBreak()!=null)
			{// Break退出
				return 1;
			}
		}
		return 0;
	}

	 Return executeTrans(SQLTrans trans,CDO cdoRequest,CDO cdoResponse) throws SQLException{
	   	//==========处理事务,加入事务传播==========//   	
		Return ret=new Return();
	   	String strDataGroupId=trans.getDataGroupId();
		//=========增加SelectTable处理,方便复用SQL条件过滤======//
		Map<String,String> selTblMap=null;
		DataSource dataSource=null;
		Connection connection=null;
		try{
			dataSource=(DataSource)SpringContextUtil.getBean(strDataGroupId);
		   	//========获取对应的connection======//
			connection=DataSourceUtils.getConnection(dataSource);
			if(connection==null){//DataGroupId错误
				throw new SQLException("Invalid datagroup id: "+strDataGroupId);
			}			
			//=========生成Block对象==========//
			BlockType block=new BlockType();
			int nTransItemCount=trans.getSQLTransChoice().getSQLTransChoiceItemCount();
			for(int i=0;i<nTransItemCount;i++)
			{
				SQLTransChoiceItem transItem=trans.getSQLTransChoice().getSQLTransChoiceItem(i);
				BlockTypeItem blockItem=null;
				if(transItem.getSelectTable()!=null){					
					blockItem=new BlockTypeItem();
					blockItem.setSelectTable(transItem.getSelectTable());
					if(selTblMap==null)
						selTblMap=new HashMap<String,String>(5);
				}else if(transItem.getInsert()!=null){
					blockItem=new BlockTypeItem();
					blockItem.setInsert(transItem.getInsert());
				}else if(transItem.getUpdate()!=null){
					blockItem=new BlockTypeItem();
					blockItem.setUpdate(transItem.getUpdate());
				}else if(transItem.getDelete()!=null){
					blockItem=new BlockTypeItem();
					blockItem.setDelete(transItem.getDelete());
				}else if(transItem.getSelectRecordSet()!=null){
					blockItem=new BlockTypeItem();
					blockItem.setSelectRecordSet(transItem.getSelectRecordSet());
				}else if(transItem.getSelectRecord()!=null){
					blockItem=new BlockTypeItem();
					blockItem.setSelectRecord(transItem.getSelectRecord());
				}else if(transItem.getSelectField()!=null){
					blockItem=new BlockTypeItem();
					blockItem.setSelectField(transItem.getSelectField());
				}else if(transItem.getIf()!=null){
					blockItem=new BlockTypeItem();
					blockItem.setIf(transItem.getIf());
				}else if(transItem.getFor()!=null){
					blockItem=new BlockTypeItem();
					blockItem.setFor(transItem.getFor());
				}else if(transItem.getSwitch()!=null){
					blockItem=new BlockTypeItem();
					blockItem.setSwitch(transItem.getSwitch());
				}else if(transItem.getSetVar()!=null){
					blockItem=new BlockTypeItem();
					blockItem.setSetVar(transItem.getSetVar());
				}else if(transItem.getResetPage()!=null){
					blockItem=new BlockTypeItem();
					blockItem.setResetPage(transItem.getResetPage());
				}
				//==========若block不为null,添加后进行集中处理======//
				if(blockItem!=null){
					block.addBlockTypeItem(blockItem);
				}
			}
			//============处理bolck里代码==============//
			int nResult=handleBlock(connection,block,cdoRequest,cdoResponse,ret,selTblMap);
			if(nResult!=2){
				// Break或自然执行完毕退出
				com.cdoPlugin.cdolib.database.xsd.Return returnObject=trans.getReturn();
				this.handleReturn(returnObject,cdoRequest,cdoResponse,ret);
			}			
		
		}catch(Throwable e){
			String strTransName=cdoRequest.getStringValue("strTransName");
			logger.error("executeTrans Exception: "+strTransName,e);
		   	//========其它异常情况处理========//
			OnException onException=trans.getOnException();
			ret=Return.valueOf(onException.getReturn().getCode(),onException.getReturn().getText(),onException.getReturn().getInfo());	
			throw new SQLException(e.getMessage(),e);
		}finally{
			//关闭连接
			DataSourceUtils.doReleaseConnection(connection, dataSource);
			if(selTblMap!=null){
				selTblMap.clear();
				selTblMap=null;
			}
		}		
		return ret;
	}
	/**
	 * 处理返回
	 * @param returnObject
	 * @param cdoRequest
	 * @param cdoResponse
	 * @param ret
	 * @throws SQLException
	 */
	private void handleReturn(com.cdoPlugin.cdolib.database.xsd.Return returnObject,CDO cdoRequest,CDO cdoResponse,Return ret) throws SQLException{
		int nReturnItemCount=returnObject.getReturnItemCount();
		for(int j=0;j<nReturnItemCount;j++){
			String strFieldId=returnObject.getReturnItem(j).getFieldId();
			String strValueId=returnObject.getReturnItem(j).getValueId();
			strFieldId=strFieldId.substring(1,strFieldId.length()-1);
			strValueId=strValueId.substring(1,strValueId.length()-1);
			Field object=null;
			try{
				if(cdoRequest.exists(strValueId))
					object=cdoRequest.getObject(strValueId);
			}catch(Exception e){
				continue;
			}
			// 输出
			if(object==null){
				continue;
			}		
			Object objValue=object.getObjectValue();
			//=======设置返回数据=======//
			DataEngineHelp.setFieldValue(cdoResponse, object.getFieldType(), strFieldId, objValue);			
		}
		ret.setCode(returnObject.getCode());
		ret.setInfo(returnObject.getInfo());
		ret.setText(returnObject.getText());
	}

	public Return handleTrans(SQLTrans sqlTrans, CDO cdoRequest,CDO cdoResponse) throws SQLException
	{
		return this.executeTrans(sqlTrans,cdoRequest,cdoResponse);
	}

	//构造函数,所有构造函数在此定义------------------------------------------------------------------------------
	IDataEngine dataEngine=null;
	public DataServiceParse(){
		dataEngine=new DataEngine(SpringContextUtil.getCharset());
	}
	
}