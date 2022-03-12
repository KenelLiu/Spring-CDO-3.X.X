package com.cdo.util.http;

import java.io.FileInputStream;

import com.cdo.util.bean.Response;
import com.cdo.util.constants.Constants;
import com.cdo.util.exception.ResponseException;

public class HttpMainTest {

	public static void main(String[] args) {
		try {
			//PropertyConfigurator.configure(new FileInputStream("D:/DEV/Cloud/530/conf/1.0.0/activiti/activitiLog4j.properties"));
			System.setProperty(Constants.HTTP.SelfKeyStoreFile, "E:/download/my.keystore,E:/download/my2.keystore");
			System.setProperty(Constants.HTTP.SelfKeyStorePassword, "123456,234567");
			System.setProperty(Constants.HTTP.SelfIgnoreVerifyHost, "10.83.66.74,192.168.1.103");
			Response res=HttpUtil.get("https://10.83.66.74:3306");
			System.out.println(res.getResponseText());
			
			res=HttpUtil.get("https://www.baidu.com");
			System.out.println(res.getResponseText());
			
			res=HttpUtil.get("https://192.168.1.103");
			System.out.println(res.getResponseText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
