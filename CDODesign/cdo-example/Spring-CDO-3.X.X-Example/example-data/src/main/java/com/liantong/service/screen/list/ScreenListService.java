package com.liantong.service.screen.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cdo.util.page.PageUtil;
import com.cdoframework.cdolib.annotation.TransName;
import com.cdoframework.cdolib.base.Return;
import com.cdoframework.cdolib.data.cdo.CDO;
import com.cdoframework.cdolib.servicebus.TransService;

public class ScreenListService extends TransService{

	@TransName(name = "getListNMSResource")
	public Return getListNMSResource(CDO cdoRequest, CDO cdoResponse){
		PageUtil.initPage(cdoRequest, cdoResponse);
		return this.service.handleDataTrans(cdoRequest, cdoResponse);			
	}	
	
	@TransName(name = "getListDeviceState")
	public Return getListDeviceState(CDO cdoRequest, CDO cdoResponse){
		PageUtil.initPage(cdoRequest, cdoResponse);
		return this.service.handleDataTrans(cdoRequest, cdoResponse);			
	}	
	
	@TransName(name = "getListLTPrdOIdFlow")
	public Return getListLTPrdOIdFlow(CDO cdoRequest, CDO cdoResponse){
		PageUtil.initPage(cdoRequest, cdoResponse);
		return this.service.handleDataTrans(cdoRequest, cdoResponse);			
	}	
	
	@TransName(name = "getListNMSAlarm")
	public Return getListNMSAlarm(CDO cdoRequest, CDO cdoResponse){
		PageUtil.initPage(cdoRequest, cdoResponse);
		return this.service.handleDataTrans(cdoRequest, cdoResponse);			
	}	
	
	@TransName(name = "getListNodeDevice")
	public Return getListNodeDevice(CDO cdoRequest, CDO cdoResponse){
		PageUtil.initPage(cdoRequest, cdoResponse);
		Return ret=this.service.handleDataTrans(cdoRequest, cdoResponse);
		if(ret.getCode()!=Return.OK.getCode())
			return ret;
		List<CDO> cdosData=cdoResponse.getCDOListValue("cdosData");
		StringBuilder tblId=new StringBuilder();
		for(int i=0;i<cdosData.size();i++){
			CDO data=cdosData.get(i);
			if(i==0){
				tblId.append("SELECT "+data.getLongValue("oId")+" AS oId,"+data.getLongValue("id")+" AS id,"+data.getLongValue("deviceId")+" AS deviceId \r\n ");				
			}else{
				tblId.append("UNION  SELECT "+data.getLongValue("oId")+" AS oId,"+data.getLongValue("id")+" AS id,"+data.getLongValue("deviceId")+" AS deviceId \r\n ");
			}
		}
		CDO cdoReq=new CDO();	
		cdoReq.setStringValue(TRANSNAME_KEY, "getNodeDeviceDetail");
		cdoReq.setStringValue("tblId", "("+tblId+") tblId");
		ret=this.service.handleDataTrans(cdoReq, cdoResponse);
		if(ret.getCode()!=Return.OK.getCode())
			return ret;
		List<CDO> cdosDetail=cdoResponse.getCDOListValue("cdosDetail");
		cdoResponse.remove("cdosDetail");
		mergeNodeDetail(cdosData, cdosDetail);
		return ret;
	}	
	
	private void mergeNodeDetail(List<CDO> cdosData,List<CDO> cdosDetail){
		Map<String,List<CDO>> detailMap=new HashMap<String,List<CDO>>();
		List<CDO> detalList=null;
		for(int i=0;i<cdosDetail.size();i++){
			CDO data=cdosDetail.get(i);
			String key=data.getLongValue("oId")+"-"+data.getLongValue("id")+"-"+data.getLongValue("deviceId");
			if(!detailMap.containsKey(key)){
				detalList=new ArrayList<CDO>();
			}else{
				detalList=detailMap.get(key);
			}
			data.remove("oId");
			data.remove("id");
			data.remove("deviceId");
			detalList.add(data);
			
			detailMap.put(key, detalList);
		}
		for(int i=0;i<cdosData.size();i++){
			CDO data=cdosData.get(i);
			String key=data.getLongValue("oId")+"-"+data.getLongValue("id")+"-"+data.getLongValue("deviceId");
			List<CDO> detail=detailMap.get(key);
			if(detail==null){
				detail=new ArrayList<CDO>();
			}
			data.setCDOListValue("detail", detail);			
		}
	}
	
}
