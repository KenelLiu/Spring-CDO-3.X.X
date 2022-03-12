package com.cdo.field.array;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.cdo.field.FieldType;
import com.cdoframework.cdolib.util.Utility;

/**
 * 定义byte数组字段
 * @author KenelLiu
 *
 */
public class ByteArrayField extends ArrayFieldImpl{
	private static final long serialVersionUID = 1390757657933478538L;
	//属性对象,所有在本类中创建，并允许外部访问的对象在此声明并提供get/set方法-----------------------------------	
	private final int dataIndex=1;//数据保存的起始位置
	private final int databuffer=1;//数据占用字节
	
	public void setValue(byte[] bysValue)
	{
		if(bysValue==null)
		{
			bysValue=new byte[0];
		}
		allocate(bysValue);
	}
	public byte[] getValue()
	{
		buffer.position(1);
		int len=getLength();
		byte[] bsValue=new byte[len];
		buffer.position(dataIndex);
		buffer.limit(buffer.capacity());		
		(buffer.slice()).get(bsValue);		
		buffer.clear();
		return bsValue;
	}

	public byte getValueAt(int nIndex)
	{
		checkArrayIndex(nIndex);
		int pos=dataIndex+databuffer*nIndex;
		buffer.position(pos);
		byte b=buffer.get();
		buffer.clear();
		return b;
	}

	public void setValueAt(int nIndex,byte byValue)
	{
		checkArrayIndex(nIndex);
		int pos=dataIndex+databuffer*nIndex;
		buffer.position(pos);
		buffer.put(byValue);
		buffer.clear();
	}
	
	public int getLength()
	{		
		return (buffer.capacity()-dataIndex)/databuffer;
	}
	
	public Object getObjectValue()
	{
		return getValue();
	}

	public Object getObjectValueAt(int nIndex)
	{
		return new Byte(getValueAt(nIndex));
	}
	
	@Override
	public Buffer getBuffer() {	
		return buffer;
	}
	
	private void allocateBuffer(byte[] bsValue){
		int len=dataIndex+bsValue.length*databuffer;
		buffer=ByteBuffer.allocate(len);
		buffer.put((byte)FieldType.BYTE_ARRAY_TYPE);		
	}
	
	private void allocate(byte[] bsValue){
		if(buffer==null){
			allocateBuffer(bsValue);
		}else{
			buffer.position(1);
			int len=getLength();
			if(len<bsValue.length){
				//最新数组大于原来数组, 重新分配长度
				allocateBuffer(bsValue);
			}else if(len>bsValue.length){
				//最新数组小于原来数组  截取原长度的一部分 作为本次数据存放,不需要重新分配内存
				len=dataIndex+bsValue.length*databuffer;
				buffer.position(0);
				buffer.limit(len);
				buffer=buffer.slice();
			}
			//若相等，数据进行覆盖即可;
		}
		//设置起始位置  
		buffer.position(dataIndex);
		buffer.put(bsValue);
		buffer.flip();
	}	
	
	//公共方法,所有可提供外部使用的函数在此定义为public方法------------------------------------------------------
	@Override
	public void toXML(StringBuilder strbXML)
	{			
		strbXML.append("<BYAF N=\"").append(this.getName()).append("\"");;
		strbXML.append(" V=\"");
		//拆分成2段，循环一半，对数据量多的平均速度提高15-20%
		byte[] bysValue=getValue();
		int length=bysValue.length;
		int mid=length/2;								
		StringBuilder tmpBuild=new StringBuilder(mid);
		int j=mid+1;
		int tmpLength=length-1;//用于判断是否需要添加逗号
		for(int i=0;i<=mid ;i++){
			strbXML.append(bysValue[i]);
			if(i<tmpLength)
				strbXML.append(",");					
			if(j<length){
				tmpBuild.append(bysValue[j]);
				j++;
				if(j<length)
					tmpBuild.append(",");	
			}
		}
		strbXML.append(tmpBuild);			
		strbXML.append("\"/>");		
	}
	
	@Override
	public void toXMLWithIndent(int nIndentSize,StringBuilder strbXML)
	{
		String strIndent=Utility.makeSameCharString('\t',nIndentSize);
		
		strbXML.append(strIndent).append("<BYAF N=\"").append(this.getName()).append("\"");;
		strbXML.append(" V=\"");
		//拆分成2段，循环一半，对数据量多的平均速度提高15-20%
		byte[] bysValue=getValue();
		int length=bysValue.length;
		int mid=length/2;								
		StringBuilder tmpBuild=new StringBuilder(mid);
		int j=mid+1;
		int tmpLength=length-1;//用于判断是否需要添加逗号
		for(int i=0;i<=mid ;i++){
			strbXML.append(bysValue[i]);
			if(i<tmpLength)
				strbXML.append(",");					
			if(j<length){
				tmpBuild.append(bysValue[j]);
				j++;
				if(j<length)
					tmpBuild.append(",");	
			}
		}
		strbXML.append(tmpBuild);			
		strbXML.append("\"/>\r\n");
	}
	
	@Override
	public String toJSON()
	{
		StringBuffer str_JSON=new StringBuffer();
		str_JSON.append("\"").append(this.getName()).append("\"").append(":").append("[");
		byte[] bysValue=getValue();
		int _length=bysValue.length;
		for(int i=0;i<_length;i=i+1)
		{
			String _sign=(i==_length-1)?"":",";
			str_JSON.append("").append(bysValue[i]).append(_sign);
		}
		str_JSON.append("],");
		return str_JSON.toString();
	}	
	

	
	public String toString()
	{
		StringBuffer str_JSON=new StringBuffer();
		byte[] bysValue=getValue();		
		str_JSON.append("\"").append(this.getName()).append("\"").append(":").append("[");
		str_JSON.append("").append(bysValue);
		str_JSON.append("],");
		return str_JSON.toString();
	}
	
	//构造函数,所有构造函数在此定义------------------------------------------------------------------------------
	public ByteArrayField(String strName){
		//请在此加入初始化代码,内部对象和属性对象负责创建或赋初值,引用对象初始化为null，初始化完成后在设置各对象之间的关系
		super(strName);
		setFieldType(type.BYTE_ARRAY);
		allocate(new byte[0]);
	}

	public ByteArrayField(String strName,byte[] bysValue){
		super(strName);		
		setFieldType(type.BYTE_ARRAY);		
		if(bysValue==null)
		{
			bysValue=new byte[0];
		}
		allocate(bysValue); 
	}
	
	public ByteArrayField(String strName,ByteBuffer buffer){
		super(strName);				
		setFieldType(type.BYTE_ARRAY);
		this.buffer=buffer;
	}
	
}
