
package com.cdoPlugin.cdolib.servicebus;

import com.cdoPlugin.exception.TransException;
import com.cdoframework.cdolib.base.Return;
import com.cdoframework.cdolib.data.cdo.CDO;

/**
 * @author Frank
 */

public interface ITransService extends IServiceObject
{
	String	SERVICENAME_KEY="strServiceName";
	String	TRANSNAME_KEY="strTransName";	
	int   ASYN_CALL_ID=-99;
		
	/**
	 * 初始化服务对象操作
	 * @return 初始化结果
	 */
	Return init();

	/**
	 * 销毁事务对象操作
	 */
	void destroy();
	
	/**
	 * 处理一个事务, 这个方法给注解方式使用	
	 * @param cdoRequest 事务请求对象，事务名用strTransName区分开
	 * @param cdoResponse 事务应答对象
	 * @return 事务处理结果，如果事务名不被支持，则返回null
	 * @throws TransException 
	 */
	public Return processTrans(CDO cdoRequest, CDO cdoResponse) throws TransException;

	/**
	 * 处理一个事务
	 * @param cdoRequest 事务请求对象，事务名用strTransName区分开
	 * @param cdoResponse 事务应答对象
	 * @return 事务处理结果，如果事务名不被支持，则返回null
	 */
	void handleEvent(CDO cdoEvent);
	
	/**
	 * 设置服务名
	 * @param strServiceName
	 */
	void setServiceName(String strServiceName);
	
	/**
	 * 取服务名
	 * @return
	 */
	String getServiceName();
	
	/**
	 * 把子类注入到父类中，完成解析注解的transname。
	 * @param child
	 */
	void inject(ITransService child); 
	
	/**同一服务,是否已经包含该transName**/
	boolean containsTrans(String strTransName);

	
}
