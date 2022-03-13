package com.cdo.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.FileSystemXmlApplicationContext;
//import org.springframework.stereotype.Component;

public class SpringContextUtil implements ApplicationContextAware{
	private static ApplicationContext applicationContext;
	//FileSystemXmlApplicationContext
	//ClassPathXmlApplicationContext
	//org.springframework.web.context.support.WebapplicationContextUtil.
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextUtil.applicationContext=applicationContext;		
	}
	
	public static ApplicationContext getApplicationContext() {
	        return applicationContext;
	 }

	public static Object getBean(String name){		
	        return getApplicationContext().getBean(name);
	    }
    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }
    //=========操作系统编码=====//
	protected static String strCharset;

	public static String getCharset()
	{
		return strCharset;
	}

	public static void setCharset(String _strCharset)
	{

		
		strCharset=_strCharset;
	}
}
