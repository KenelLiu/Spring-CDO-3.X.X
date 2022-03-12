package com.liantong.web.filter;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.cdo.business.BusinessService;
import com.cdo.util.codec.SHA;
import com.cdoframework.cdolib.base.Return;
import com.cdoframework.cdolib.data.cdo.CDO;
import com.cdoframework.cdolib.servicebus.ITransService;
import com.liantong.web.AuthKeyUtil;
import com.liantong.common.Constants;

public class OAuthFilter implements Filter {
	private static Log logger=LogFactory.getLog(OAuthFilter.class);	
	private static final long Millis=TimeUnit.MINUTES.toMillis(60);
	
	//private Set<String> filterUrl ;
	public void init(FilterConfig filterConfig) throws ServletException {
		//filterUrl = new HashSet<String>();
	}
	
	private void loadAuth(){
		loadAuth(false,null);
	}
	private void loadAuth(boolean reload,HttpServletResponse res){

		try{			
			if(AuthKeyUtil.mapAuth.isEmpty() || reload){
				//=========重新加载数据=======//
				CDO cdoRequest=new CDO();
				CDO cdoResponse=new CDO();
				cdoRequest.setStringValue(ITransService.SERVICENAME_KEY,"AuthKeyService");
				cdoRequest.setStringValue(ITransService.TRANSNAME_KEY, "getAllAuthKey");
				Return ret=BusinessService.getInstance().handleTrans(cdoRequest, cdoResponse);
				JSONObject data=new JSONObject();
				if(ret.getCode()==Return.OK.getCode()){
					List<CDO> cdoList=cdoResponse.getCDOListValue("cdosData");					
					Map<String,CDO> map=cdoList.stream().collect(
							  Collectors.toMap(param->param.getStringValue("appKey"),
									  param->param,(oldValue,newValue)->newValue));					
					AuthKeyUtil.mapAuth.clear();
					AuthKeyUtil.mapAuth.putAll(map);
					
					data.put(Constants.RET_API_CODE, Constants.RET_API_CODE_SUCCESS);
					data.put(Constants.RET_MESSAGE, "重新加载Authkey数据成功");	
					logger.info("加载Authkey数据成功...");
				}else{
					data.put(Constants.RET_API_CODE, Constants.RET_API_EXCEPTION);
					data.put(Constants.RET_MESSAGE, "加载Authkey数据失败");	
					logger.error("加载Authkey数据失败..."+ret);
				}
				//=============通过重新加载接口获取数据,需响应=========//
				if(reload && res!=null){
					outResponse(res, data);
				}
			}
		}catch(Exception ex){
			logger.error("加载Authkey数据失败："+ex.getMessage(), ex);
		}
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain){
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String servletPath=req.getServletPath();
		JSONObject data=null;
		try{			
			if(servletPath.equals("/reloadAuthkey")){
				//==========重载数据========//
				loadAuth(true, res);
				return;
			}	
			if(servletPath.startsWith("/repair/") || servletPath.startsWith("/manual/")
				||servletPath.startsWith("/coor/") || servletPath.startsWith("/test/")){
				chain.doFilter(request, response);
				return;
			}	
			//=====未加载过Authkey数据,需先加载下,然后进行验签=====//
			loadAuth();
			if(!signSHA1(req, res)){
				return;
			}       	
			chain.doFilter(request, response);		
		}catch(Exception ex){
			logger.error("出现异常："+ex.getMessage(), ex);
			if(data==null)data=new JSONObject();
			data.put(Constants.RET_API_CODE, Constants.RET_API_EXCEPTION);
			data.put(Constants.RET_MESSAGE, "验证发生异常:"+ex.getMessage());	
			outResponse(res, data);
		}					
	}
	
	private boolean signSHA1(HttpServletRequest req,HttpServletResponse res){
		JSONObject data=new JSONObject();

		String appKey=AuthKeyUtil.getHeadValue(req,"appKey");
		String timestamp=AuthKeyUtil.getHeadValue(req,"timestamp");			
		String reqSignature=AuthKeyUtil.getHeadValue(req,"signature");
		String reqAppSecret=AuthKeyUtil.getHeadValue(req,"appSecret");
		if(reqAppSecret!=null && reqAppSecret.trim().length()>0){
			data.put(Constants.RET_API_CODE, Constants.RET_API_EXCEPTION);
			data.put(Constants.RET_MESSAGE, "不符安全规范,切勿传输授权码[appSecret]");				
			outResponse(res, data);
			return false;
		}
		if(appKey.equals("") || timestamp.equals("") ||reqSignature.equals("")){
			data.put(Constants.RET_API_CODE, Constants.RET_API_PARAM_NOT_EXISTS);
			data.put(Constants.RET_MESSAGE, "缺少验签参数");				
			outResponse(res, data);
			return false;
		}
		String appSecret= AuthKeyUtil.getAppSecret(appKey);
		if(appSecret.equals("")){
			data.put(Constants.RET_API_CODE, Constants.RET_API_PARAM_NOT_EXISTS);
			data.put(Constants.RET_MESSAGE, "根据appKey["+appKey+"]服务端未找到对应secret值");				
			outResponse(res, data);
			return false;
		}
		try{
			long nTimestamp=Long.valueOf(timestamp);
			long sysTimestamp=System.currentTimeMillis();
			long val=Math.abs(sysTimestamp-nTimestamp);
			if(val>Millis){
				data.put(Constants.RET_API_CODE, Constants.RET_API_EXCEPTION);
				data.put(Constants.RET_MESSAGE, "参数[timestamp]时间不在有效时间范围内");				
				outResponse(res, data);	
				return false;
			}
		}catch(Exception e){
			data.put(Constants.RET_API_CODE, Constants.RET_API_EXCEPTION);
			data.put(Constants.RET_MESSAGE, "参数[timestamp]不能转换成数字");				
			outResponse(res, data);			
			logger.error(e.getMessage(),e);
			return false;
		}
		String strOrgin=appKey+appSecret+timestamp;			
		String sign=SHA.encryptSHA1(strOrgin.getBytes());
		if(!sign.equalsIgnoreCase(reqSignature)){
			data.put(Constants.RET_API_CODE, Constants.RET_API_Valid_SIGN_Fail);
			data.put(Constants.RET_MESSAGE, "签名验证失败");	
			outResponse(res, data);
			return false;
		}   
		return true;
	}

	
	private void outResponse(HttpServletResponse res,JSONObject data){
		res.addHeader("Access-Control-Allow-Origin", "*");
		res.addHeader("P3P","CP=CAO PSA OUR");					
		res.setCharacterEncoding("UTF-8");		
		res.setContentType("text/html;charset=UTF-8");
		res.setHeader("Pragma", "No-cache");
		res.setHeader("Cache-Control", "no-cache");
		res.setDateHeader("Expires", 0);
		PrintWriter  out=null;
		try{
		  out = res.getWriter();
		  out.write(data.toString());  			 
		  out.close();
		}catch(Exception ex){
			logger.error(ex.getMessage(), ex);
		}finally{
			if(out!=null)
				out.close();
		}
	}

}