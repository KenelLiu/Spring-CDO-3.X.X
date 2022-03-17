package com.cdoPlugin.cdolib.servicebus;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cdoPlugin.cdolib.annotation.TransName;
import com.cdoPlugin.cdolib.database.IDataEngine;
import com.cdoPlugin.exception.TransException;
import com.cdoframework.cdolib.base.Return;
import com.cdoframework.cdolib.data.cdo.CDO;
/**
 * 增加事务传播属性
 * 为了方便处理,在定义了transName名称的方法上,
 * 默认 自动开启事务 @see TransName autoStartTransaction=true,且传播属性值为Propagation.REQUIRED
 * 在 XML里的SQLTrans的Propagation属性 ,可以定义为
 * REQUIRED,
 * SUPPORTS,
 * MANDATORY
 * REQUIRES_NEW,
 * NOT_SUPPORTED
 * NEVER
 * NESTED
 * @see com.cdoPlugin.transaction.Propagation 
 * @author Kenel
 */
public abstract class TransService implements ITransService
{	
	private static Log logger=LogFactory.getLog(TransService.class);
	private String strServiceName;
	protected IServiceBus serviceBus=null;
	protected IServicePlugin servicePlugin=null;
	protected IService service = null;
	
	protected Map<String, Method> transMap = new HashMap<String, Method>();


	final public void setServiceBus(IServiceBus serviceBus)
	{
		
		this.serviceBus=serviceBus;
	}

	final public void setServicePlugin(IServicePlugin servicePlugin)
	{
		this.servicePlugin=servicePlugin;
	}

	final public void setService(IService service)
	{
		this.service = service;
	}
	final public IService getService()
	{
		return this.service;
	}
	public Return init()
	{	
		return Return.OK;
	}
	
	@Override
	public void inject(ITransService child) {
		if(child != null)
		{
			Class<?> cls = child.getClass();
			Method[] methods = cls.getMethods();
			TransName transName = null;
			String name = null;
			for(Method method : methods) {
				// 查找所有带@TransName方法
				if(method.isAnnotationPresent(TransName.class)) {
					transName = method.getAnnotation(TransName.class);					
					name = transName.name();		
					if(name == null || name.equals("")) {
						name = method.getName();
					}
					// 一个服务类禁止出现重名的transName
					if(transMap.put(name, method) != null) {
						logger.error("存在同名的transName："+ name);
						System.exit(-1);
					}
				}
			}
		}
	}
	
		
	/**
	 * 设置服务名
	 * @param strServiceName
	 */
	final public void setServiceName(String strServiceName)
	{
		this.strServiceName = strServiceName;
	}

	/**
	 * 取服务名
	 * @return
	 */
	final public String getServiceName()
	{
		return this.strServiceName;
	}

	public void destroy()
	{
	}

	public void handleEvent(CDO cdoEvent)
	{
	}
		

	@Override
	public final Return processTrans(CDO cdoRequest, CDO cdoResponse) throws TransException {
		String strTransName = cdoRequest.getStringValue(ITransService.TRANSNAME_KEY);
		Method method = null;
		if((method = transMap.get(strTransName)) != null) {
			try{
				return (Return) method.invoke(this, cdoRequest, cdoResponse);			
			}catch(Throwable e){
				logger.error(strTransName+":函数调用发生错误,message="+e.getMessage(),e);
				throw new TransException(strTransName+":函数调用发生错误,message="+e.getMessage(),e);
			}
		} 
		return null;
	}
	
	
	@Override
	public boolean containsTrans(String strTransName) {
		return transMap.containsKey(strTransName);
	}

	
		
}
