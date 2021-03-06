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

	//?????????,???????????????????????????----------------------------------------------------------------------------------

	//????????????,??????static????????????????????????------------------------------------------------------------------------	
	private static Log logger=LogFactory.getLog(DataServiceParse.class);
	/**
	 * ??????SQL????????????If??????
	 * 
	 * @param sqlIf
	 * @param cdoRequest
	 * @return 0-?????????????????????1-??????Break?????????2-??????Return??????
	 * @throws Exception
	 */
	private int handleSQLIf(SQLIf sqlIf,CDO cdoRequest,StringBuilder strbSQL,Map<String,String> selTblMap)
	{
		//============??????NullThen========//
		NullSQLThen nullSqlThen=sqlIf.getNullSQLThen();
		if(nullSqlThen!=null){
			//========??????????????????========//
			String strValue1=sqlIf.getValue1();
			strValue1=strValue1.substring(1,strValue1.length()-1);
			boolean isExist=cdoRequest.exists(strValue1);	
			if(isExist){
				//???????????????NullField
				if(cdoRequest.getField(strValue1).getFieldType()==FieldType.type.NULL){
					isExist=false;
				}
			}
			if(!isExist){
				return handleSQLBlock(nullSqlThen,cdoRequest,strbSQL,selTblMap);
			}
		}
		//=============??????????????????.??????????????????==========//
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
			{// ????????????
				return 0;
			}
			return handleSQLBlock(sqlElse,cdoRequest,strbSQL,selTblMap);
		}			
	}
	/**
	 * ??????SQL????????????For??????
	 * 
	 * @param sqlFor
	 * @param cdoRequest
	 * @param strbSQL
	 * @return 0-?????????????????????1-??????Break?????????2-??????Return??????
	 * @throws Exception
	 */
	protected int handleSQLFor(SQLFor sqlFor,CDO cdoRequest,StringBuilder strbSQL,Map<String,String> selTblMap)
	{
		// ??????????????????
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

		// ????????????
		for(int i=nFromIndex;i<nFromIndex+nCount;i=nStep+i)
		{
			// ??????IndexId
			cdoRequest.setIntegerValue(strIndexId,i);

			// ??????Block
			int nResult=handleSQLBlock(sqlFor,cdoRequest,strbSQL,selTblMap);
			if(nResult==0)
			{// ??????????????????
				continue;
			}
			else if(nResult==1)
			{// ??????Break
				break;
			}
			else
			{// ??????Return
				return nResult;
			}
		}

		return 0;
	}
	/**
	 * ??????SQLBlock????????????????????????SQL??????
	 * 
	 * @param sqlBlock
	 * @return 0-?????????????????????1-??????Break?????????2-??????Return??????
	 */
	private int handleSQLSwitch(SQLSwitch sqlSwitch,CDO cdoRequest,StringBuilder strbSQL,Map<String,String> selTblMap){
		String var=sqlSwitch.getVar();		
		var=var.substring(1,var.length()-1);
		//=========?????????CaseNull?????????========//
		SQLCaseNull caseNull=sqlSwitch.getSQLCaseNull();
		if(caseNull!=null){
			//??????????????????
			boolean isExist=cdoRequest.exists(var);	
			if(isExist){
				//???????????????NullField
				if(cdoRequest.getField(var).getFieldType()==FieldType.type.NULL){
					isExist=false;
				}
			}
			if(!isExist)
				return handleSQLBlock(caseNull,cdoRequest,strbSQL,selTblMap);
		}
		//===========??????Case??????==========//
		String varValue=cdoRequest.getObjectValue(var).toString();		
		SQLCase[] cases=sqlSwitch.getSQLCase();
		if(cases!=null && cases.length>0){
			for(int i=0;i<cases.length;i++){
				String value=cases[i].getValue();
				if(value.startsWith("{") && value.endsWith("}")){
					value=value.substring(1,value.length()-1);
					value=cdoRequest.getObjectValue(value).toString();
				}
				//========???????????????===========//
				String separate=cases[i].getSeparate();
				if(separate!=null && !separate.equals("")){
					String[] args=value.split(separate);
					for(int k=0;k<args.length;k++){
						if(varValue.equals(args[k])){														
							return handleSQLBlock(cases[i],cdoRequest,strbSQL,selTblMap);					
						}	
					}
				}else{
					//========??????????????????=========//
					if(varValue.equals(value)){													
						return handleSQLBlock(cases[i],cdoRequest,strbSQL,selTblMap);			
					}	
				}					
				
			}
		}			
		//===========??????Case default??????==========//
		SQLDefault defaults=sqlSwitch.getSQLDefault();
		if(defaults!=null){			
			return handleSQLBlock(defaults,cdoRequest,strbSQL,selTblMap);					
		}			
		return 0;
	}
	/**
	 * ??????SQLBlock????????????????????????SQL??????
	 * 
	 * @param sqlBlock
	 * @return 0-?????????????????????1-??????Break?????????2-??????Return??????
	 */
	protected int handleSQLBlock(SQLBlockType sqlBlock,CDO cdoRequest,StringBuilder strbSQL,Map<String,String> selTblMap)
	{
		// ??????????????????Item
		int nItemCount=sqlBlock.getSQLBlockTypeItemCount();
		for(int i=0;i<nItemCount;i++)
		{
			// ???????????????Item
			SQLBlockTypeItem item=sqlBlock.getSQLBlockTypeItem(i);
			if(item.getOutputSQL()!=null)
			{// OutputSQL,?????????????????????
				strbSQL.append(item.getOutputSQL());
			}
			else if(item.getOutputField()!=null)
			{// OutputField?????????????????????????????????
				String strOutputFieldId=item.getOutputField();
				strOutputFieldId=strOutputFieldId.substring(1,strOutputFieldId.length()-1);
				strbSQL.append(cdoRequest.getStringValue(strOutputFieldId));
			}
			else if(item.getOutputTable()!=null){
				// OutputTable?????????????????????????????????
				String outTblId=item.getOutputTable();
				outTblId=outTblId.substring(1,outTblId.length()-1);
				if(selTblMap==null || selTblMap.get(outTblId)==null){					
					throw new RuntimeException("?????????OutputTable???,?????????SelectTable.???????????????["+outTblId+"]?????????SelectTable");
				}else{
					strbSQL.append(selTblMap.get(outTblId));
				}
			}
			else if(item.getSQLIf()!=null)
			{// SQLIf
				int nResult=handleSQLIf(item.getSQLIf(),cdoRequest,strbSQL,selTblMap);
				if(nResult==0)
				{// ??????????????????
					continue;
				}
				else
				{// ??????Break???Return
					return nResult;
				}
			}
			else if(item.getSQLFor()!=null)
			{// SQLFor
				int nResult=handleSQLFor(item.getSQLFor(),cdoRequest,strbSQL,selTblMap);
				if(nResult==0)
				{// ??????????????????
					continue;
				}
				else
				{// ??????Break???Return
					return nResult;
				}				
			}else if(item.getSQLSwitch()!=null){
				//SQLSwitch
				int nResult=handleSQLSwitch(item.getSQLSwitch(),cdoRequest,strbSQL,selTblMap);
				if(nResult==0)
				{// ??????????????????
					continue;
				}
				else
				{// ??????Break???Return
					return nResult;
				}
			}
			else
			{
				continue;
			}
		}

		// ??????????????????
		return 0;
	}
	
	/**
	 * ??????If??????
	 * 
	 * @param ifItem
	 * @param cdoRequest
	 * @return 0-?????????????????????1-??????Break?????????2-??????Return??????
	 * @throws Exception
	 */
	private int handleIf(Connection connection,If ifItem,CDO cdoRequest,CDO cdoResponse,Return ret,Map<String,String> selTblMap) throws SQLException,IOException
	{
		//==============??????NullThen====================//
		NullThen nullThen=ifItem.getNullThen();
		if(nullThen!=null ){
			//========??????????????????========//
			String strValue1=ifItem.getValue1();
			strValue1=strValue1.substring(1,strValue1.length()-1);
			boolean isExist=cdoRequest.exists(strValue1);	
			if(isExist){
				//???????????????NullField
				if(cdoRequest.getField(strValue1).getFieldType()==FieldType.type.NULL){
					isExist=false;
				}
			}
			if(!isExist)
				return handleBlock(connection,nullThen,cdoRequest,cdoResponse,ret,selTblMap);
		}
		//============??????????????????????????????===============//
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
	 * ??????For??????
	 * 
	 * @param sqlFor
	 * @param cdoRequest
	 * @param strbSQL
	 * @return 0-?????????????????????1-??????Break?????????2-??????Return??????
	 * @throws Exception
	 */
	private int handleFor(Connection connection,For forItem,CDO cdoRequest,CDO cdoResponse,Return ret,Map<String,String> selTblMap) throws SQLException,IOException
	{
		// ??????????????????
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
		
		// ????????????
		for(int i=nFromIndex;i<nFromIndex+nCount;i=i+nStep)
		{
			// ??????IndexId
			cdoRequest.setIntegerValue(strIndexId,i);

			// ??????Block
			int nResult=handleBlock(connection,forItem,cdoRequest,cdoResponse,ret,selTblMap);
			if(nResult==0)
			{// ??????????????????
				continue;
			}
			else if(nResult==1)
			{// ??????Break
				break;
			}
			else
			{// ??????Return
				return nResult;
			}
		}

		return 0;
	}
	/**
	 * ??????SQLBlock????????????????????????SQL??????
	 * 
	 * @param sqlBlock
	 * @return 0-?????????????????????1-??????Break?????????2-??????Return??????
	 */
	private int handleSwitch(Connection connection,Switch switchs,CDO cdoRequest,CDO cdoResponse,Return ret,Map<String,String> selTblMap) throws SQLException,IOException{
		String var=switchs.getVar();		
		var=var.substring(1,var.length()-1);
		//=======?????????CaseNull?????????=======//
		CaseNull caseNull=switchs.getCaseNull();
		if(caseNull!=null){
			//??????????????????
			boolean isExist=cdoRequest.exists(var);	
			if(isExist){
				//???????????????NullField
				if(cdoRequest.getField(var).getFieldType()==FieldType.type.NULL){
					isExist=false;
				}
			}
			if(!isExist)			
				return handleBlock(connection,caseNull,cdoRequest,cdoResponse,ret,selTblMap);
		}
		//=======??????Case??????=============//
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
		//===========??????Case Default??????==========//
		Default defaults=switchs.getDefault();
		if(defaults!=null){			
			return handleBlock(connection,defaults,cdoRequest,cdoResponse,ret,selTblMap);					
		}			
		return 0;
	}

	/*
	 * ????????????block??????
	 * 
	 * @return 0-?????????????????????1-??????Break?????????2-??????Return??????
	 */
	private int handleBlock(Connection connection,BlockType block,
							CDO cdoRequest,CDO cdoResponse,Return ret,Map<String,String> selTblMap) throws SQLException,IOException
	{		
		int nItemCount=block.getBlockTypeItemCount();
		for(int i=0;i<nItemCount;i++)
		{
			BlockTypeItem blockItem=block.getBlockTypeItem(i);			
			if(blockItem.getSelectTable()!=null){
				// ?????????????????????SQL
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
				//Insert  ?????????????????????SQL
				Insert insert=(Insert)blockItem.getInsert();
				StringBuilder strbSQL=new StringBuilder();
				handleSQLBlock(insert,cdoRequest,strbSQL,selTblMap);
				String strSQL=strbSQL.toString();
				// ??????SQL				
				dataEngine.executeUpdate(connection,strSQL,cdoRequest);	
			}
			else if(blockItem.getSelectRecord()!=null)
			{
				// ?????????????????????SQL
				SelectRecord selectRecord=(SelectRecord)blockItem.getSelectRecord();
				StringBuilder strbSQL=new StringBuilder();
				handleSQLBlock(selectRecord,cdoRequest,strbSQL,selTblMap);
				String strSQL=strbSQL.toString();

				// ??????SQL
				CDO cdoRecord=new CDO();
				String strRecordCountId=selectRecord.getRecordCountId();				
				int nRecordCount=dataEngine.executeQueryRecord(connection,strSQL,cdoRequest,cdoRecord);
				if(strRecordCountId.length()>0)
				{// ???????????????????????????
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
				// ?????????????????????SQL
				Update update=(Update)blockItem.getUpdate();
				StringBuilder strbSQL=new StringBuilder();
				handleSQLBlock(update,cdoRequest,strbSQL,selTblMap);
				String strSQL=strbSQL.toString();

				// ??????SQL				
				int nRecordCount=dataEngine.executeUpdate(connection,strSQL,cdoRequest);	
				String strRecordCountId=update.getRecordCountId();
				if(strRecordCountId.length()>0)
				{// ???????????????????????????
					strRecordCountId=strRecordCountId.substring(1,strRecordCountId.length()-1);
					cdoRequest.setIntegerValue(strRecordCountId,nRecordCount);
				}
			}
			else if(blockItem.getDelete()!=null)
			{// Delete
				// ?????????????????????SQL
				Delete delete=(Delete)blockItem.getDelete();
				StringBuilder strbSQL=new StringBuilder();
				handleSQLBlock(delete,cdoRequest,strbSQL,selTblMap);
				String strSQL=strbSQL.toString();

				// ??????SQL				
				int nRecordCount=dataEngine.executeUpdate(connection,strSQL,cdoRequest);
				String strRecordCountId=delete.getRecordCountId();
				if(strRecordCountId.length()>0)
				{// ???????????????????????????
					strRecordCountId=strRecordCountId.substring(1,strRecordCountId.length()-1);
					cdoRequest.setIntegerValue(strRecordCountId,nRecordCount);
				}
			}
			else if(blockItem.getSelectField()!=null)
			{
				// ?????????????????????SQL
				SelectField selectField=(SelectField)blockItem.getSelectField();
				StringBuilder strbSQL=new StringBuilder();
				handleSQLBlock(selectField,cdoRequest,strbSQL,selTblMap);
				String strSQL=strbSQL.toString();

				// ??????SQL				
				Field objFieldValue=dataEngine.executeQueryField(connection,strSQL,cdoRequest);
				if(objFieldValue==null)
				{
					continue;
				}
				Object objValue=objFieldValue.getObjectValue();

				String strOutputId=selectField.getOutputId();
				strOutputId=strOutputId.substring(1,strOutputId.length()-1);
				//=====??????Field??????=====//
				DataEngineHelp.setFieldValue(cdoRequest, objFieldValue.getFieldType(), strOutputId, objValue);
				
			}
			else if(blockItem.getSelectRecordSet()!=null)
			{
				// SelectRecordSet ?????????????????????SQL
				SelectRecordSet selectRecordSet=(SelectRecordSet)blockItem.getSelectRecordSet();
				StringBuilder strbSQL=new StringBuilder();
				handleSQLBlock(selectRecordSet,cdoRequest,strbSQL,selTblMap);
				String strSQL=strbSQL.toString();

				// ??????SQL
				CDOArrayField cdoArrayField=new CDOArrayField("");
				String strRecordCountId=selectRecordSet.getRecordCountId();
				int nRecordCount=dataEngine.executeQueryRecordSet(connection,strSQL,cdoRequest,cdoArrayField);
				if(strRecordCountId.length()>0)
				{// ???????????????????????????
					strRecordCountId=strRecordCountId.substring(1,strRecordCountId.length()-1);
					cdoRequest.setIntegerValue(strRecordCountId,nRecordCount);
				}
				
				String strOutputId=selectRecordSet.getOutputId();
				strOutputId=strOutputId.substring(1,strOutputId.length()-1);
				// RecordSet???????????????
				cdoRequest.setCDOListValue(strOutputId, cdoArrayField.getValue());
			}
			else if(blockItem.getSetVar()!=null)
			{
				SetVar sv=blockItem.getSetVar();				
				DataEngineHelp.setVar(sv, cdoRequest);
			}
			else if(blockItem.getResetPage()!=null)
			{
				//======??????nPageIndex>totalPage,?????????????????????========//
				ResetPage restPage=blockItem.getResetPage();
				DataEngineHelp.setResetPage(restPage, cdoRequest, cdoResponse);			
			}
			else if(blockItem.getIf()!=null)
			{
				int nResult=this.handleIf(connection,(If)blockItem.getIf(),cdoRequest,cdoResponse,ret,selTblMap);
				if(nResult==0)
				{// ??????????????????
					continue;
				}
				else
				{// ??????Break???Return
					return nResult;
				}
			}
			else if(blockItem.getFor()!=null)
			{
				int nResult=this.handleFor(connection,(For)blockItem.getFor(),cdoRequest,cdoResponse,ret,selTblMap);
				if(nResult==0)
				{// ??????????????????
					continue;
				}
				else
				{// ??????Break???Return
					return nResult;
				}
			}else if(blockItem.getSwitch()!=null){
				int nResult=this.handleSwitch(connection,(Switch)blockItem.getSwitch(),cdoRequest,cdoResponse,ret,selTblMap);
				if(nResult==0)
				{// ??????????????????
					continue;
				}
				else
				{// ??????Break???Return
					return nResult;
				}				
			}else if(blockItem.getReturn()!=null){
				
				com.cdoPlugin.cdolib.database.xsd.Return returnObject=(com.cdoPlugin.cdolib.database.xsd.Return)blockItem.getReturn();
				this.handleReturn(returnObject,cdoRequest,cdoResponse,ret);

				return 2;
			}
			else if(blockItem.getBreak()!=null)
			{// Break??????
				return 1;
			}
		}
		return 0;
	}

	 Return executeTrans(SQLTrans trans,CDO cdoRequest,CDO cdoResponse) throws SQLException{
	   	//==========????????????,??????????????????==========//   	
		Return ret=new Return();
	   	String strDataGroupId=trans.getDataGroupId();
		//=========??????SelectTable??????,????????????SQL????????????======//
		Map<String,String> selTblMap=null;
		DataSource dataSource=null;
		Connection connection=null;
		try{
			dataSource=(DataSource)SpringContextUtil.getBean(strDataGroupId);
		   	//========???????????????connection======//
			connection=DataSourceUtils.getConnection(dataSource);
			if(connection==null){//DataGroupId??????
				throw new SQLException("Invalid datagroup id: "+strDataGroupId);
			}			
			//=========??????Block??????==========//
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
				//==========???block??????null,???????????????????????????======//
				if(blockItem!=null){
					block.addBlockTypeItem(blockItem);
				}
			}
			//============??????bolck?????????==============//
			int nResult=handleBlock(connection,block,cdoRequest,cdoResponse,ret,selTblMap);
			if(nResult!=2){
				// Break???????????????????????????
				com.cdoPlugin.cdolib.database.xsd.Return returnObject=trans.getReturn();
				this.handleReturn(returnObject,cdoRequest,cdoResponse,ret);
			}			
		
		}catch(Throwable e){
			String strTransName=cdoRequest.getStringValue("strTransName");
			logger.error("executeTrans Exception: "+strTransName,e);
		   	//========????????????????????????========//
			OnException onException=trans.getOnException();
			ret=Return.valueOf(onException.getReturn().getCode(),onException.getReturn().getText(),onException.getReturn().getInfo());	
			throw new SQLException(e.getMessage(),e);
		}finally{
			//????????????
			DataSourceUtils.doReleaseConnection(connection, dataSource);
			if(selTblMap!=null){
				selTblMap.clear();
				selTblMap=null;
			}
		}		
		return ret;
	}
	/**
	 * ????????????
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
			// ??????
			if(object==null){
				continue;
			}		
			Object objValue=object.getObjectValue();
			//=======??????????????????=======//
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

	//????????????,??????????????????????????????------------------------------------------------------------------------------
	IDataEngine dataEngine=null;
	public DataServiceParse(){
		dataEngine=new DataEngine(SpringContextUtil.getCharset());
	}
	
}