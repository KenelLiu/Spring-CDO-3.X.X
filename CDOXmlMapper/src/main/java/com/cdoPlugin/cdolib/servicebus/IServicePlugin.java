
package com.cdoPlugin.cdolib.servicebus;

import java.sql.SQLException;

import com.cdoPlugin.exception.TransException;
import com.cdoframework.cdolib.base.Return;
import com.cdoframework.cdolib.data.cdo.CDO;

/**
 * @author Frank
 * 
 * 本接口定义插件对象的行为
 */
public interface IServicePlugin{

	String getPluginName();
		
	Return handleTrans(CDO cdoRequest,CDO cdoResponse) throws TransException, SQLException;
	
	Return handleDataTrans(CDO cdoRequest,CDO cdoResponse) throws SQLException;
	
}
