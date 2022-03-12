package com.cdo.business;


import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cdo.field.Field;
import com.cdo.field.FieldType;
import com.cdoframework.cdolib.data.cdo.CDO;

public class BusinessDataEngine extends com.cdoframework.cdolib.database.DataEngine
{
	private static final Log log=LogFactory.getLog(BusinessDataEngine.class);
	public void onSQLStatement(String strSQL)
	{
		if(log.isDebugEnabled()){log.debug("SQL:"+strSQL);}
	}

	public void onException(String strText,Exception e)
	{				
		log.error("Database Exception: "+strText+"\r\n"+e+":"+e);
	}
	
	public void onExecuteSQL(String strSQL,ArrayList<String> alParaName,CDO cdoRequest){
		if (log.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder("\n{");
			for (int i = 0; i < alParaName.size(); i++) {
				Field object = cdoRequest.getObject(alParaName.get(i));
				Object objValue = object.getObjectValue();
				int nType = object.getFieldType().getType();
				sb.append(nType==FieldType.BYTE_ARRAY_TYPE?new String((byte[]) objValue):objValue);
				sb.append(',');
			}
			sb.append('}');
			log.debug(strSQL + sb.toString());
		}
	}
	//构造函数,所有构造函数在此定义------------------------------------------------------------------------------

	public BusinessDataEngine()
	{

	}
}
