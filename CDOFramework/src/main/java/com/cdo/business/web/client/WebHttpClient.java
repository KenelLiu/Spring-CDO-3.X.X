package com.cdo.business.web.client;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cdo.field.Field;
import com.cdo.field.FieldType;
import com.cdo.field.FileField;
import com.cdo.util.bean.CDOHTTPResponse;
import com.cdo.util.constants.Constants;
import com.cdo.util.exception.HttpException;
import com.cdo.util.serial.Serializable;
import com.cdoframework.cdolib.base.Return;
import com.cdoframework.cdolib.data.cdo.CDO;
import com.cdoframework.cdolib.servicebus.ITransService;

public class WebHttpClient implements IWebClient{	
	private static Log logger=LogFactory.getLog(WebHttpClient.class);
	private String strUrl;

	public WebHttpClient() {
	}

	public WebHttpClient(String strUrl) {		
		this.strUrl = strUrl;
	}

	public String getUrl() {
		return this.strUrl;
	}

	public void setUrl(String strUrl) {
		this.strUrl = strUrl;
	}

	
	public Return handleTrans(CDO cdoRequest, CDO cdoResponse) {

		if ((this.strUrl == null) || (this.strUrl.equals(""))){
			throw new HttpException(" target strUrl  is null ");
		}			
		CDOHTTPResponse httpRes=null;
		try {
			CDOHTTPClient httpClient = new CDOHTTPClient(this.strUrl);	
			httpClient.setTransCDO(cdoRequest);
			//cdo有文件上传,设置文件参数 和设置 头信息				
			if(cdoRequest.getSerialFileCount()>0){
				 Map<String,File> uploadFiles=new HashMap<String,File>();
	    		 Map<String,String> headers=new HashMap<String,String>();
				 Iterator<Map.Entry<String,Field>> it=cdoRequest.entrySet().iterator();		    		 
	    		 while(it.hasNext()){
	    			 Map.Entry<String,Field> entry=it.next();
	    			 Field objExt=entry.getValue();
	    			 if(objExt.getFieldType().getType()==FieldType.FILE_TYPE){
	    				 uploadFiles.put(entry.getKey(),((FileField)entry.getValue()).getValue());
	    				 //设置普通参表示有文件上传
	    				 headers.put(Constants.CDO.HTTP_CDO_UPLOAD_FILE_FLAG, "1");//表示CDO里包含有文件
	    			 }
	    		 }
	    		 httpClient.setHeaders(headers);
	    		 httpClient.setUploadFiles(uploadFiles);
			}
			//主要处理由外部，设置控制文件流，默认为自动关闭。当由外部通过OutStreamCDO控制  设置autoCloseStream 外部控制关闭
			httpClient.setCdoResponse(cdoResponse);
			//响应数据	
			httpRes=httpClient.execute();					
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return Return.valueOf(-1, " http response :" + httpRes+ ";Send Http Request ERROR:" + ex.getMessage());
		}
		
		if(httpRes.getStatusCode() != HttpStatus.SC_OK){
			return Return.valueOf(-1, " http Status:" + httpRes.getStatusCode()
			        + ";Send Http Request ERROR:" + httpRes);
		}
		try {
			CDO cdoReturn=new CDO();
			ParseHTTPProtoCDO.HTTPProtoParse.parse(httpRes.getResponseBytes(), cdoResponse,cdoReturn);
			//有文件传输,仅设置文件句柄，文件在磁盘上，或者通过外部开发者自己控制，写入到指定设备[内存，磁盘，输出流上]
			httpRes.copyFile2CDO(cdoResponse);			
			return new Return(cdoReturn.getIntegerValue("nCode"),cdoReturn.getStringValue("strText"), cdoReturn.getStringValue("strInfo"));	
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return Return.valueOf(-1, "ERROR:" + ex.getMessage());
		}finally{
			httpRes.setResponseBytes(null);
			httpRes=null;
		}
	}


}