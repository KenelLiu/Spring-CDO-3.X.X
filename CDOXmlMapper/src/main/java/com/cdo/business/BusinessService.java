package com.cdo.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cdoPlugin.cdolib.servicebus.IServiceBus;
import com.cdoPlugin.cdolib.servicebus.ServiceBus;
import com.cdoframework.cdolib.base.Return;
import com.cdoframework.cdolib.data.cdo.CDO;
import com.cdoframework.cdolib.util.Utility;

/**
 * 创建业务处理的实例
 * 在一个jvm里  只有一个serviceBus 实例，初始化框架所有配置，读取业务开发插件
 * @author KenelLiu
 */
public class BusinessService
{
	//静态对象,所有static在此声明并初始化------------------------------------------------------------------------
	private static BusinessService instance=new BusinessService();
	private static final Log log=LogFactory.getLog(BusinessService.class);
	public static BusinessService getInstance()
	{//使用单列		
		return instance;
	}

	//内部对象,所有在本类中创建并使用的对象在此声明--------------------------------------------------------------
	private ServiceBus serviceBus;
	
	public IServiceBus getServiceBus(){
		return this.serviceBus;
	}
	private boolean bIsRunning;

	//属性对象,所有在本类中创建，并允许外部访问的对象在此声明并提供get/set方法-----------------------------------
	public boolean isRunning()
	{
		return this.bIsRunning;
	}

	//引用对象,所有在外部创建并传入使用的对象在此声明并提供set方法-----------------------------------------------

	//内部方法,所有仅在本类或派生类中使用的函数在此定义为protected方法-------------------------------------------

	//公共方法,所有可提供外部使用的函数在此定义为public方法------------------------------------------------------

	/**
	 * 读取  启动时 的配置文件  -DCDO_Plugin_CONFIG_FILE=${BASE_HOME}/conf/cdo.conf
	 * 
	 * @return
	 */
	public Return start(){
		String strPluginsConfigXMLFile=System.getProperty("CDO_Plugin_CONFIG_FILE");	//文件全路径	
		return start(strPluginsConfigXMLFile);
	}
	
	public Return start(String strPluginsConfigXMLFile){			
		return start(strPluginsConfigXMLFile,"cdoPluginsConfig.xml");
	}
	
	public Return start(String strPluginsConfigXMLFile,String pluginsConfigXmlFile){
		return start(strPluginsConfigXMLFile,pluginsConfigXmlFile,"UTF-8");
	}
	
	public Return start(String strPluginsConfigXMLFile,String pluginsConfigXmlFile,String encoding){
		if(this.bIsRunning==true){
			return Return.OK;
		}
		String strPluginsConfigXML=null;
		if(strPluginsConfigXMLFile!=null && !strPluginsConfigXMLFile.trim().equals("")){
			log.info("load CDO PluginsConfig from  "+strPluginsConfigXMLFile);
			strPluginsConfigXML = Utility.readTextResource(strPluginsConfigXMLFile,encoding,false);
		}else{
			log.info("load CDO PluginsConfig from classPath: "+pluginsConfigXmlFile);
			strPluginsConfigXML = Utility.readTextResource(pluginsConfigXmlFile,encoding);
		}	
		Return ret=serviceBus.init(strPluginsConfigXML.toString());	
		if(ret.getCode()!=Return.OK.getCode()){
			return ret;
		}
		
		ret=serviceBus.start();
		if(ret.getCode()!=Return.OK.getCode()){
			serviceBus.destroy();
			return ret;
		}
		
		this.bIsRunning=true;
		
		return Return.OK;
	}
	
	public void stop(){
		if(this.bIsRunning==false){
			return;
		}
		serviceBus.stop();
		serviceBus.destroy();
		this.bIsRunning=false;
	}

	public Return handleTrans(CDO cdoRequest,CDO cdoResponse){
		Return ret=serviceBus.handleTrans(cdoRequest,cdoResponse);
		if(ret==null){
			return Return.valueOf(-1,"Invalid request","System.Error");
		}
		return ret;
	}
	
	private BusinessService(){		
		serviceBus=new ServiceBus();
	}
	
}
