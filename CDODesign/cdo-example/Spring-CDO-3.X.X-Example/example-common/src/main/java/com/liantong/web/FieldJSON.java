package com.liantong.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;

public class FieldJSON {
	  static Log logger=LogFactory.getLog(Field2CDO.class);
	  
	  static String str2JSONArr(Class<?> cls,String fieldName,String value){		  		
		  	String clsName=null;
		  	try{
		  		if(value==null || value.trim().equals("")){
		  			return value;
		  		}
		  		clsName=cls.getSimpleName();
		  		switch (clsName){
				case "SafeEventUpload":
				case "ReportUpload":	
				case "CaseUpload":
					if(fieldName.equals("threatenedAssets")||fieldName.equals("attachment")){
						JSONArray arr=new JSONArray(value);
						return arr.toString();
					}
					break;				
				}
		  		return value;
		  	}catch(Exception ex){
		  		logger.error("clsName="+clsName+",fieldName="+fieldName+",value="+value+",error="+ex.getMessage());
		  		return value;
		  	}
	  }
}
