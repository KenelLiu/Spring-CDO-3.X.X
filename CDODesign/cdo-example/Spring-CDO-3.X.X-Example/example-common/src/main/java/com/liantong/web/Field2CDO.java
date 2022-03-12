package com.liantong.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cdoframework.cdolib.data.cdo.CDO;

public class Field2CDO {
	 static Log logger=LogFactory.getLog(Field2CDO.class);
	 
	 public static void setField(JSONArray arr,Class<?> cls,CDO cdoRequest,String cdosKey){
	    	List<CDO> cdoList=new ArrayList<CDO>(arr.length());
	    	for(int i=0;i<arr.length();i++){
	    		JSONObject jsonObject=arr.getJSONObject(i);
	    		CDO rowCDO=new CDO();
	    		setField(jsonObject,cls,rowCDO);
	    		cdoList.add(rowCDO);
	    	}
	    	cdoRequest.setCDOListValue(cdosKey, cdoList);
	    }
	 
	  public static void setField(JSONObject jsonObject,Class<?> cls,CDO rowCDO){
	    	java.lang.reflect.Field[] fields=cls.getDeclaredFields();
	    	for (java.lang.reflect.Field field : fields) {
	    		String typeName=field.getType().getSimpleName();
	    		String fieldName=field.getName();
	    		try{
	    			if(fieldName.equals("serialVersionUID")){
	    				continue;
	    			}
	    			switch(typeName){
			    		case "int":
			    		case "long":
			    			String value=jsonObject.optString(fieldName,"0").trim();
			    			if(value.equals("")){
			    				value="0";
			    			}
			    			long lVal=Long.valueOf(value);
			    			rowCDO.setLongValue(fieldName, lVal);
			    			break;
			    		case "Date"://java.util.Date 类型时
			    		case "ShortDate"://	ShortDate
			    			if(jsonObject.isNull(fieldName)){
			    				rowCDO.setNullValue(fieldName);
			    			}else{		    				
			    				value=jsonObject.getString(fieldName);
			    				if(value.trim().equals("")){
			    					rowCDO.setNullValue(fieldName);
			    				}else{
				    				//日期格式要求 "yyyy-MM-dd HH:mm:ss";
			    					//日期格式要求 "yyyy-MM-dd";
			    					if(typeName.equals("Date"))
			    						rowCDO.setDateTimeValue(fieldName, value);
			    					else
			    						rowCDO.setDateValue(fieldName, value);
			    				}
			    			}
			    			break;			    		
			    		case "JSONArray":
			    			if(jsonObject.isNull(fieldName)){
			    				rowCDO.setStringValue(fieldName, "[]");
			    			}else{		    				
			    				JSONArray v=jsonObject.getJSONArray(fieldName);
			    				rowCDO.setStringValue(fieldName, v.toString());
			    			}
			    			break;		    		
			    		default: 
			    			value=jsonObject.optString(fieldName,"");
			    			value=FieldJSON.str2JSONArr(cls,fieldName,value);
			    			rowCDO.setStringValue(fieldName, value);		    			
		    		}
	    		}catch(Exception ex){
	    			logger.error("转换类型失败fieldName="+fieldName+",typeName="+typeName+",error="+ex.getMessage(),ex);
	    			throw new JSONException("转换类型失败fieldName="+fieldName+",typeName="+typeName+",error="+ex.getMessage());
	    		}
			}
	    }
	  

}
