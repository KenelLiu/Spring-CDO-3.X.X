package com.cdoPlugin.cdolib.servicebus;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cdoPlugin.cdolib.database.DataServiceParse;
import com.cdoPlugin.cdolib.database.TransDefine;
import com.cdoPlugin.cdolib.database.xsd.SQLTrans;
import com.cdoPlugin.exception.TransException;
import com.cdoframework.cdolib.base.Return;
import com.cdoframework.cdolib.data.cdo.CDO;

/**
 * 
 * @author KenelLiu
 *
 */
public class Service implements IService
{

	//内部类,所有内部类在此声明----------------------------------------------------------------------------------

	//静态对象,所有static在此声明并初始化------------------------------------------------------------------------	
	private static Log logger=LogFactory.getLog(Service.class);
	private static Log logerStatis=LogFactory.getLog("transStatistics");	
	//内部对象,所有在本类中创建并使用的对象在此声明--------------------------------------------------------------
	private Map<String,TransDefine> hmTransDefine;

	//属性对象,所有在本类中创建，并允许外部访问的对象在此声明并提供get/set方法-----------------------------------
	//引用对象,所有在外部创建并传入使用的对象在此声明并提供set方法-----------------------------------------------
	private String strServiceName;
	private DataServiceParse dataServiceParse;
	private IServicePlugin servicePlugin;
	private IServiceBus serviceBus; 
	// 存放所有的普通的transService对象
	private Map<String, List<ITransService>> hmServiceMap;

	public void setServiceName(String strServiceName)
	{
		this.strServiceName = strServiceName;
	}
	public String getServiceName()
	{
		return this.strServiceName;
	}
	public void setDataServiceParse(DataServiceParse dataServiceParse)
	{
		this.dataServiceParse = dataServiceParse;
	}
	public Map<String, List<ITransService>> getTransServiceMap(){
		return this.hmServiceMap;
	}
	
	//内部方法,所有仅在本类或派生类中使用的函数在此定义为protected方法-------------------------------------------

	//私有方法 所有仅在本类或派生类中使用的函数在此定义为private方法-------------------------------------------

	public Return executeDataServiceTrans(String strTransName,CDO cdoRequest,CDO cdoResponse) throws SQLException
	{
		TransDefine transDefine = this.hmTransDefine.get(strTransName);
		if(transDefine==null)
		{
			return null;
		}
		SQLTrans sqlTrans = transDefine.getSqlTrans();
		if(sqlTrans==null)
		{
			return null;
		}		
		return this.dataServiceParse.handleTrans(sqlTrans,cdoRequest,cdoResponse);
	}
	//公共方法,所有可提供外部使用的函数在此定义为public方法------------------------------------------------------
	
	public void addTransService(ITransService transService)
	{
		addService(transService);
	}

	
	private void addService(ITransService service) {
		List<ITransService> services = null;
		synchronized (hmServiceMap) {
			if(hmServiceMap.get(service.getServiceName()) == null){
				services = new ArrayList<ITransService>();
				hmServiceMap.put(service.getServiceName(), services);
			} else {
				services = hmServiceMap.get(service.getServiceName());
			}
		}
		services.add(service);
		service.inject(service);
	}
	public TransDefine putTransDefine(String transName,TransDefine transDefine)
	{
		return this.hmTransDefine.put(transName,transDefine);
		
	}

	public Return init(String strServiceName,IServicePlugin servicePlugin,IServiceBus serviceBus)
	{
		this.strServiceName = strServiceName;	
		this.servicePlugin = servicePlugin;
		this.serviceBus = serviceBus;
		return Return.OK;
	}
	//接口实现,所有实现接口函数的实现在此定义--------------------------------------------------------------------


	/**
	 * @throws SQLException 
	 * @throws InvocationTargetException 
	 * @see {@link com.cdoPlugin.cdolib.servicebus.IService#handleDataTrans(com.cdoframework.cdolib.data.cdo.CDO, com.cdoframework.cdolib.data.cdo.CDO)}}
	 */
	public Return handleDataTrans(CDO cdoRequest,CDO cdoResponse) throws SQLException
	{
		String strTransName = cdoRequest.getStringValue(ITransService.TRANSNAME_KEY);
		return this.executeDataServiceTrans(strTransName,cdoRequest,cdoResponse);
	}


	/**
	 * @throws TransException 
	 * @throws SQLException 
	 * @see {@link com.cdoPlugin.cdolib.servicebus.IService#handleTrans(com.cdoframework.cdolib.data.cdo.CDO, com.cdoframework.cdolib.data.cdo.CDO)}}
	 */
	public Return handleTrans(CDO cdoRequest,CDO cdoResponse) throws TransException, SQLException{
		// 根据serviceName直接定位Service
		long beginTime =0l;
		if(logerStatis.isInfoEnabled())
			beginTime=System.currentTimeMillis();
		String strTransName = cdoRequest.getStringValue(ITransService.TRANSNAME_KEY);		
		Return ret = null;
		List<ITransService> transServices = this.hmServiceMap.get(strServiceName);
		//在TransService或 ActiveService调用  strTransName方法,若在class里未找到执行方法，则直接调用DataService  
		if(transServices != null) {
			for(ITransService transService : transServices){
				if(transService.containsTrans(strTransName)){
					ret = transService.processTrans(cdoRequest, cdoResponse);
					if(ret != null) {
						break;
					}
				}
			}
		}				
		if(ret==null){		
			try{		
				ret = this.executeDataServiceTrans(strTransName,cdoRequest,cdoResponse);							
			}catch(Throwable e){				
				logger.error("When handle data service "+strServiceName+"."+strTransName,e);
				throw new SQLException("When handle data service "+strServiceName+"."+strTransName, e);
			}
		}		
		if(logerStatis.isInfoEnabled()) {
			long duration = System.currentTimeMillis() - beginTime;
			logerStatis.info(" handle "+strServiceName+"."+strTransName+",process time="+duration+"ms");
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.cdoframework.cdolib.servicebus.IService#handleEvent(com.cdoframework.cdolib.data.cdo.CDO)
	 */
	public void handleEvent(CDO cdoEvent)
	{//此处需要在循环体中处理异常,以保证事件都能通知到
		for(Iterator<Map.Entry<String, List<ITransService>>> it=hmServiceMap.entrySet().iterator();it.hasNext();){
			 List<ITransService> list=it.next().getValue();
			 for(int i=0;i<list.size();i++){
				 try{
					 list.get(i).handleEvent(cdoEvent);
					}catch(Exception e){
						logger.error("handleEvent:"+e.getMessage(),e);
					}
			 }
		}			

	}

	public void destroy()
	{		
		
	}

	/**
	 * 启动 active服务
	 * @return
	 */
	public Return start(){
	
		return Return.OK;
	}

	public void stop(){
		
	}
	//事件处理,所有重载派生类的事件类方法(一般为on...ed)在此定义-------------------------------------------------

	//事件定义,所有在本类中定义并调用，由派生类实现或重载的事件类方法(一般为on...ed)在此定义---------------------

	//构造函数,所有构造函数在此定义------------------------------------------------------------------------------

	public Service()
	{		
		hmTransDefine	= new HashMap<String,TransDefine>(30); 			
		hmServiceMap = new HashMap<String, List<ITransService>>();
	}

}
