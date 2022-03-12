package com.liantong.config;

public interface IConfig {
	//启动项目  需要在 vm里设置参数  如：-DactivitiAdmin_CONFIG_FILE=/path/to/activitiAdmin.conf
	public static final String Activiti_CONFIG_FILE="activitiAdmin_CONFIG_FILE";
	public static final String Activiti_API_CONFIG_FILE="activitiApi_CONFIG_FILE";
	
	public static final String FILE_activitiAdmin="activitiAdmin.conf";
	public static final String FILE_activitiApi="activitiApi.conf";

	
	public static final String FILE_activitiLog4j="activitiLog4j.properties";
	public static final String FILE_log4j2_xml="log4j2-spring.xml";
	public static final String FILE_redis="redis.properties";	
	public static final String Key_HandOver="handOverTable";	
}
