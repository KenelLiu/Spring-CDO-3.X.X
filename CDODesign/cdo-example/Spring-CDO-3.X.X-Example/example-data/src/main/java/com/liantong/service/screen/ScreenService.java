package com.liantong.service.screen;

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;
import com.cdoframework.cdolib.annotation.TransName;
import com.cdoframework.cdolib.base.Return;
import com.cdoframework.cdolib.data.cdo.CDO;
import com.cdoframework.cdolib.servicebus.TransService;

public class ScreenService extends TransService {
	private static Log logger=LogFactory.getLog(ScreenService.class);
	
	/**
	 * 提供大屏时更新数据
	 * @param cdoRequest
	 * @param cdoResponse
	 * @return
	 */
	@TransName(name = "getDataByHour")
	public Return getDataByHour(CDO cdoRequest, CDO cdoResponse) {		
		return this.service.handleDataTrans(cdoRequest, cdoResponse);			
	}
	/**
	 * 提供大屏日更新数据
	 * @param cdoRequest
	 * @param cdoResponse
	 * @return
	 */
	@TransName(name = "getDataByDay")
	public Return getDataByDay(CDO cdoRequest, CDO cdoResponse) {		
		return this.service.handleDataTrans(cdoRequest, cdoResponse);			
	}
	/**
	 *  提供大屏月更新数据
	 * @param cdoRequest
	 * @param cdoResponse
	 * @return
	 */
	@TransName(name = "getDataByMonth")
	public Return getDataByMonth(CDO cdoRequest, CDO cdoResponse){				
		return this.service.handleDataTrans(cdoRequest, cdoResponse);			
	}	
}

