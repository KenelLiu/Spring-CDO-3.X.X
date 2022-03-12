package com.cdo.example;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.cdoframework.cdolib.data.cdo.CDO;

public class CDOFieldTest {
	private static Log logger=null;
	@Before
	public void setUp() throws Exception {
		ConfigurationSource source;
		try {
			String pathFile="E:/Spring-CDO-3.X.X-Example/conf/activitiApi/log4j2-spring.xml";
			//方法1 使用 public ConfigurationSource(InputStream stream) throws IOException 构造函数
			source = new ConfigurationSource(new FileInputStream(pathFile));
			//方法2 使用 public ConfigurationSource(InputStream stream, File file)构造函数
			//File config=new File("D:\\log4j2.xml");
			//source = new ConfigurationSource(new FileInputStream(config),config);
			//方法3 使用 public ConfigurationSource(InputStream stream, URL url) 构造函数
			//String path="D:\\log4j2.xml";
			//source = new ConfigurationSource(new FileInputStream(path),new File(path).toURL());
			//source.setFile(new File("D:\log4j2.xml"));
			//source.setInputStream(new FileInputStream("D:\log4j2.xml"));
			Configurator.initialize(null, source); 
			logger=LogFactory.getLog(CDOFieldTest.class);
		}catch(Exception ex){
			logger.info(ex.getMessage(),ex);
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {		
		CDO cdo=ExampleCDO.getCDO();
		cdo.setNullValue("NullKey");
		cdo.setStringArrayValue("StrArr", new String[]{ "数组元素1", null, "数组元素3",null});
		CDO cdoOut=new CDO();
		cdoOut.copyFrom(cdo);
		cdoOut.setCDOValue("cdo", cdo.clone());
		
		cdoOut.setCDOArrayValue("cdoArr", new CDO[]{cdo.clone(),cdo.clone()});		
		CDO x=new CDO();
		x.copyFrom(cdoOut.toXML());
		logger.info(x.toXMLWithIndent());
	}


}
