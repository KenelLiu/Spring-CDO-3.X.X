package com.liantong.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.cdoframework.cdolib.data.cdo.CDO;

public class AuthKeyUtil {
	//==============数据在 OAuthFilter 里加载=========//
	public static Map<String,CDO> mapAuth=new java.util.concurrent.ConcurrentHashMap<String,CDO>(20);
	
	/**
	 * 获取参数 appKey,signature,timestamp的值
	 * 若在head里获取不到数据,则在url里获取
	 * @param req
	 * @param key
	 * @return
	 */
	public static String getHeadValue(HttpServletRequest req,String key){		
		String value=req.getHeader(key)==null?"":req.getHeader(key).trim();
		if(value.equals("")){
			value=req.getParameter(key)==null?"":req.getParameter(key).trim();
		}
		return value;
	}
	/**
	 * 根据appKey的值获取appSecret值 
	 * @return
	 */
	public static String getAppSecret(String appKey){
		CDO cdoData=mapAuth.get(appKey);
		if(cdoData==null) return "";
		return cdoData.getStringValue("appSecret").trim();
	}
	/**
	 * 根据appKey的值获取client值 
	 * @return
	 */	
	public static String getClient(String appKey){
		CDO cdoData=mapAuth.get(appKey);
		if(cdoData==null) return "";
		return cdoData.getStringValue("client").trim();
	}
}
