package com.cdoPlugin.cdolib.servicebus;

import java.sql.SQLException;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cdoPlugin.cdolib.database.xsd.DataService;
import com.cdoPlugin.cdolib.servicebus.xsd.ServiceConfig;
import com.cdoPlugin.cdolib.servicebus.xsd.TransService;
import com.cdoPlugin.exception.TransException;
import com.cdoframework.cdolib.base.Return;
import com.cdoframework.cdolib.data.cdo.CDO;
import com.cdoframework.cdolib.util.Utility;
/**
 * 
 * @author KenelLiu
 *
 */
public class ServicePlugin implements IServicePlugin
{

	// 内部类,所有内部类在此声明----------------------------------------------------------------------------------

	// 静态对象,所有static在此声明并初始化------------------------------------------------------------------------	
	private static Log logger=LogFactory.getLog(ServicePlugin.class);
	// 内部对象,所有在本类中创建并使用的对象在此声明--------------------------------------------------------------
	private HashMap<String,Service> hmService;
	// 属性对象,所有在本类中创建，并允许外部访问的对象在此声明并提供get/set方法-----------------------------------
	// 引用对象,所有在外部创建并传入使用的对象在此声明并提供set方法-----------------------------------------------
	private String strPluginName;
	private ServiceBus serviceBus;
	
	public String getPluginName()
	{
		return strPluginName;
	}

	public void setPluginName(String strPluginName)
	{
		this.strPluginName=strPluginName;
	}

	public void setServiceBus(ServiceBus _serviceBus)
	{
		this.serviceBus=_serviceBus;
	}
	// 内部方法,所有仅在本类或派生类中使用的函数在此定义为protected方法-------------------------------------------

	// 私有方法 所有仅在本类或派生类中使用的函数在此定义为private方法-------------------------------------------

	// 公共方法,所有可提供外部使用的函数在此定义为public方法------------------------------------------------------
	
	
	public void init(String strPluginName,com.cdoPlugin.cdolib.servicebus.xsd.ServicePlugin pluginDefine)
					throws Exception{
		
		
		int nServiceCount = pluginDefine.getServiceConfigCount();
		if(nServiceCount==0){
			throw new Exception("no server define");
		}			
		this.strPluginName = strPluginName;
		// =============初始化Service====================//
		ServiceConfig[] serviceConfigs = pluginDefine.getServiceConfig();				
		for(ServiceConfig sc:serviceConfigs){
			Service service = new Service();
			service.setDataServiceParse(serviceBus.getDataServiceParse());			
			if(logger.isInfoEnabled()){logger.info("init service: "+ sc.getId());}
			Return ret = service.init(sc.getId(),this,this.serviceBus);
			if(ret.getCode()!=Return.OK.getCode()){
				throw new Exception("init service error : "+ret.getText());
			}
			IService oldService = this.hmService.put(sc.getId(),service);
			IService oldService2 = serviceBus.addService(service);
			if(oldService!=null || oldService2!=null){
				//TODO 清除对象
				throw new Exception("duplicate serviceId :" + service.getServiceName());
			}	
		}
		// =============初始化DataService====================//
		int nDataServiceCount=pluginDefine.getDataServiceCount();
		try{
			for(int i=0;i<nDataServiceCount;i++)
			{
				com.cdoPlugin.cdolib.servicebus.xsd.DataService dataServiceDefine=pluginDefine.getDataService(i);
				if(logger.isInfoEnabled()){logger.info("parse trans xml :"+dataServiceDefine.getResource());}
				String strSQLTransXML=Utility.readTextResource(dataServiceDefine.getResource(),"utf-8");
				if(strSQLTransXML==null){
					throw new Exception("Invalid resource "+dataServiceDefine.getResource());
				}

				DataService dataService=null;
				try{
					dataService=DataService.fromXML(strSQLTransXML);
				}catch(Exception e){
					logger.error(e.getMessage(), e); 
					throw new Exception("SQL XML parse error: "+dataServiceDefine.getId());
				}
				Service service = this.hmService.get(dataServiceDefine.getId());
				if(service==null){
					throw new Exception("init service error : can not find service id "+dataServiceDefine.getId());
				}
				Return ret = dataService.init(dataServiceDefine.getId(),service,this,serviceBus);
				if(ret.getCode()!=Return.OK.getCode()){
					//TODO 清除对象
					throw new Exception("init service error : "+ret.getText());
				}
			}
		}catch(Exception e){
			//TODO 清除对象
			throw e;
		}
		//=================== 初始化TransService================//
		int nTransServiceCount=pluginDefine.getTransServiceCount();
		try{
			for(int i=0;i<nTransServiceCount;i++){				
				TransService transService=pluginDefine.getTransService(i);
				Service service = this.hmService.get(transService.getId());
				if(service==null){
					throw new Exception("init service error : can not find service id: "+transService.getId());
				}
				if(logger.isInfoEnabled()){logger.info("init trans service "+ transService.getClassPath());}
				ITransService transServiceObject=((ITransService)Class.forName(transService.getClassPath())
								.newInstance());
				
				transServiceObject.setServicePlugin(this);
				transServiceObject.setServiceBus(serviceBus);
				transServiceObject.setServiceName(transService.getId());
				transServiceObject.setService(service);
				
				Return ret=transServiceObject.init();
				if(ret.getCode()!=Return.OK.getCode()){
					throw new Exception("Init service object failed: "+transService.getId());
				}
				service.addTransService(transServiceObject);
			}
		}catch(Exception e){
			throw e;
		}
	}

	

