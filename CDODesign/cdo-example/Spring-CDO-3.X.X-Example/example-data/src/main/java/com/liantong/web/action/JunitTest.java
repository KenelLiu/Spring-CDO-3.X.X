package com.liantong.web.action;

import com.cdo.business.BusinessService;
import com.cdo.pattern.Pattern;
import com.cdo.util.codec.MD5;
import com.cdo.util.common.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cdoframework.cdolib.base.Return;
import com.cdoframework.cdolib.data.cdo.CDO;
import com.cdoframework.cdolib.servicebus.ITransService;
import com.liantong.bean.screen.LTDevice;
import com.liantong.bean.screen.LTPrdOId5Mins;
import com.liantong.bean.screen.NMSCurrentAlarm;
import com.liantong.bean.screen.NMSManagedResource;
import com.liantong.bean.screen.NodeDevice;
import com.liantong.common.Constants;
import com.liantong.config.AdminConfig;
import com.liantong.web.CDO2Field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
/**
 * 接受OA审核流程
 * @author KenelLiu 
 */
@Controller
@RequestMapping("/test")
public class JunitTest {
    private static Log logger=LogFactory.getLog(JunitTest.class); 
    
	SimpleDateFormat dateTimeFmt=new SimpleDateFormat(Pattern.PATTERN_DATETIME);
    @Autowired
    private  HttpServletRequest request;
	@RequestMapping(value ="/workflow/paService/doCreateRequest", method = RequestMethod.POST,produces="application/json") 
	@ResponseBody
    public String doCreateRequest(HttpServletRequest request,HttpServletResponse response,Model model) throws IOException{
    	JSONObject retJson=new JSONObject();
    	String requestName="";
    	String workflowId="";
        try {        	        	
        	 requestName=request.getParameter("requestName");
        	 workflowId=request.getParameter("workflowId");
        	String mainData=request.getParameter("mainData");
        	JSONArray arrMain=new JSONArray(mainData);
        	for(int i=0;i<arrMain.length();i++){
        		JSONObject fieldData=arrMain.getJSONObject(i);
        		if(fieldData.getString("fieldName").equals("sqh")){
        			//是申请函
        			JSONArray arrFile=fieldData.getJSONArray("fieldValue");
        			for(int j=0;j<arrFile.length();j++){
        				JSONObject fileData=arrFile.getJSONObject(j);
        				String fileName=fileData.getString("fileName");
        				String fileContent=fileData.getString("filePath");
        				if(fileContent.startsWith("base64:")){
        					String dir=AdminConfig.getConfigValue(Constants.Config.uploadPath)+"/JunitTest";
        					File f=new File(dir);
        					if(!f.exists() || !f.isDirectory()){
        						f.mkdirs();
        					}
        					String fullPath=dir+fileName;        					
        					fileContent=fileContent.substring("base64:".length());        					
        					byte[] bytes=org.apache.commons.codec.binary.Base64.decodeBase64(fileContent);
        					//===输出文件======//
        					FileUtil.writeFile(bytes, new File(fullPath));        					
        				}
        				
        			}        			
        		}
        	}
        	retJson.put(Constants.RET_API_CODE, Constants.RET_API_CODE_SUCCESS);
        	retJson.put("requestName", requestName); 
        	retJson.put("workflowId", workflowId);            	            	
        	retJson.put("message", "处理成功");   
        }catch(Exception  ex) {
            logger.error("处理数据异常:"+ex.getMessage(), ex);
            try{
            	retJson.put(Constants.RET_API_CODE, Constants.RET_API_EXCEPTION);
            	retJson.put("requestName", requestName); 
            	retJson.put("workflowId", workflowId);            	            	
            	retJson.put("message", "处理数据发生异常,"+ex.getMessage());   
            	
            }catch(Exception e){}
        }
        return retJson.toString();
    }
	@RequestMapping(value = "/getListNodeDevice",method ={RequestMethod.GET,RequestMethod.POST},produces={"application/json;charset=utf-8"})
    @ResponseBody
    public String getScreenListData(){
        JSONObject retJson=new JSONObject();        
        JSONObject dataJson=new JSONObject();
        CDO cdoRequest=null;
        CDO cdoResponse=null;
        try { 
        	logger.info("开始===========");
        	cdoRequest=new CDO();
        	cdoResponse=new CDO();
        	int nPageIndex=request.getParameter("nPageIndex")==null?1:Integer.parseInt(request.getParameter("nPageIndex").trim());
        	int nPageSize=request.getParameter("nPageSize")==null?10:Integer.parseInt(request.getParameter("nPageSize").trim());
        	if(nPageIndex<1)nPageIndex=1;
        	if(nPageSize<20)nPageSize=20;
        	if(nPageSize>100)nPageSize=100;
        	cdoRequest.setIntegerValue("nPageIndex", nPageIndex);
        	cdoRequest.setIntegerValue("nPageSize", nPageSize);
        	cdoRequest.setStringValue(ITransService.SERVICENAME_KEY,"ScreenService");
 			cdoRequest.setStringValue(ITransService.TRANSNAME_KEY, "getListNodeDevice");
        	Class<?> cls=NodeDevice.class;
        	
        	Return ret=BusinessService.getInstance().handleTrans(cdoRequest, cdoResponse);
            if(ret.getCode()==Return.OK.getCode()){
            	long nCount=cdoResponse.getLongValue("nCount");                
            	JSONObject page=new JSONObject();
            	page.put("nCount", nCount);
            	page.put("nPageIndex", nPageIndex);
            	page.put("nPageSize", nPageSize);
            	dataJson.put("page", page);
            	JSONArray arr=new JSONArray();
            	dataJson.put("list", arr);    	
            	List<CDO> dataList=cdoResponse.getCDOListValue("cdosData");   
            	if(dataList.size()>0){
                	retJson.put(Constants.RET_API_CODE, Constants.RET_API_CODE_SUCCESS);
                	retJson.put(Constants.RET_MESSAGE, "获取数据成功");
            		CDO2Field.setField(arr, dataList,cls);
            	}else{
                	retJson.put(Constants.RET_API_CODE, Constants.RET_API_NOT_FOUND);
                	retJson.put(Constants.RET_MESSAGE, "数据不存在");                		
            	}
            	retJson.put("data", dataJson);
            }else{
            	retJson.put(Constants.RET_API_CODE, Constants.RET_API_EXCEPTION);                	
            	retJson.put(Constants.RET_MESSAGE, "获取数据失败");
            	retJson.put("data", dataJson);
            }        
        	
        }catch(Exception  ex) {
            logger.error("处理数据异常:"+ex.getMessage(), ex);
            try{
            	retJson.put(Constants.RET_API_CODE, Constants.RET_API_EXCEPTION);
            	retJson.put(Constants.RET_MESSAGE, "处理数据发生异常,"+ex.getMessage()); 
            	retJson.put("data", dataJson);
            }catch(Exception e){}
        }finally {        	
        	deepRelease(cdoRequest, cdoResponse);
        }
        return retJson.toString();
    }

    private void deepRelease(CDO cdoRequest,CDO cdoResponse){
    	if(cdoRequest!=null)
    		cdoRequest.deepRelease();
    	if(cdoResponse!=null)
    		cdoResponse.deepRelease();
    }  		
}
