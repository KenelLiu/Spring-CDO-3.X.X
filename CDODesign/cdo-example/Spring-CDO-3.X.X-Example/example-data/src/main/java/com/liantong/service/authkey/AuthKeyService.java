package com.liantong.service.authkey;

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;
import com.cdoframework.cdolib.annotation.TransName;
import com.cdoframework.cdolib.base.Return;
import com.cdoframework.cdolib.data.cdo.CDO;
import com.cdoframework.cdolib.servicebus.TransService;

public class AuthKeyService extends TransService {
	private static Log logger=LogFactory.getLog(AuthKeyService.class);
	
	
	/**
	 * OA审核流程
	 * @param cdoRequest
	 * @param cdoResponse
	 * @return
	 */
	@TransName(name = "saveApplyOAAudit")
	public Return saveApplyOAAudit(CDO cdoRequest, CDO cdoResponse){				
		return this.service.handleDataTrans(cdoRequest, cdoResponse);			
	}
	
	
}

