package com.liantong.web.servlet;
import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;
import com.cdo.business.BusinessService;
import com.cdo.util.resource.GlobalResource;
import com.cdo.util.system.SystemPropertyUtil;
import com.cdoframework.cdolib.base.Return;
import com.liantong.config.AdminConfig;
import com.liantong.config.IConfig;
import com.liantong.web.action.common.Constants;

/**
 */
public class WebConfigLoadServlet extends HttpServlet {

	private static final long serialVersionUID = -2660000154191149013L;
	private static  Log logger=LogFactory.getLog(AdminConfig.class);
	final String prjName="activitiApi";
	@Override
	public void init() throws ServletException {		
		Return ret = Return.OK;
		try {
			//初始配置
			/**
			String pathFile="E:/Spring-CDO-3.X.X-Example/conf/activitiApi/log4j2-spring.xml";
			ConfigurationSource source = new ConfigurationSource(new FileInputStream(pathFile));
			Configurator.initialize(null, source); 
			logger=LogFactory.getLog(AdminConfig.class);
			**/
			super.init();
			AdminConfig.initResource(IConfig.Activiti_API_CONFIG_FILE,IConfig.FILE_activitiApi);						
			this.getServletContext().setAttribute(Constants.Config.domain, AdminConfig.getConfigValue(Constants.Config.domain));	
			this.getServletContext().setAttribute(Constants.Config.version, AdminConfig.getConfigValue(Constants.Config.version));
			this.getServletContext().setAttribute(Constants.Config.downloadUrl, AdminConfig.getConfigValue(Constants.Config.downloadUrl));	
			this.getServletContext().setAttribute(Constants.REALM, Constants.AUTHOR_REALM);	
			
			//设置log4j输出名
			System.setProperty("prjLog4j",prjName);
			//AdminConfig.reloadLog4j(prjName,IConfig.FILE_log4j2_xml);
			//绑定配置
			GlobalResource.bundleLocalCDOEnv(AdminConfig.getConfigPath());	
			logger.info(prjName+" 初始配置["+AdminConfig.getConfigPath()+"]数据完成........");
			//============加载数据库配置==============//			
			BusinessService app = BusinessService.getInstance();
			String strBusResource=GlobalResource.cdoConfig.getString("servicebusResource.path");	
			//没有指定路径  读取默认配置
			if(strBusResource==null){
				 String confPath=AdminConfig.getConfigPath();
				 String parent=new File(confPath).getParent();
				 strBusResource=parent+"/servicebusResource.xml";				
				 File file=new File(strBusResource);
				 if(!file.exists()||!file.isFile())
					 strBusResource=null;			 
			}
			if(strBusResource==null || strBusResource.equals("")){
				ret=Return.valueOf(-1, prjName+" init Environment failed! parameter[servicebusResource.path,frameworkResource.path] is not found");
			}else{
				ret=app.start(strBusResource);
			}		
		} catch (Exception e) {
			logger.fatal(prjName+" 初始配置["+AdminConfig.getConfigPath()+"]数据异常:"+e.getMessage(), e);
		}
		if(ret.getCode()!=Return.OK.getCode()){
			logger.fatal(ret.getText());
			logger.fatal("||*****************************************||\r\n||*****************************************||\r\n||  started faild and will exit            ||\r\n||*****************************************||\r\n||*****************************************||");
			System.exit(-1);
			return;
		}
		//========加载加密key========//
		//	loadKeys();
		//=========启动定时任务=========//
		//SchduleStrap.getInstance().initApi();
		//==============定时每天02:30执行==============//
		//SchduleStrap.getInstance().addSchedule("02:30",new SchudleAreaSafetyMain());
		if(logger.isInfoEnabled())
			logger.info(prjName+" business service started-----------------");
	}
}
