package com.cdoPlugin.cdolib.servicebus;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.cdoPlugin.exception.TransException;
import com.cdoframework.cdolib.base.Return;
import com.cdoframework.cdolib.data.cdo.CDO;

public interface IService
{
	/**
	 * 处理一个事务
	 * @param cdoRequest 事务请求对象，事务名用strTransName区分开
	 * @param cdoResponse 事务应答对象
	 * @return 事务处理结果，如果事务名不被支持，则返回null
	 * @throws TransException 
	 * @throws SQLException 
	 */
	Return handleTrans(CDO cdoRequest,CDO cdoResponse) throws TransException, SQLException;

	/**
	 * 调用SQL事务处理的方法
	 * ServiceBus会根据Request内的数据和ServiceBus.xml的配置内容，自动查找到对应的插件，调用其对象提供的处理方法
	 * @param cdoRequest 事务请求对象
	 * @param cdoResponse 事务应答对象
	 * @return 事务处理结果
	 * @throws SQLException 
	 */
	Return handleDataTrans(CDO cdoRequest,CDO cdoResponse) throws SQLException;
	
	/**
	 * 处理一个事务
	 * @param cdoRequest 事务请求对象，事务名用strTransName区分开
	 * @param cdoResponse 事务应答对象
	 * @return 事务处理结果，如果事务名不被支持，则返回null
	 */
	void handleEvent(CDO cdoEvent);

	String getServiceName();
	
	Map<String, List<ITransService>> getTransServiceMap();
	Return start();
	void stop();
	void destroy();	
}
