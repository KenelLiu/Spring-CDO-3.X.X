package com.cdo.util.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
//import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.content.ByteArrayBody;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cdo.util.bean.CDOHTTPResponse;
//import com.cdo.util.common.Zipper;
import com.cdo.util.constants.Constants;
import com.cdo.util.exception.HttpException;
//import com.cdo.util.resource.GlobalResource;
//import com.cdo.util.serial.Serializable;
import com.cdoframework.cdolib.data.cdo.CDO;
/**
 * 
 * @author KenelLiu
 *
 */
public class HttpClient {	
	private static Log logger=LogFactory.getLog(HttpClient.class);
	
    public static final ContentType TEXT_PLAIN_UTF8 =ContentType.create("text/plain", Charset.forName(Constants.Encoding.CHARSET_UTF8));
	//----------传输类型---------//
	public static final int TRANSMODE_BODY = 1;//整体传输  例如SOAP xml文件 设置在body里传输
	public static final int TRANSMODE_FORM = 2;//普通form传输  包含文件传输
	//--------方法-------------//
	public static final String METHOD_POST = "POST";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_GET = "GET";
	public static final String METHOD_DELETE = "DELETE";
	
	private org.apache.http.client.HttpClient httpClient;
	private HttpRequestBase httpRequest;
	private Map<String, String> headers;
	private Map<String, File>  uploadFiles;
	private int transMode;
	private String url;
	private String method=METHOD_POST;
	private CDO transCDO=null;

	private List<NameValuePair> paramsList = new ArrayList<NameValuePair>();

	
	private String body = null;

	private HttpHost httpHost = null;
	
   
	public CDO getTransCDO() {
		return transCDO;
	}

	public void setTransCDO(CDO transCDO) {
		this.transCDO = transCDO;
	}

	public HttpClient() {
		this(null,METHOD_POST);
	}

	public HttpClient(String url) {
		this(url,METHOD_POST);
	}
	
	public HttpClient(String url,String method) {
		this(url,method,TRANSMODE_FORM);
	}
	
	public HttpClient(String url, String method,int transMode) {
		this(url,method,transMode,null);	
	}

