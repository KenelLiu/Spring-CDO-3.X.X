package com.cdo.util.http;
import java.io.File;
import java.nio.charset.CodingErrorAction;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Consts;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cdo.util.constants.Constants;
import com.cdo.util.exception.HttpException;

import com.cdo.util.system.SystemPropertyUtil;
public class HttpPool45 extends HttpPool{

	private static final Log logger=LogFactory.getLog(HttpUtil.class);
    private static  PoolingHttpClientConnectionManager poolHttp;
	private static  RequestConfig defaultRequestConfig;
    private static int retryTime=1;

	static {
		try{
			int socketTimeout=(SystemPropertyUtil.getInt(Constants.HTTP.SocketTimeout_MS,30))*1000;//默认 30秒 （单位毫秒）		
			int connectTimeout=(SystemPropertyUtil.getInt(Constants.HTTP.ConnectionTimeout_MS,30))*1000;//默认 30秒 （单位毫秒）
			int requestTimeout=(SystemPropertyUtil.getInt(Constants.HTTP.RequestTimeout_MS,30))*1000;//默认 30秒 （单位毫秒）
			
			int defaultMaxPerRoute= SystemPropertyUtil.getInt(Constants.HTTP.DefaultMaxPerRoute,100);//默认100
			int maxTotal=SystemPropertyUtil.getInt(Constants.HTTP.MaxTotal, 600);//默认600
			int retryTime=SystemPropertyUtil.getInt(Constants.HTTP.RetryTime, 1);//默认1
			String keyStoreFile=SystemPropertyUtil.get(Constants.HTTP.SelfKeyStoreFile, null);
			SSLContext sslContext=null;
			SSLConnectionSocketFactory sslConnFactory=null;
			if(keyStoreFile==null){
				sslContext = SSLContexts.custom().build();
				sslConnFactory=new SSLConnectionSocketFactory(sslContext);
			}else{
				String selfSplitChar=SystemPropertyUtil.get(Constants.HTTP.SelfSplitChar,",");				
				String keystorePassword=SystemPropertyUtil.get(Constants.HTTP.SelfKeyStorePassword, "123456");//访问keystore的密码		
				
				String[] keyStoreFiles=keyStoreFile.split(selfSplitChar);
				String[] passwords=keystorePassword.split(selfSplitChar);
				//=======增加了自定义keyStoreFile,还需保证能正常访问其他https站如 http://www.baidu.com==//
				X509TrustManager x509=new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                            X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            X509Certificate[] certs, String authType) {
                    }
                };
				//==============加载自定义证书的keystore文件及访问密码=========//
				org.apache.http.ssl.SSLContextBuilder sslBuilder=SSLContexts.custom();	
				List<TrustManager> trustManagerList=new ArrayList<TrustManager>();
				for(int i=0;i<keyStoreFiles.length;i++){
					if(keyStoreFiles[i]==null || keyStoreFiles[i].trim().equals("")){
						break;
					}
					//==========访问keystore密码,若没有设置默认密码============//
					String passwd=null;//默认密码
					if(i<passwords.length){
						passwd=passwords[i];
					}
					if(passwd==null || passwd.equals("")){
						passwd="123456";
					}				
					sslBuilder=sslBuilder.loadTrustMaterial(new File(keyStoreFiles[i]), passwd.toCharArray());	
					
					trustManagerList.add(x509);
				}
				
				sslContext=sslBuilder.build();
                TrustManager[] trustManager=trustManagerList.toArray(new TrustManager[trustManagerList.size()]);               
				//========添加了keyStoreFile,还需保证能正常访问其他https正常网站如 http://www.baidu.com
				sslContext.init(null,trustManager, null);
				//========自定义证书所在的服务器或ip 不需要进行验证==========//
				String ignoreVerifyHost=SystemPropertyUtil.get(Constants.HTTP.SelfIgnoreVerifyHost, "");
				SelfHostnameVerifier selfHostName=new SelfHostnameVerifier(Arrays.asList(ignoreVerifyHost.split(selfSplitChar)));
				sslConnFactory= new SSLConnectionSocketFactory(sslContext,selfHostName);
			}
	        	
	        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
	                .register("http", PlainConnectionSocketFactory.INSTANCE)
	                .register("https", sslConnFactory)
	                .build();	    
		    
	        // Create socket configuration
	        SocketConfig socketConfig = SocketConfig.custom()
	            .setTcpNoDelay(true)
	            .setSoReuseAddress(true)
	            .setSoKeepAlive(true)            
	            .build();
			
			 // Create message constraints
		    MessageConstraints messageConstraints = MessageConstraints.custom().build();
		            //.setMaxHeaderCount(200)
		            //.setMaxLineLength(2000)
		            
	       // Create connection configuration
	       ConnectionConfig connectionConfig = ConnectionConfig.custom()
	           .setMalformedInputAction(CodingErrorAction.IGNORE)
	           .setUnmappableInputAction(CodingErrorAction.IGNORE)
	           .setCharset(Consts.UTF_8)           
	           .setMessageConstraints(messageConstraints)
	           .build();
		    //--------创建连接池------------//	    
	        poolHttp= new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			poolHttp.setMaxTotal(maxTotal);
			poolHttp.setDefaultMaxPerRoute(defaultMaxPerRoute);
			
			poolHttp.closeExpiredConnections();
			poolHttp.closeIdleConnections(5, TimeUnit.MINUTES);      
//			poolHttp.setValidateAfterInactivity(60*1000);
			poolHttp.setDefaultSocketConfig(socketConfig);
			poolHttp.setDefaultConnectionConfig(connectionConfig);
			
	        // Use custom cookie store if necessary.
//	        CookieStore cookieStore = new BasicCookieStore();
	        // Use custom credentials provider if necessary.
	//        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
	        // Create global request configuration
			defaultRequestConfig= RequestConfig.custom()
	            .setCookieSpec(CookieSpecs.DEFAULT)	               
	            .setSocketTimeout(socketTimeout)
	            .setConnectTimeout(connectTimeout)
	            .setConnectionRequestTimeout(requestTimeout)
	            .setExpectContinueEnabled(Boolean.FALSE)
	            .build();
	//            .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
	//            .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))	            
//			   getHttpClient();
			  if(logger.isInfoEnabled())
				  logger.info("Initialize http4.5 pool,socketTimeout="+(socketTimeout/1000)+"s,connectTimeout="+(connectTimeout/1000)
						  +"s,requestTimeout="+(requestTimeout/1000)+" s,maxTotal="+maxTotal
						  +",defaultMaxPerRoute="+defaultMaxPerRoute+",retryTime="+retryTime);
       
		}catch(Exception ex){
			logger.error("创建http4.5 pool 出现异常:"+ex.getMessage(), ex);
			throw new HttpException("创建http pool 出现异常:"+ex.getMessage(), ex);
		}
	}
	
	public HttpPool45() {		
	
	}
	
	public  CloseableHttpClient getHttpClient() {	
        return HttpClients.custom()
        .setConnectionManager(poolHttp)
        .setDefaultRequestConfig(defaultRequestConfig).setRetryHandler(new DefaultHttpRequestRetryHandler(retryTime, false)).build();
	}
	
    
	
}
