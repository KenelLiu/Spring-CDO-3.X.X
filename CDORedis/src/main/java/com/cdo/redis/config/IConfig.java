package com.cdo.redis.config;

public interface IConfig {
	//启动项目  需要在 vm里设置参数  如：-DACS_CONFIG_FILE=/path/to/ACSCenter.conf
	public static final String ACS_CONFIG_FILE="ACS_CONFIG_FILE";
	public static final String ACSAdmin_CONFIG_FILE="ACSAdmin_CONFIG_FILE";
	public static final String ACSLOG_CONFIG_FILE="ACSLOG_CONFIG_FILE";
	public static final String ACSAPI_CONFIG_FILE="ACSApi_CONFIG_FILE";
	public static final String CDO_CONFIG_FILE="CDO_CONFIG_FILE";
	public static final String TASK_CDO_CONFIG_FILE="TASK_CDO_CONFIG_FILE";
	
	public static final String CPE_CONFIG_FILE="CPE_CONFIG_FILE";
	
	//---前端---//
	public static final String FILE_ACSAdmin="ACSAdmin.conf";
	public static final String FILE_ACSLog="ACSLog.conf"; 
	public static final String FILE_ACSCenter="ACSCenter.conf"; 
	public static final String FILE_ACSApi="ACSApi.conf";
	//---后端------//
	public static final String FILE_CDO="CDO.conf";	
	//----日志---//
	public static final String FILE_ACSLog4j="ACSLog4j.properties";
	//---redis--//
	public static final String FILE_Redis="redis.properties";

}
