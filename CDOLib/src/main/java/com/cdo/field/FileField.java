package com.cdo.field;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.cdo.field.FieldType.type;
import com.cdo.google.GoogleCDO;
import com.cdoframework.cdolib.util.Function;
import com.cdoframework.cdolib.util.Utility;
import com.google.protobuf.ByteString;

/**
 * 
 * @author KenelLiu
 *  仅保存一下文件名，在跨jvm RPC调用中，cdo 对象会得到文件类型，文件为空
 *  通过后续操作将的二进制传输文件，输出到调用的服务器上，赋值后，即可得到文件。
 *  对使用用户，过程是透明的，只需在ＣＤＯ使用setFile,getFile就可获取文件。
 *  １　采用ＨＴＴＰ　ＩＮＶＯＫＥ　则使用ＨＴＴＰ传输文件
 *  ２　采用ＲＰＣ　　ＩＮＶＯＫＥ　则使用自定义ＣＤＯ协议传输　　本质是基于ＴＣＰ协议传输。
　 *  　　　ＣＤＯ协议传输基于ｎｅｔｔｙ长连接　已处理了　半包　拆包　粘包问题
 */
public class FileField extends FieldImpl
{

	//内部类,所有内部类在此声明----------------------------------------------------------------------------------

	//静态对象,所有static在此声明并初始化------------------------------------------------------------------------

	//内部对象,所有在本类中创建并使用的对象在此声明--------------------------------------------------------------

	private static final long serialVersionUID = 6572572742412689563L;
	//属性对象,所有在本类中创建，并允许外部访问的对象在此声明并提供get/set方法-----------------------------------
	private final int dataIndex=1;//文件名称保存的起始位置
	private int databuffer=0;//文件名称数据占用字节   随字符串UTF8长度 而改变
	private int preDataBuffer=0;//文件名称数据未改变之前 所占用字节	
	
	private File fileValue;
		
	public void setValue(File fileValue)
	{
		this.fileValue=fileValue;
		
		if(fileValue!=null)
			allocate(fileValue.getPath());
		else
			allocate("");
	}
	
	public File getValue()
	{
		return this.fileValue;
	}
	
	
	public Object getObjectValue()
	{
		return fileValue;
	}

	private  void allocateBuffer(){
		int len=dataIndex+databuffer;
		buffer=ByteBuffer.allocate(len);
		buffer.put((byte)FieldType.FILE_TYPE);
			
	}
	
	private void allocate(String strValue){
		byte[] strByte=strValue.getBytes(Charset.forName("UTF-8"));
		databuffer=strByte.length;
		if(buffer==null){
			allocateBuffer();
		}else{			
			if(preDataBuffer<databuffer){
				//最新字符串 长度大于之前的长度
				allocateBuffer();
			}else if(preDataBuffer>databuffer){
				//最新字符串 长度小于原来字符串长度  截取原长度的一部分 作为本次数据存放,不需要重新分配内存
				int len=dataIndex+databuffer;
				buffer.position(0);
				buffer.limit(len);
				buffer=buffer.slice();
			}
			//若相等，数据进行覆盖即可;
		}		
		buffer.position(dataIndex);
		buffer.put(strByte);
		buffer.flip();
		//设置字符串长度，当字符串改变 可以进行比较
		preDataBuffer=databuffer;
	}
	//公共方法,所有可提供外部使用的函数在此定义为public方法------------------------------------------------------
	@Override
	public void toXML(StringBuilder strbXML)
	{
		String path=this.fileValue==null?" file is null":this.fileValue.getPath();
		strbXML.append("<FILE N=\"").append(this.getName()).append("\"");
		strbXML.append(" V=\"").append(Function.FormatTextForXML(path)).append("\"/>");
	}
	@Override
	public void toXMLWithIndent(int nIndentSize,StringBuilder strbXML)
	{
		String path=this.fileValue==null?" file is null":this.fileValue.getPath();
		
		String strIndent=Utility.makeSameCharString('\t',nIndentSize);
		
		strbXML.append(strIndent).append("<File N=\"").append(this.getName()).append("\"");
		strbXML.append(" V=\"").append(Function.FormatTextForXML(path)).append("\"/>\r\n");
	}
	@Override
	public String toJSON()
	{
		StringBuffer str_JSON=new StringBuffer();
		String path=this.fileValue==null?" file is null":this.fileValue.getPath();
		str_JSON.append("\"").append(this.getName()).append("\"").append(":\"").append(Function.FormatTextForJson(path)).append("\",");
		return str_JSON.toString();
	}

	//构造函数,所有构造函数在此定义------------------------------------------------------------------------------

	public FileField(String strFieldName){

		super(strFieldName);		
		setFieldType(type.FILE);		
		setValue(null);
				
	}

	public FileField(String strFieldName,File fileValue){
		super(strFieldName);		
		setFieldType(type.FILE);		
		setValue(fileValue);
	}
	
	public FileField(String strFieldName,ByteBuffer buffer){
		super(strFieldName);		
		setFieldType(type.FILE);		
		this.buffer=buffer;
		this.fileValue=new File(getFileNameValue());
	}
	
	private String getFileNameValue(){
		buffer.position(dataIndex);
		ByteBuffer slice = buffer.slice();
		byte[] dst=new byte[slice.capacity()];
		slice.get(dst);
		return new String(dst,Charset.forName("UTF-8"));
	}
}
