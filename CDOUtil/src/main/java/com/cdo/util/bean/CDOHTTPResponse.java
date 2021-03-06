package com.cdo.util.bean;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.cdoframework.cdolib.data.cdo.CDO;

public class CDOHTTPResponse extends Response {

	public CDOHTTPResponse() {
		
	}	
	private static final long serialVersionUID = -6353099618301387046L;
	private Map<String, File> mapFile=new HashMap<String, File>();

	public void addFile(String strField,File fileValue){
		mapFile.put(strField, fileValue);
	}
	public File getFile(String strField){
		return mapFile.get(strField);
	}
	public Map<String, File> mapFile(){
		return mapFile;
	}
	public void copyFile2CDO(CDO cdo){
		for(Iterator<Map.Entry<String, File>> it=mapFile.entrySet().iterator();it.hasNext();){
			Entry<String, File> entry=it.next();
			cdo.setFileValue(entry.getKey(),entry.getValue());
		}
	}	
}