	// 接口实现,所有实现接口函数的实现在此定义--------------------------------------------------------------------
	public Return handleTrans(CDO cdoRequest,CDO cdoResponse) throws TransException, SQLException
	{

		String strServiceName=cdoRequest.exists(ITransService.SERVICENAME_KEY)?cdoRequest.getStringValue(ITransService.SERVICENAME_KEY):null;
		String strTransName=cdoRequest.exists(ITransService.TRANSNAME_KEY)?cdoRequest.getStringValue(ITransService.TRANSNAME_KEY):null;	
		if(strServiceName==null||strTransName==null ||
				strServiceName.length()==0|| strTransName.length()==0){
			logger.error("[strServiceName,strTransName] can not be empty,strServiceName="+strServiceName+",strTransName="+strTransName);
			return Return.valueOf(-1, "[strServiceName,strTransName] can not be empty", "[strServiceName,strTransName] can not be empty");
		}		
		IService service = this.hmService.get(strServiceName);
		if(service==null){
			//service 未找到
			logger.error("can not find service named "+strServiceName+",Trans not supported:"+strServiceName+'.'+strTransName);			
			return Return.valueOf(-1,"can not find service named "+strServiceName);
		}
		Return ret = service.handleTrans(cdoRequest,cdoResponse);
		if(ret==null)
		{// Trans 不支持 或 未找到
			logger.error(" Return is null,maybe not find trans named "+strTransName+",Trans not supported:"+strServiceName+'.'+strTransName);	
			return Return.valueOf(-1, strServiceName+"."+strTransName+" Return is null,maybe not find trans  named");
		}
		return ret;
	}
	


	public Return handleDataTrans(CDO cdoRequest,CDO cdoResponse) throws SQLException
	{
		String strServiceName=cdoRequest.exists(ITransService.SERVICENAME_KEY)?cdoRequest.getStringValue(ITransService.SERVICENAME_KEY):null;
		String strTransName=cdoRequest.exists(ITransService.TRANSNAME_KEY)?cdoRequest.getStringValue(ITransService.TRANSNAME_KEY):null;	
		if(strServiceName==null||strTransName==null ||
				strServiceName.length()==0|| strTransName.length()==0){
			logger.error("[strServiceName,strTransName] can not be empty,strServiceName="+strServiceName+",strTransName="+strTransName);
			return Return.valueOf(-1, "[strServiceName,strTransName] can not be empty", "[strServiceName,strTransName] can not be empty");
		}		

		IService service = this.hmService.get(strServiceName);
		if(service==null)
		{
			logger.error("can not find service named "+strServiceName+",Trans not supported:"+strServiceName+'.'+strTransName);			
			return Return.valueOf(-1,"can not find service named "+strServiceName);
		}
		return service.handleDataTrans(cdoRequest,cdoResponse);
	}
	// 事件处理,所有重载派生类的事件类方法(一般为on...ed)在此定义-------------------------------------------------

	// 事件定义,所有在本类中定义并调用，由派生类实现或重载的事件类方法(一般为on...ed)在此定义---------------------

	// 构造函数,所有构造函数在此定义------------------------------------------------------------------------------

	public ServicePlugin(){
		hmService 			= new HashMap<String,Service>(5);
	}
}
