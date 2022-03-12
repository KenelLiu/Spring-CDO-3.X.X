package com.cdoframework.cdolib.base;

import java.io.Serializable;


/*编码规则--------------------------------------------------------------
 1. 所有成员变量的命名必须要反映变量类型和用途
 2. 类名首字母一律大写
 3. 所有命名要有含义并且名符其实，并且命名主体部分尽量用一个或数个完整的
 英文单词或常用的缩写，尽量少用缩写，所有命名长度不超过30个字符，尽量
 不超过20个字符长度
 4. 每个函数或对象的声明必须放在所属区域部分，如果找不到合适的区域，可以
 创建新的区域
 5. 尽量用通过JavaDoc注释说明每个变量、属性和函数的功能，用途、入口和出口
 参数、返回值 等，以帮助阅读者理解
 6. 函数体中要加上恰当的注释，尤其是比较关键或逻辑较复杂的地方
 7. 书写格式要美观，尽量用TAB，少用或不用空格，并且代码尽量对齐
 8. 较复杂的代码段要分段书写，每段前要加上该段的简单功能描述
 9. 所有的{，}单独占一行，必要时可以在其后加上注释
 10.每个if,then,while,...,等等的内容必须在{}内，即使只有一行
 ---------------------------------------------------------------------*/

/**********************************************************************
在此添加该类的功能描述
**********************************************************************/
final public class Return implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8554204960600320065L;

	/*静态对象--------------------------------------------------------------
	此处声明该类的所有静态(static)对象，并根据要求创建和初始化对象,举例如下：
	static public MyApp myApp=new MyApp();
	---------------------------------------------------------------------*/
	final static public Return OK	=new Return(0,"OK","OK");
	
	/*内部对象--------------------------------------------------------------
	本类中的所有成员变量对象在此处声明为protected(推荐)或private类型，对象
	的变量命名要以“m_”开始，下划线后的第一字母要小写，并且变量命名中要隐
	含变量的类型，不要在此处初始化对象，格式举例如下：
	protected string		m_strName;
	protected DataEngine	m_dataEngine;
	---------------------------------------------------------------------*/

	/*属性------------------------------------------------------------------
	本类中需要对外界开放的在本类中创建的成员变量对象在此处定义为属性
	属性一般可以通过Get和Set方法设定，必要时也可以加以简化实现，举例如下
	//简化方式，只可用于可读写的传值对象
	public string			strName;
	//正常方式
	protected string		m_strName;
	public string			getName(){return m_strName;}
	public void				setName(string strName){m_strName=strName;}
	---------------------------------------------------------------------*/
	private int		nCode;
	private String	strText;
	private String	strInfo;
	private Throwable throwable;
	
	public int getCode()
	{
		return nCode;
	}

	public void setCode(int code)
	{
		nCode=code;
	}

	public String getInfo()
	{
		return strInfo;
	}

	public void setInfo(String strInfo)
	{
		this.strInfo=strInfo;
	}

	public String getText()
	{
		return strText;
	}

	public void setText(String strText)
	{
		this.strText=strText;
	}
	public void setThrowable(Throwable throwable)
	{
		this.throwable = throwable;
	}
	public Throwable getThrowable()
	{
		return this.throwable;
	}
	
	/*引用对象--------------------------------------------------------------
	在本类中引用的由外界创建的成员对象变量在此以函数方式传入到本类中，定义
	格式如下：
	protected DataEngine	m_strDataengine;
	public void	setDataEngine(DataEngine dataEngine)
	{
		m_dataEngine	=dataEngine;
	}
	---------------------------------------------------------------------*/

	/*构造函数--------------------------------------------------------------
	初始化子对象，并设置子对象之间的关系
	引用对象初始化为null
	值对象一般初始化为默认值，数值型为0，字符串为""
	创建子类对象
	---------------------------------------------------------------------*/
	public Return()
	{
		//在此处初始化所有非静态的内部protected和private对象，所有引用对象的
		//内部变量初始化为null，比如：
		//m_strName		="";
		//m_dataEngine	=null;
		//m_slSet		=new SortedList();
	}

	public Return(int nCode,String strText)
	{
		//在此处初始化所有非静态的内部protected和private对象，所有引用对象的
		//内部变量初始化为null，比如：
		//m_strName		="";
		//m_dataEngine	=null;
		//m_slSet		=new SortedList();
		this.nCode		=nCode;
		this.strText	=strText;
		this.strInfo	="";
	}

	public Return(int nCode,String strText,String strInfo)
	{
		//在此处初始化所有非静态的内部protected和private对象，所有引用对象的
		//内部变量初始化为null，比如：
		//m_strName		="";
		//m_dataEngine	=null;
		//m_slSet		=new SortedList();
		this.nCode		=nCode;
		this.strText	=strText;
		this.strInfo	=strInfo;
	}

	/*内部函数--------------------------------------------------------------
	所有只在本类内部和本类的派生类中才能调用的函数在此声明为protected类型
	格式如下：
	protected FDReturn	handleMO(MO mo)
	{
		//...
		return FDReturn.ValueOfOK();
	}
	---------------------------------------------------------------------*/

	/*方法-----------------------------------------------------------------
	所有要公开给外界的成员函数在此声明为public类型，格式如下：
	public FDReturn	run()
	{
		//...
		return FDReturn.ValueOfOK();
	}
	---------------------------------------------------------------------*/
	public static Return valueOf(int nCode,String strText)
	{
		Return ret	=new Return(nCode,strText);
		
		return ret;
	}

	
	public static Return valueOf(int nCode,String strText,String strInfo)
	{
		Return ret	=new Return(nCode,strText,strInfo);
		
		return ret;
	}	
		
	public static Return valueOf(Return ret,String strInfo)
	{
		Return retNew	=new Return(ret.nCode,ret.strText,strInfo);
		
		return retNew;
	}

	public static Return valueOf(int nCode,String strText,Throwable throwable)
	{
		Return ret = new Return(nCode,strText);
		ret.setThrowable(throwable);
		return ret;
	}
	public static Return valueOf(int nCode,String strText,String strInfo,Throwable throwable)
	{
		Return ret = new Return(nCode,strText,strInfo);
		ret.setThrowable(throwable);
		return ret;
	}

	public static Return valueOfOK(String strInfo)
	{
		Return retNew	=new Return(0,"OK",strInfo);
		
		return retNew;
	}
	
	public String toString()
	{
		return nCode+":"+strInfo+":"+strText;
	}

	/*事件处理函数-----------------------------------------------------------
	所有重载的基类的事件类成员函数在此实现，格式如下：
	protected virtual void	onConnected()
	{
		...
	}
	---------------------------------------------------------------------*/

	/*事件定义函数-----------------------------------------------------------
	所有在本类中声明的允许派生类重载的带有事件性质的成员函数在此实现，格式如下：
	protected virtual void	onConnected()
	{
	}
	protected abstract void onDisconnect();
	---------------------------------------------------------------------*/
}