	public HttpClient(String url, String method,int transMode,
			Map<String, String> headers) {
		this.url = url;
		this.transMode = transMode;
		this.method = method;
		this.headers = headers;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setTransMode(int transMode) {
		this.transMode = transMode;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public void setUploadFiles(Map<String, File> uploadFiles) {	
		this.uploadFiles = uploadFiles;
	}	
	
	public void setNameValuePair(Map<String, String> param) {		
		if (param == null)
			return;
		for (Map.Entry<String,String> entry : param.entrySet()) {			
			if ((entry.getKey()  == null) || (entry.getKey().trim().equals("")))
				continue;
			this.paramsList.add(new BasicNameValuePair(entry.getKey().trim(), entry.getValue()));
		}
	}

	public CDOHTTPResponse execute(){
		HTTPResponse handler=new HTTPResponse();
		return handleResponse(handler);
	}
	
	/**
	 * 执行Http Request,并得到响应
	 * @param handler
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected  <T>T handleResponse(ResponseHandler<T> handler) {
		Object response = null;
		try {
			setRequestParam();	
			this.httpClient = HttpUtil.getHttpClient();	
			if (this.httpHost != null)
				response = this.httpClient.execute(this.httpHost,this.httpRequest, handler);
			else 
				response = this.httpClient.execute(this.httpRequest, handler);
			
			return (T)response;
		} catch (Exception e) {			
			if(httpRequest!=null){try{httpRequest.abort();}catch(Exception em){}}
			logger.error(e.getMessage(), e);
			throw new HttpException("httpClient send request error:"+ e.getMessage());
		}finally{			
			//释放资源
			if(this.httpRequest!=null){try{this.httpRequest.releaseConnection();}catch(Exception ex){}}
		}
	}
	/**
	 * 设置请求参数
	 */
	@SuppressWarnings("deprecation")
	private void setRequestParam() {

		HttpEntity  entity = null;		
		try {
			if (this.url == null || this.url.trim().length()==0) {
				throw new HttpException("http url is null");
			}				
			if (this.transMode ==TRANSMODE_BODY) {
				//作为body传输
				if (this.body!= null) {
					entity=new StringEntity(this.body, Constants.Encoding.CHARSET_UTF8);					
				}
			} else {
				if(this.uploadFiles!=null){
					//有文件传输    必须使用  MultipartEntity		 							
					entity=entity==null?new MultipartEntity():entity;
					if(this.uploadFiles!=null){
						  for (Map.Entry<String,File> entry : this.uploadFiles.entrySet()) {			
								if ((entry.getKey()  == null) || (entry.getKey().trim().equals("")))
									continue;		
								((MultipartEntity)entity).addPart(entry.getKey(),  new FileBody(entry.getValue()));
							}
					  }								
					//设置普通一般参数 
					for(NameValuePair pair:this.paramsList){	
							((MultipartEntity)entity).addPart(pair.getName(), new StringBody(pair.getValue(),Charset.forName(Constants.Encoding.CHARSET_UTF8)));								
					}
					//设置CDO 参数
					if(getTransCDO()!=null){					
					   ((MultipartEntity)entity).addPart(Constants.CDO.HTTP_CDO_REQUEST,new StringBody(getTransCDO().toXML()));				  
					}
				}else{					
					if(getTransCDO()!=null){
						entity = new ByteArrayEntity(getTransCDO().toProtoBuilder().build().toByteArray());
					}else{
						//普通请求
						entity = new UrlEncodedFormEntity(paramsList,Charset.forName(Constants.Encoding.CHARSET_UTF8));	
					}	
				}								
				/**4.5
				 * 
				 *  MultipartEntityBuilder reqEntity=MultipartEntityBuilder.create();				 * 
				 * 	reqEntity.addBinaryBody(entry.getKey(),  entry.getValue());		
				 * 	reqEntity.addTextBody(pair.getName(),pair.getValue(),TEXT_PLAIN_UTF8);
				 *  entity=reqEntity.build();
				 */				 			
			}
			
			if (this.method.equals(METHOD_PUT)) {
				this.httpRequest = new HttpPut(this.url);
				((HttpEntityEnclosingRequestBase) this.httpRequest).setEntity(entity);
			} else if (this.method.equals(METHOD_GET)) {
				this.httpRequest = new HttpGet(this.url);
			} else if (this.method.equals(METHOD_DELETE)) {
				this.httpRequest = new HttpDelete(this.url);
			} else {
				this.httpRequest = new HttpPost(this.url);
				((HttpPost)this.httpRequest).setEntity(entity);			
			}

			if (this.headers != null) {
				for (Map.Entry<String,String> entry : this.headers.entrySet()) {			
					if ((entry.getKey()  == null) || (entry.getKey().trim().equals("")))
						continue;
					this.httpRequest.setHeader(entry.getKey().trim(), entry.getValue());
				}			
			}		
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new HttpException("http 参数发生异常:"+e.getMessage(),e);
		}
		
	}


	public void setProxy(String url, int port) {
		this.httpHost = new HttpHost(url, port);
	}
		


	
	protected class HTTPResponse implements ResponseHandler<CDOHTTPResponse>{
		private OutputStream outStream;
		private boolean autoCloseStream=true;
		public HTTPResponse(){
			
		}
		public void setOutputStream(OutputStream outStream){
			this.outStream=outStream;
		}
		public void setAutoCloseStream(boolean autoCloseStream){
			this.autoCloseStream=autoCloseStream;
		}
		
		public CDOHTTPResponse handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {
			CDOHTTPResponse cdoHttpResponse=new CDOHTTPResponse();
			cdoHttpResponse.setAllHeaders(response.getAllHeaders());
			cdoHttpResponse.setStatusCode(response.getStatusLine().getStatusCode());			
			//----读取响应报头信息	 用于区分普通请求响应  还是cdo响应---//
			LinkedHashMap<String, FileInfo> linkMap=readHeader(response.getAllHeaders());
			try{					
				if(linkMap.size()==1){//仅有 cdo输出			 
					cdoHttpResponse.setResponseBytes(response.getEntity()==null?null:EntityUtils.toByteArray(response.getEntity()));
					return cdoHttpResponse;
				}else if(linkMap.size()>1){//cdo和下载文件   混合输出,第一个输出为cdo,后续为文件依次输出//	
					outMixData(response, linkMap, cdoHttpResponse);
					return cdoHttpResponse;
				}else{
					 //普通http请求     判断响应是否为下载文件请求				
		    		 Header header = response.getFirstHeader("Content-Disposition"); 					
		    		 if(header==null){
		    			 //普通响应数据,非文件下载
		    			 cdoHttpResponse.setResponseBytes(response.getEntity()==null?null:EntityUtils.toByteArray(response.getEntity()));
		    		 }else{
		    			 //文件输出		    			
		    			 HttpEntity resEntity = response.getEntity();		    			 	
	    				 if(resEntity==null){//body 为 empty	    					
	    					 logger.warn("download http entity is empty ....");
	    					 return cdoHttpResponse;
	    				 }	
	    				 download(header, response, cdoHttpResponse);
		    		 }					
				}
			  return cdoHttpResponse;
			}catch(Exception ex){
		    		 throw new IOException(ex.getMessage(),ex);
		   }
	 }
		//---------------------读取 响应报头,是否是cdo  header------------------//	
		private LinkedHashMap<String, FileInfo> readHeader(Header[] header){
				LinkedHashMap<String, FileInfo> linkMap=new	LinkedHashMap<String, FileInfo>(); 		
				for(int i=0;i<header.length;i++){
					if(header[i].getName().startsWith(Constants.CDO.HTTP_CDO_RESPONSE)){
						String[] filesField=header[i].getName().split(",");//分割cdo file key字段						
						String[] fileLen=header[i].getValue().split(";")[0].split(",");//分割  文件大小
						String[] filesName=header[i].getValue().split(";")[1].split(",");//分割 文件名
						for(int j=0;j<filesField.length;j++){							
							linkMap.put(filesField[j],new FileInfo(filesName[j], Long.parseLong(fileLen[j])));	
						}						
					}
				}
				return linkMap;
		}
	 //-----------------------------输出混合cdo 和文件数据-----------------------//	
	 private void outMixData(HttpResponse response,LinkedHashMap<String, FileInfo> linkMap,CDOHTTPResponse cdoHttpResponse) throws IOException{					 
			 InputStream inStream=null;
			 try{
				//输出CDO	
				 inStream = response.getEntity().getContent();	
	    		 Iterator<String> it=linkMap.keySet().iterator();
	    		 String strFieldName=it.next();
	    		 FileInfo fileInfo=linkMap.get(strFieldName);
	    		 cdoHttpResponse.setResponseBytes(outCDO(inStream,(int)fileInfo.getLength()));
	    		 linkMap.remove(strFieldName);	    		 
	   			 if(this.outStream==null){
	   			     //下载文件输出到临时目录 ，返回文件句柄给 cdoHttpResponse
	   				 outFile(inStream, linkMap,cdoHttpResponse);	 
	   			 }else{
	   			     //直接把下载文件 输出到指定流里,是否关闭由外部流指定
	   				 Iterator<Map.Entry<String,FileInfo>> iterator=linkMap.entrySet().iterator(); 
	   				 Entry<String,FileInfo> entry=iterator.next();
	   				 try{
	   					 outFile(inStream,entry.getValue().getLength(),this.outStream);
	   				 }finally{
	   					if(this.autoCloseStream){
	   						try{if(outStream!=null)outStream.close();}catch(Exception ex){};
	   					}
	   				 }
	   			 }	
			 }catch(Exception ex){
				 logger.error(ex.getMessage(),ex);
				 throw new IOException(ex.getMessage(),ex);
			 }finally{
				 if(inStream!=null)try{inStream.close();}catch(Exception e){}
			 }
		}
		//-----------------响应流中 CDO 内容---------------------//
		private byte[] outCDO(InputStream instream,int xmlLen) throws IOException{
            final ByteArrayBuffer buffer = new ByteArrayBuffer(xmlLen);           
            byte[] tmp =null;
			int len=xmlLen;		
			int maxSize=Constants.Byte.maxSize;		
			int l;
			while(len>0){					
			    int length=len>maxSize?maxSize:len;
			    tmp= new byte[length];
			    l=instream.read(tmp);
			    buffer.append(tmp, 0, l);
				len=len-maxSize;			
			}          
            return buffer.toByteArray();
		}
		//--------------下载到文件，临时存储的位置-------------------//
		private String getTmpPath(){
	   		 String tmpDirPath=System.getProperty("java.io.tmpdir");			            
		     File tmpDir= new File(tmpDirPath); 
		     if(!tmpDir.exists() || !tmpDir.isDirectory())  
		           tmpDir.mkdirs();  
		     return tmpDirPath;
		}
		
		public void download( Header header,HttpResponse response,CDOHTTPResponse cdoHttpResponse) throws IOException{
			 InputStream inStream=null;
			 try{						 
				 inStream = response.getEntity().getContent();	
   			 HeaderElement[] values = header.getElements();  
   			 for(int i=0;i<values.length;i++){			    		
   				 NameValuePair param = values[i].getParameterByName("filename");
   				 if(param!=null){
	    	               try {   
	   	                        String fileName = param.getValue();   	                       
	   	                        if(this.outStream==null){
	   	                        	//输出到临时文件
	   	                        	outFile(inStream, fileName,cdoHttpResponse);
	   	                        }else{
	   	                        	try{
	   	                        		//输出到指定流
	   	                        		outStream(inStream, this.outStream);
	   	                        	}finally{
	   	     	   						if(this.autoCloseStream){
	   	     	   							try{if(outStream!=null)outStream.close();}catch(Exception ex){};
	   	     	   						}
	   	                        	}
	   	                        }
   	                        break;
   	                   } catch (Exception e) {  
   	                	logger.error(e.getMessage(), e);
   	                 }   
   				 }
   			 }	
			 }catch(Exception ex){
				 logger.error(ex.getMessage(),ex);
				 throw new IOException(ex.getMessage(),ex);
			 }finally{
				 if(inStream!=null)try{inStream.close();}catch(Exception e){}
			 }			
		}
		//--------------------下载文件流完成后，把文件句柄返回给cdoHTTPResponse-------------//
		private void outFile(InputStream inStream,String fileName,CDOHTTPResponse cdoHTTPResponse) throws IOException{
			  String tmpDirPath=getTmpPath();
			  FileOutputStream fileout=null;
			  try{	   				
	   			  String suffix=fileName.substring(fileName.lastIndexOf("."),fileName.length());
	   			  String date=new SimpleDateFormat("yyyyMMddHHmmsss").format(new Date());
	   		      String tempPath =tmpDirPath+"/"+fileName.substring(0,fileName.lastIndexOf("."))+"_"+date+ suffix;				    				 
	   			  File tmpFile=new File(tempPath);
	   			  if(!tmpFile.exists())
	   			    	tmpFile.createNewFile();
	   			    
	   			 fileout= new FileOutputStream(tmpFile);  
	   			 outStream(inStream, fileout);
	   			 cdoHTTPResponse.addFile(fileName, tmpFile);
	         } catch (Exception e) {  
	        	throw new IOException(e.getMessage(),e);
	         }finally{
	        	 if(fileout!=null)try{fileout.close();}catch(Exception ex){}        	 
	        } 	  
		}
		//-------------------输出文件流-----------------------//
		private void outStream(InputStream inStream,OutputStream outputStream) throws IOException{
			  try{	   				 
	             byte[] buffer=new byte[Constants.Byte.maxSize*2];  
	             int ch = 0;  
	             while ((ch = inStream.read(buffer)) != -1) {  
	            	 outputStream.write(buffer,0,ch);  
	             }  	            
	             outputStream.flush(); 		         
	         } catch (Exception e) {  
	        	throw new IOException(e.getMessage(),e);
	         }	  
		}	
		//------------------下载到多个文件 输出到临时文件------------------//
		private void outFile(InputStream inStream,LinkedHashMap<String, FileInfo> linkMap,CDOHTTPResponse cdoResponse) throws IOException{
			  String tmpDirPath=getTmpPath();			
		     //输出流        
	   		 for(Iterator<Map.Entry<String,FileInfo>> it=linkMap.entrySet().iterator();it.hasNext();){
	   			 	Entry<String,FileInfo> entry=it.next();
	   			 	//输出文件
	   				String fileName=entry.getValue().getFileName();
	   			    String suffix=fileName.substring(fileName.lastIndexOf("."),fileName.length());
	   			    String date=new SimpleDateFormat("yyyyMMddHHmmsss").format(new Date());
	   				String tempPath =tmpDirPath+"/"+fileName.substring(0,fileName.lastIndexOf("."))+"_"+date+suffix;				    				 
	   			    File tmpFile=new File(tempPath);	   				
	   			    if(!tmpFile.exists())
	   			    	tmpFile.createNewFile();
	   			    FileOutputStream fileOutputStream=null;
	   			    try{
	   			    	fileOutputStream=new FileOutputStream(tmpFile);
	   				    outFile(inStream,entry.getValue().getLength(),fileOutputStream);
	   			      }finally{
	   			    	  try{if(fileOutputStream!=null)fileOutputStream.close();}catch(Exception ex){};
	   				 }	   				 
	   				 cdoResponse.addFile(entry.getKey(), tmpFile);
	   		 }		
		}
		//----------------------输出文件指定的长度 ----------------------//
		private void outFile(InputStream inStream,long fileLen,OutputStream outputStream) throws IOException{            
            try{		
				int maxSize=Constants.Byte.maxSize*2;		
				int l;
				long total=0;
				byte[] tmp =null;
				while(fileLen>total){	
					long remainLen=fileLen-total;
				    int length=remainLen>maxSize?maxSize:(int)remainLen;
				    tmp= new byte[length];
			        if((l = inStream.read(tmp)) != -1) {			        	
			        	outputStream.write(tmp,0, l);			            
			        }
			        total=total+l;		
				}  
				outputStream.flush();		       
            }catch(Exception ex){            	
            	throw new IOException(ex.getMessage(),ex);
            }
		}
		
		class FileInfo {
			String fileName;
			long  length;
			public FileInfo(String fileName,long length){
				this.fileName=fileName;
				this.length=length;
			}
			public String getFileName() {
				return fileName;
			}
			public void setFileName(String fileName) {
				this.fileName = fileName;
			}
			public long getLength() {
				return length;
			}
			public void setLength(long length) {
				this.length = length;
			}
			
		}	
	}
}