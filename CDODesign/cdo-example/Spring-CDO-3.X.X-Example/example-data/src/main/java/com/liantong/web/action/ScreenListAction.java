package com.liantong.web.action;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdo.business.BusinessService;
import com.cdoframework.cdolib.base.Return;
import com.cdoframework.cdolib.data.cdo.CDO;
import com.cdoframework.cdolib.servicebus.ITransService;
import com.liantong.bean.screen.LTDevice;
import com.liantong.bean.screen.LTPrdOId5Mins;
import com.liantong.bean.screen.NMSCurrentAlarm;
import com.liantong.bean.screen.NMSManagedResource;
import com.liantong.bean.screen.NodeDevice;
import com.liantong.common.Constants;
import com.liantong.web.CDO2Field;

/**
 * 大屏获取列表数据
 * 
 * @author KenelLiu
 */
@Controller
@RequestMapping("/api")
public class ScreenListAction {
    private static Log logger=LogFactory.getLog(ScreenListAction.class);

    @Autowired
    private  HttpServletRequest request;
    
    @RequestMapping(value = "/getScreenListData",method ={RequestMethod.GET,RequestMethod.POST},produces={"application/json;charset=utf-8"})
    @ResponseBody
    public String getScreenListData(){
    	String commandType=request.getParameter("commandType")==null?
    			"":request.getParameter("commandType").trim();
        JSONObject retJson=new JSONObject();        
        JSONObject dataJson=new JSONObject();
        CDO cdoRequest=null;
        CDO cdoResponse=null;
        try {       
        	cdoRequest=new CDO();
        	cdoResponse=new CDO();
        	int nPageIndex=request.getParameter("nPageIndex")==null?1:Integer.parseInt(request.getParameter("nPageIndex").trim());
        	int nPageSize=request.getParameter("nPageSize")==null?50:Integer.parseInt(request.getParameter("nPageSize").trim());
        	if(nPageIndex<1)nPageIndex=1;
        	if(nPageSize<20)nPageSize=20;
        	if(nPageSize>100)nPageSize=100;
        	cdoRequest.setIntegerValue("nPageIndex", nPageIndex);
        	cdoRequest.setIntegerValue("nPageSize", nPageSize);
        	cdoRequest.setStringValue(ITransService.SERVICENAME_KEY, "ScreenService"); 
        	Class<?> cls=null;
        	switch(commandType){
        		case "syncNMSResource":// 设备表/子设备表（ nms_managed_resource ）
        			cdoRequest.setStringValue(ITransService.TRANSNAME_KEY, "getListNMSResource");
        			cls=NMSManagedResource.class;
        			break;
        		case "syncLTDeviceState"://设备状态表(lt_devicerawdatatable)
        			cdoRequest.setStringValue(ITransService.TRANSNAME_KEY, "getListDeviceState");
        			cls=LTDevice.class;
        			break;
        		case "syncLTPrdOIdFlow"://设备端口流量 lt_prd_oId_5mins
        			cdoRequest.setStringValue(ITransService.TRANSNAME_KEY, "getListLTPrdOIdFlow");
        			cls=LTPrdOId5Mins.class;
        			break;
        		case "syncNMSAlarm"://告警结构 nms_currentalarm 
        			cdoRequest.setStringValue(ITransService.TRANSNAME_KEY, "getListNMSAlarm");
        			cls=NMSCurrentAlarm.class;
        			break;
        		case "syncNodeDevice"://节点链路及详情
        			cdoRequest.setStringValue(ITransService.TRANSNAME_KEY, "getListNodeDevice");
        			cls=NodeDevice.class;
        			break;
        		case "":
        			retJson.put(Constants.RET_API_CODE, Constants.RET_API_PARAM_NOT_EXISTS);        			
        			retJson.put(Constants.RET_MESSAGE, "未定义参数值commandType,请检查接口参数定义");
        			retJson.put("data", dataJson);
        			break;
        		default:
        			retJson.put(Constants.RET_API_CODE, Constants.RET_API_PARAM_NOT_EXISTS);        			
        			retJson.put(Constants.RET_MESSAGE, "不支持参数值commandType["+commandType+"],请检查接口参数定义");
        			retJson.put("data", dataJson);
        	}
        	if(retJson.isNull(Constants.RET_API_CODE)){        		
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
