package com.liantong.web;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cdo.field.Field;
import com.cdo.field.FieldType;
import com.cdoframework.cdolib.data.cdo.CDO;

public class CDO2Field {
	 static Log logger=LogFactory.getLog(Field2CDO.class);
	 
	public static  void setField(JSONArray arr,List<CDO> dataList,Class<?> cls){
	    	for(int i=0;i<dataList.size();i++){
	    		CDO rawCDO=dataList.get(i);
	    		JSONObject rawJson=new JSONObject();
	    		for(Iterator<Map.Entry<String,Field>> it=rawCDO.iterator();it.hasNext();){
	    			Entry<String,Field> entry=it.next();
	    			String fieldName=entry.getKey();
	    			byte type=entry.getValue().getFieldType().getType();
	    			if(type>=FieldType.BOOLEAN_TYPE &&  type<FieldType.CDO_TYPE){
	    				setField(rawJson, rawCDO, cls, fieldName);	    			
	    			}else if(type==FieldType.CDO_TYPE){
	    				JSONObject dataJson=new JSONObject();
	    				CDO dataCDO=rawCDO.getCDOValue(fieldName);
	    				setField(dataJson, dataCDO, cls);	
	    				rawJson.put(fieldName, dataJson);
	    			}else if(type==FieldType.CDO_ARRAY_TYPE){
	    				JSONArray dataArr=new JSONArray();
	    				List<CDO> cdoArr=rawCDO.getCDOListValue(fieldName);
	    				setField(dataArr, cdoArr, cls);
	    				rawJson.put(fieldName, dataArr);
	    			}
	    		}
	    		arr.put(rawJson);
	    	}
	    }
	  
	public static  void setField(JSONObject rawJson,CDO rawCDO,Class<?> cls){
		for(Iterator<Map.Entry<String,Field>> it=rawCDO.iterator();it.hasNext();){
			Entry<String,Field> entry=it.next();
			String fieldName=entry.getKey();
			byte type=entry.getValue().getFieldType().getType();
			if(type>=FieldType.BOOLEAN_TYPE &&  type<FieldType.CDO_TYPE){
				setField(rawJson, rawCDO, cls, fieldName);	    			
			}else if(type==FieldType.CDO_TYPE){
				JSONObject dataJson=new JSONObject();
				CDO dataCDO=rawCDO.getCDOValue(fieldName);
				setField(dataJson, dataCDO, cls);
				rawJson.put(fieldName, dataJson);
			}else if(type==FieldType.CDO_ARRAY_TYPE){
				JSONArray dataArr=new JSONArray();
				List<CDO> cdoArr=rawCDO.getCDOListValue(fieldName);
				setField(dataArr, cdoArr, cls);
				rawJson.put(fieldName, dataArr);
			}
		}
	}
	
	static void setField(JSONObject rawJson,CDO rawCDO,Class<?> cls,String fieldName){
		   	String typeName=null;
	    	try{
	    		java.lang.reflect.Field field=cls.getDeclaredField(fieldName); 
	    		typeName=field.getType().getSimpleName();    		
	    		String objVal=rawCDO.getObjectValue(fieldName).toString();
				switch(typeName){
		    		case "int":
		    		case "long":
		    			rawJson.put(fieldName, Long.parseLong(objVal));
		    			break; 
		    		case "boolean":
		    			if(objVal.toLowerCase().equals("true")){
		    				rawJson.put(fieldName,1);
		    			}else{
		    				rawJson.put(fieldName,0);
		    			}
		    			break;
		    		case "float":
		    			rawJson.put(fieldName, Double.valueOf(objVal).floatValue());
		    			break;
		    		default: 
		    			objVal=FieldJSON.str2JSONArr(cls,fieldName,objVal);
		    			rawJson.put(fieldName, objVal);	    					    			
	    		}
			}catch(Exception ex){
				logger.error("转换类型失败fieldName="+fieldName+",typeName="+typeName+",error="+ex.getMessage(),ex);
				throw new JSONException("转换类型失败fieldName="+fieldName+",typeName="+typeName+",error="+ex.getMessage());
			}								    
	   }
}
