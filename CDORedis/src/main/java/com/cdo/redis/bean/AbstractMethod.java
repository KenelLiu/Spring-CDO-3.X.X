package com.cdo.redis.bean;

import com.cdoframework.cdolib.data.cdo.CDO;

public class AbstractMethod {
	protected String RequestId;
	protected CDO data;
	public static String JSON_KEY_Method="methodName"; 
	public String getRequestId() {
		return RequestId;
	}

	public void setRequestId(String requestId) {
		RequestId = requestId;
	}

	public CDO getData() {
		return data;
	}

	public void setData(CDO data) {
		this.data = data;
	}
	
	
}
