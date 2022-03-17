package com.cdoPlugin.cdolib.servicebus;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cdoPlugin.cdolib.database.DataServiceParse;
import com.cdoPlugin.exception.TransException;
import com.cdoframework.cdolib.base.Return;
import com.cdoframework.cdolib.data.cdo.CDO;
import com.cdoframework.cdolib.util.Utility;

/**
 * 使用spring jcl log
 * @author KenelLiu
 *
 */
public class ServiceBus implements IServiceBus
{

	//静态对象,所有static在此声明并初始化------------------------------------------------------------------------	
	private static Log logger=LogFactory.getLog(ServiceBus.class);
	//内部对象,所有在本类中创建并使用的对象在此声明--------------------------------------------------------------
	private ServicePlugin[] plugins;	
	private DataServiceParse dataServiceParse=new DataServiceParse();
	private HashMap<String,Object> hmSharedData;
	private ReentrantReadWriteLock lockSharedData;		
	private HashMap<String,IService> hmService;
	
	public DataServiceParse getDataServiceParse()
	{
		return this.dataServiceParse;
	}
	//引用对象,所有在外部创建并传入使用的对象在此声明并提供set方法-----------------------------------------------
	private String strDefaultDataGroupId;

	public void setGroupId(String strGroupId)
	{
		this.strDefaultDataGroupId	= strGroupId;
	}
	public String getDefaultDataGroupId()
	{
		return this.strDefaultDataGroupId;
	}
	
	public HashMap<String,IService> getHmService(){
		
		return this.hmService;
	} 
	
	
	// 公共方法,所有可提供外部使用的函数在此定义为public方法------------------------------------------------------
	
	/**
	 * ServiceBus.xsd 由 ServiceBusResource.xsd  和 PluginsConfig.xsd 组成，分拆主要是为了部署方便，将不变和经常变化的分开。
   	 * ServiceBusResource.xsd 主要定义了 关系型数据库源
   	 * PluginsConfig.xsd   定义了使用插件的配置
   	 * 将ServiceBus.xml转化为java对象	
	 * @param strServiceBusXML  
	 * @return
	 */
	public Return init(String strServiceBusXML){
		//==========将XML转换成对象==========//
		com.cdoPlugin.cdolib.servicebus.xsd.PluginsConfig pluginsConfig=null;
		try{
			pluginsConfig=com.cdoPlugin.cdolib.servicebus.xsd.PluginsConfig.fromXML(strServiceBusXML);
		}catch(Exception e){
			logger.error("When parse serviceBus.xml , caught exception: ",e);
			return Return.valueOf(-1,"Init ServiceBus Failed: "+e.getLocalizedMessage());
		}
						
		//============加载插件对象=============//
		if(logger.isInfoEnabled()){logger.info("Staring load  plugins ....................");}
		int nPluginCount=pluginsConfig.getPluginXMLResourceCount();
		this.plugins=new ServicePlugin[nPluginCount];
		try{
			for(int i=0;i<nPluginCount;i++){
				String strXMLResource=pluginsConfig.getPluginXMLResource(i);
				if(logger.isInfoEnabled()){logger.info("loading "+strXMLResource+"..............................");}
				String strXML=Utility.readTextResource(strXMLResource,"utf-8");
				if(strXML==null){
					throw new Exception("Resource "+strXMLResource+" invalid");
				}
				com.cdoPlugin.cdolib.servicebus.xsd.ServicePlugin servicePluginDefine=com.cdoPlugin.cdolib.servicebus.xsd.ServicePlugin.fromXML(strXML);
				this.plugins[i]=new ServicePlugin();
				//=============初始化插件======//	
				this.plugins[i].setServiceBus(this);
				this.plugins[i].init(i+"",servicePluginDefine);
			}
		}catch(Exception e){
			plugins	=null;
			logger.error("when parse plugin ,caught Exception: ",e);
			return Return.valueOf(-1,"Init ServiceBus Failed: "+e.getLocalizedMessage());
		}
		if(logger.isDebugEnabled()){logger.debug("load  plugins successfully....................");}
		
		return Return.OK;
	}
	 	

	public void destroy(){
		
		for(Iterator<Map.Entry<String, IService>> it=hmService.entrySet().iterator();it.hasNext();){
			it.next().getValue().destroy();
		}
	}
	
	/**
	 * 启动 Plugin服务
	 * @return
	 */
	public Return start()
	{
		for(Iterator<Map.Entry<String, IService>> it=hmService.entrySet().iterator();it.hasNext();){
			it.next().getValue().start();
		}
		
		return Return.OK;
	}

	/**
	 * 停止Business服务
	 *
	 */
	public void stop(){
		//Stop Plugin
		for(Iterator<Map.Entry<String, IService>> it=hmService.entrySet().iterator();it.hasNext();){
			it.next().getValue().stop();
		}		
		
	}

