package com.cdo.redis.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
public class RedisConfig implements IConfig{
	private static Log logger = LogFactory.getLog(RedisConfig.class);

	private static Map<String, String> configMap = new LinkedHashMap<String, String>();
	
	
	public static String getConfigValue(String key) {
		return configMap.get(key);
	}

	public static void setConfigValue(String key, String value) {
		configMap.put(key, value);
	}
	
	public static Set<Map.Entry<String, String>> entrySet(){
		return configMap.entrySet();
	}
	
	public static Map<String, String> getConfig(){
		return configMap;
	}
	
	/**
	 * 1 加载指定redisFileName文件
	 * @param prjName 项目名称
	 * @param log4jFileName log4j的文件名
	 */
	public static void loadRedis(String prjName,String parentPath,String redisFileName){
		//---重新加载日志配置文件--//
		InputStream stream=null;	
		if(prjName==null) prjName="";
		String file="";
		try {
			file=parentPath+"/"+redisFileName;								
			Properties redis=new Properties();
			File redisFile=new File(file);
			if(redisFile.exists()&&redisFile.isFile()){
				stream=new FileInputStream(redisFile);
				redis.load(stream);	

				Iterator<Object> iterator = redis.keySet().iterator();
				while (iterator.hasNext()) {
					String key = (String) iterator.next();
					String value = redis.getProperty(key);
					configMap.put(key, value);
				}
				logger.info(prjName+"加载redis config:"+file+"........");				
			 }				
		} catch (Exception e) {
			logger.error(prjName+" 加载redis config["+file+"]数据异常:"+e.getMessage(), e);
		}finally{
			if(stream!=null)try{stream.close();}catch(Exception ex){};
		}
	}
}
