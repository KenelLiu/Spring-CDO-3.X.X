package com.liantong.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

public class AdminConfig implements IConfig{
	private static Log logger = LogFactory.getLog(AdminConfig.class);

	private static Map<String, String> configMap = new LinkedHashMap<String, String>();
	private static String configPath;
	private static boolean isClassPath;//是否是读取 class根路径下配置文件
	
	public static void initResource(String varPath) throws IOException{
		initResource(varPath,null);
	}
	/**
	 * 1 优先查找classpath 根路径 是否有 classPathFile 文件 ,如果有 且配置项的size>0
	 *   则使用classpath数据
	 *   
	 * 2 根路径classpath 不存在配置文件或者配置文件的size=0,则查找
		-DvarPath=/path/to/file.conf  指定的文件				
	 * @param varPath 启动时配置的值 如 :-DACS_CONFIG_FILE=/path/to/ACSCenter.conf
	 * @param classPathFile class根目录下文件 
	 * @throws IOException 
	 */
	public static void initResource(String varPath,String classPathFile) throws IOException {
		//默认读取class根目录配置文件		
		try{
			isClassPath=true;
			URL  url = Thread.currentThread().getContextClassLoader().getResource("");
			logger.info("current classPath :======"+url);
			String classPath = url.toString().substring(6);
		    String system=System.getProperty("os.name").toLowerCase();			 
			if(system.startsWith("windows")){
				classPath = url.toString().substring(6);
			}else{
				classPath = url.toString().substring(5);
			}
				  
			if(!classPath.endsWith("/")){
				classPath = classPath +"/"+ classPathFile;
			} else {
				classPath= classPath + classPathFile;
			}	
			
			File f=new File(classPath);
			
			if(!f.exists() || !f.isFile() ){				
				isClassPath=false;
				configPath=System.getProperty(varPath);
				logger.warn("classPath File not found:"+classPath+"\r\nload System.getProperty("+varPath+") file:"+configPath);
				initPath();
			}else{
				//class根路径文件存在,则加载文件
				logger.info("load classPath File:"+classPath);
				load(new FileInputStream(f));
				if(configMap.size()>0){
					configPath=classPath;
					return;
				}
				isClassPath=false;
				configPath=System.getProperty(varPath);
				initPath();
				if(configMap.size()==0){					
					logger.warn("load classPath File key size=0:"+classPath+",read -D"+varPath+" reload file ["+configPath+"]");
				}
			}
			//-------加载配置文件-------
			load(new FileInputStream(configPath));
		} catch (Exception e) {
			if(isClassPath){
				throw new IOException("init classpath file="+classPathFile+" error:"+e.getMessage(),e);
			}else{
				throw new IOException("init -D"+varPath+"="+configPath+" file error:"+e.getMessage(),e);
			}
		} 
	}
	
	private static void initPath(){
		StringBuilder sb=new StringBuilder();
		char ch;
		for(int i=0;i<configPath.length();i++){
			ch=configPath.charAt(i);
			if(ch=='{'){
				if(i>0 && configPath.charAt(i-1)=='$'){
					sb=new StringBuilder();
				}
			}else if(ch=='}'){
				if(sb!=null){
					String value=System.getProperty(sb.toString());
					if(value!=null && !value.equals(""))
						configPath=configPath.replace("${"+sb.toString()+"}",value);
				}					
				sb=null;
			}else{
				if(sb!=null)
					sb.append(ch);
			}
		}
	}
	/**
	 * 用类加载器读取文件信息
	 * @param classPathFile class根路径的配置文件
	 * @throws IOException 
	 */
	private  static void initResourceClassPath(String classPathFile) throws IOException{
		ClassLoader loader = AdminConfig.class.getClassLoader();
		load(loader.getResourceAsStream(classPathFile));
	}
	
	private static void load(InputStream in) throws IOException{
		Properties properties = new Properties();
		try {
			properties.load(in);
		} catch (Exception e) {
			throw new IOException(e.getMessage(),e);
		}
		Iterator<Object> iterator = properties.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			String value = properties.getProperty(key);
			configMap.put(key, value);
		}
	}
	
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
	
	public static String getConfigPath(){
		return configPath;
	}
	/**
	 * 读取的配置文件   是否classPath 根目录下文件
	 * @return
	 */
	public static boolean isClassPath(){
		return isClassPath;
	}
	/**
	 * 1 优先加载指定log4jFileName文件,如果未加载到,则加载log4j.properties
	 * @param prjName 项目名称
	 * @param log4jFileName log4j的文件名
	 */
	public static void reloadLog4j(String prjName,String log4jFileName){
		//---重新加载日志配置文件--//
		InputStream stream=null;	
		if(prjName==null) prjName="";
		String file="";
		try {
			String busfullPath=AdminConfig.getConfigPath();
			String parent=new File(busfullPath).getParent();
			
			file=parent+"/"+log4jFileName;								
			Properties log4j=new Properties();
			File logFile=new File(file);
			if(logFile.exists()&&logFile.isFile()){
				stream=new FileInputStream(logFile);
				log4j.load(stream);		
				//重新设置log4j
				//String pathFile="F:/CDO/Spring-Log-CDO-3.X.X/CDOFramework/src/main/resources/log4j2-spring.xml";
				//方法1 使用 public ConfigurationSource(InputStream stream) throws IOException 构造函数
				ConfigurationSource source = new ConfigurationSource(stream);
				Configurator.initialize(null, source); 
				logger=LogFactory.getLog(AdminConfig.class);
				logger.info(prjName+" 重新加载log:"+file+"........");
				return;
			 }	
			
			
			file=parent+"/log4j2-spring.xml";								
			logFile=new File(file);
			if(logFile.exists()&&logFile.isFile()){
				stream=new FileInputStream(logFile);
				log4j.load(stream);		
				//重新设置log4j
				ConfigurationSource source = new ConfigurationSource(stream);
				Configurator.initialize(null, source); 	
				logger.info(prjName+"重新加载log:"+file+"........");
			}
			
		} catch (Exception e) {
			logger.error(prjName+" 重新配置log["+file+"]数据异常:"+e.getMessage(), e);
		}finally{
			if(stream!=null)try{stream.close();}catch(Exception ex){};
		}
	}
	

	/**
	 * 1 加载指定redisFileName文件
	 * @param prjName 项目名称
	 * @param log4jFileName log4j的文件名
	 */
	public static void reloadRedis(String prjName,String redisFileName){
		//---重新加载日志配置文件--//
		InputStream stream=null;	
		if(prjName==null) prjName="";
		String file="";
		try {
			String busfullPath=AdminConfig.getConfigPath();
			String parent=new File(busfullPath).getParent();
			
			file=parent+"/"+redisFileName;								
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
