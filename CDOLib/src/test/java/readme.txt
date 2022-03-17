Log4j2.xml 普通加载			
			//方法1 使用 public ConfigurationSource(InputStream stream) throws IOException 构造函数
			//source = new ConfigurationSource(new FileInputStream(pathFile));
			//方法2 使用 public ConfigurationSource(InputStream stream, File file)构造函数
			//File config=new File("D:\\log4j2.xml");
			//source = new ConfigurationSource(new FileInputStream(config),config);
			//方法3 使用 public ConfigurationSource(InputStream stream, URL url) 构造函数
			//String path="D:\\log4j2.xml";
			//source = new ConfigurationSource(new FileInputStream(path),new File(path).toURL());
			//source.setFile(new File("D:\log4j2.xml"));
			//source.setInputStream(new FileInputStream("D:\log4j2.xml"));
			//Configurator.initialize(null, source); 
			
web项目加载 
1.使用log4j-web.jar
2.public class WebContextListener implements ServletContextListener {
	static StatusLogger statusLog=StatusLogger.getLogger();
	private ServletContext servletContext;
	public void contextInitialized(ServletContextEvent event){		
		try {				
			System.setProperty("prjLog4j","activitiAdmin");
			this.servletContext=event.getServletContext();
			String log4jXml="file:///"+AdminConfig.initLog4j2Xml(IConfig.Activiti_CONFIG_FILE,"activitiLog4j-log4j2.xml");
			statusLog.info("load log4j2xml from:"+log4jXml);
			Configurator.initialize("activitiAdmin", this.servletContext.getClassLoader(), log4jXml, this.servletContext);	
		} catch (Exception e) {
			statusLog.error(e.getMessage(),e);
		}	
	}
	
	
}