	/**
	 * 调用事务处理的方法
	 * ServiceBus会根据Request内的数据和ServiceBus.xml的配置内容，自动查找到对应的插件，调用其对象提供的处理方法
	 * @param cdoRequest 事务请求对象
	 * @param cdoResponse 事务应答对象
	 * @return 事务处理结果
	 * @throws SQLException 
	 * @throws TransException 
	 */
	public Return handleTrans(CDO cdoRequest,CDO cdoResponse) throws TransException, SQLException
	{

		String strServiceName=cdoRequest.exists(ITransService.SERVICENAME_KEY)?cdoRequest.getStringValue(ITransService.SERVICENAME_KEY):null;
		String strTransName=cdoRequest.exists(ITransService.TRANSNAME_KEY)?cdoRequest.getStringValue(ITransService.TRANSNAME_KEY):null;	
		if(strServiceName==null||strTransName==null ||
				strServiceName.length()==0|| strTransName.length()==0){
			logger.error("[strServiceName,strTransName] can not be empty,strServiceName="+strServiceName+",strTransName="+strTransName);
			return Return.valueOf(-1, "[strServiceName,strTransName] can not be empty", "[strServiceName,strTransName] can not be empty");
		}
		//-------开始事务---------------//
		onTransStarted(strServiceName,strTransName,cdoRequest);		
		// 正常处理
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
		onTransFinished(strServiceName,strTransName,cdoRequest,cdoResponse,ret);
		return ret;
	}

	public Object getSharedData(String strId)
	{
		if(strId==null)
		{
			return null;
		}
		
		Object objValue=null;
		
		ReentrantReadWriteLock.ReadLock lock=lockSharedData.readLock();
		lock.lock();
		objValue=hmSharedData.get(strId);
		lock.unlock();
		
		return objValue;
	}

	public void setSharedData(String strId,Object objValue)
	{
		if(strId==null)
		{
			return;
		}
		
		ReentrantReadWriteLock.WriteLock lock=lockSharedData.writeLock();
		lock.lock();
		hmSharedData.put(strId,objValue);
		lock.unlock();
	}
	
	public IService addService(IService service){
		return this.hmService.put(service.getServiceName(),service);
	}
 
	//接口实现,所有实现接口函数的实现在此定义--------------------------------------------------------------------
	/**
	 * 调用事件处理的方法
	 * ServiceBus会根据Request内的数据和ServiceBus.xml的配置内容，自动查找到对应的插件，调用其对象提供的处理方法
	 * @param cdoEvent 事件对象
	 */
	public void handleEvent(CDO cdoEvent)
	{
		for(Iterator<Map.Entry<String, IService>> it=hmService.entrySet().iterator();it.hasNext();){
			it.next().getValue().handleEvent(cdoEvent);
		}
		onEventHandled(cdoEvent);
	}

	//事件处理,所有重载派生类的事件类方法(一般为on...ed)在此定义-------------------------------------------------

	//事件定义,所有在本类中定义并调用，由派生类实现或重载的事件类方法(一般为on...ed)在此定义---------------------
	private void onTransStarted(String strServiceName,String strTransName,CDO cdoRequest){
		
		if(logger.isDebugEnabled()){
			StringBuilder sb = new StringBuilder(50);
			sb.append("Starting handle ").append(" ServceName=").append(strServiceName).append(" transName=").append(strTransName).append("\r\n").append(cdoRequest.toString());
			logger.debug(sb.toString());	
			return; 
		}else if(logger.isInfoEnabled()){
			StringBuilder sb = new StringBuilder(50);
			sb.append("Starting handle ").append(" ServceName=").append(strServiceName).append(" transName=").append(strTransName);
			logger.info(sb.toString());				
		}
	}
	
	private void onTransFinished(String strServiceName,String strTransName,CDO cdoRequest,CDO cdoResponse,Return retResult){
		if(logger.isDebugEnabled()){
			StringBuilder sb = new StringBuilder(50);
			sb.append("End handle ").append(" ServiceName=").append(strServiceName).append(" transName=").append(strTransName).append("\r\n");
			sb.append(" Return code=").append(retResult.getCode()).append(" text=").append(retResult.getText()).append(" info=").append(retResult.getInfo());
			sb.append(" cdoResponse=").append(cdoResponse.toString());
			logger.debug(sb.toString());
			return;
		}else if(logger.isInfoEnabled()){
			StringBuilder sb = new StringBuilder(50);
			sb.append("End handle ").append(" ServiceName=").append(strServiceName).append(" transName=").append(strTransName).append("\r\n");
			sb.append(" Return code=").append(retResult.getCode()).append(" text=").append(retResult.getText()).append(" info=").append(retResult.getInfo());		
			logger.info(sb.toString());			
		}
	}


	public void onEventHandled(CDO cdoEvent)
	{
		try
		{
			if(logger.isDebugEnabled()){logger.debug(":Event handled:\r\n"+cdoEvent.toXML());}
		}
		catch(Exception e)
		{
			
		}
	}

	//构造函数,所有构造函数在此定义------------------------------------------------------------------------------
	public ServiceBus()
	{

		//请在此加入初始化代码,内部对象和属性对象负责创建或赋初值,引用对象初始化为null，初始化完成后在设置各对象之间的关系
		hmSharedData			=new HashMap<String,Object>();
		lockSharedData			=new ReentrantReadWriteLock();
		hmService 				= new LinkedHashMap<String,IService>(20);			
		dataServiceParse		=new DataServiceParse();		
	}



}
