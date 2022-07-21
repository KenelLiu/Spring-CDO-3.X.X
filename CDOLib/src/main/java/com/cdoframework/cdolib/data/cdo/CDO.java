package com.cdoframework.cdolib.data.cdo;


import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nanoxml.XMLElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cdo.avro.AvroCDO;
import com.cdo.exception.StateException;
import com.cdo.field.BooleanField;
import com.cdo.field.ByteField;
import com.cdo.field.DateField;
import com.cdo.field.DateTimeField;
import com.cdo.field.DoubleField;
import com.cdo.field.Field;
import com.cdo.field.FieldType;
import com.cdo.field.FileField;
import com.cdo.field.FloatField;
import com.cdo.field.IntegerField;
import com.cdo.field.LongField;
import com.cdo.field.NullField;
import com.cdo.field.ShortField;
import com.cdo.field.StringField;
import com.cdo.field.TimeField;
import com.cdo.field.array.ArrayField;
import com.cdo.field.array.BooleanArrayField;
import com.cdo.field.array.ByteArrayField;
import com.cdo.field.array.DateArrayField;
import com.cdo.field.array.DateTimeArrayField;
import com.cdo.field.array.DoubleArrayField;
import com.cdo.field.array.FloatArrayField;
import com.cdo.field.array.IntegerArrayField;
import com.cdo.field.array.LongArrayField;
import com.cdo.field.array.ShortArrayField;
import com.cdo.field.array.StringArrayField;
import com.cdo.field.array.TimeArrayField;
import com.cdo.google.GoogleCDO;
import com.cdoframework.cdolib.util.Utility;

/**
 *CDO 维护一个通用数据类型
 *key dot符号[即 .]，表示cdo的层级关系
 *除  NullField能保存null值外,
 *其他常规字段[StringField,DateField...等],内容不能为NULL值,若设置null值,则会初始化成默认值
 */
public class CDO{

	private static final Log logger=LogFactory.getLog(CDO.class);

	//========CDO里是否设置了文件，若设置了文件 则在网络传输需要特别处理 ,仅对最外层cdo有文件类型的进行处理=====//
	int fileCount;
	//内部类,所有内部类在此声明----------------------------------------------------------------------------------
	final class FieldId
	{
		static final int SIMPLE=0;
		static final int MULTI_LEVEL=1;
		static final int ARRAY_ELEMENT=2;
		
		int nType;//0-简单类型，1-多级类型，2-数组元素
		
		String strMainFieldId;
		String strFieldId;
		String strIndexFieldId;
	};

	//======内部对象,存储字段内容==========//
	private HashMap<String,Field> hmItem;
	
	//======设置需要传输文件个数 =======//
	 void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}
	 
	//=================内部方法==================//
	protected void putItem(String strKey,Field field){
		hmItem.put(strKey,field);
	}	
	//======0-简单类型，1-多级，2-数组元素========//
	//======如果FieldId错误，则返回null=========//
	 FieldId parseFieldId(String strFieldId)
	{
		if(hmItem==null){
			throw new RuntimeException(" CDO is already release ,can't set field ["+strFieldId+"]");
		}
			
		char[] chsFieldId=strFieldId.toCharArray();
		
		int nLength=chsFieldId.length;;
		
		FieldId fieldId=null;
		if(chsFieldId[nLength-1]==']')
		{//数组元素
			int nMatchIndex=Utility.findMatchedChar(nLength-1,chsFieldId);
			if(nMatchIndex<1)
			{
				return null;
			}
			
			fieldId=new FieldId();
			fieldId.nType=2;
			fieldId.strMainFieldId=strFieldId.substring(0,nMatchIndex);
			if(fieldId.strMainFieldId.length()==0)
			{
				return null;
			}
			fieldId.strIndexFieldId=strFieldId.substring(nMatchIndex+1,nLength-1);
			if(fieldId.strIndexFieldId.length()==0)
			{
				return null;
			}
			
			return fieldId;
		}
		
		for(int i=nLength-1;i>=0;i--)
		{
			char ch=chsFieldId[i];
			if(ch=='.')
			{//多级
				fieldId=new FieldId();
				fieldId.nType=1;
				fieldId.strMainFieldId=strFieldId.substring(0,i);
				if(fieldId.strMainFieldId.length()==0)
				{
					return null;
				}
				fieldId.strFieldId=strFieldId.substring(i+1);
				if(fieldId.strFieldId.length()==0)
				{
					return null;
				}

				return fieldId;
			}
		}
		
		//简单FieldId
		fieldId=new FieldId();
		fieldId.nType=0;
		fieldId.strFieldId=strFieldId;
		
		return fieldId;
	}

	//=============序列化方法    cdo2xml  cdo2Avro cdo2proto,cdo2Json=============//
	/**
	 * 将CDO转换成avro对象
	 * @return
	 */
	public  AvroCDO toAvro(){	
		if(this.hmItem==null){
			return null;
		}
		LinkedHashMap<CharSequence,ByteBuffer> fieldMap=new LinkedHashMap<CharSequence, ByteBuffer>();
		String prefixField="";	
		toAvro(prefixField,fieldMap);		
		AvroCDO arvo=new AvroCDO();			
		arvo.setFields(fieldMap);		
		return arvo;
	}
	
	/**
	 * 将保存在CDO字段调用该方法,转换成key,fieldMap
	 * @param prefixField
	 * @param fieldMap
	 */
	 void toAvro(String prefixField,Map<CharSequence,ByteBuffer> fieldMap){
		if(this.hmItem==null){
				throwStateException(prefixField);		
		}			 
		Entry<String, Field> entry=null;
		for(Iterator<Map.Entry<String, Field>> it=this.entrySet().iterator();it.hasNext();){
			entry=it.next();
			entry.getValue().toAvro(prefixField, fieldMap);
		}
	}
	/**
	 * 将CDO转换成GoogleCDO.CDOProto.Builder对象
	 * @return
	 */
	public GoogleCDO.CDOProto.Builder toProtoBuilder(){
		if(this.hmItem==null){
			return null;
		}
		GoogleCDO.CDOProto.Builder cdoProto=GoogleCDO.CDOProto.newBuilder();
		String prefixField="";	
		toProto(prefixField,cdoProto);		
		return cdoProto;
	}
	
	/**
	 *将保存在CDO字段调用该方法,转换成GoogleCDO.CDOProto.Builder
	 * @param prefixField
	 * @param fieldMap
	 */
	 void toProto(String prefixField,GoogleCDO.CDOProto.Builder cdoProto){
		if(this.hmItem==null){
			throwStateException(prefixField);
		}		 
		Entry<String, Field> entry=null;
		for(Iterator<Map.Entry<String, Field>> it=this.entrySet().iterator();it.hasNext();){
			entry=it.next();
			entry.getValue().toProto(prefixField, cdoProto);
		}
	}	
	 
	private void throwStateException(String prefixField){
		String message="The CDO";
		if(prefixField.endsWith(".")){
			prefixField=prefixField.substring(0,prefixField.length()-1);				
		}			
		int index=prefixField.lastIndexOf(".");
		if(index>0){
			String name=prefixField.substring(index+1, prefixField.length());
			if(name.contains("[")){
				 prefixField=prefixField.substring(0,index+1)+name.substring(0,name.lastIndexOf("["));
				 message="The CDO Array";
			}
		}else{
			if(prefixField.contains("[")){
				 prefixField=prefixField.substring(0,prefixField.indexOf("["));
				 message="The CDO Array ";
			}
		}
		throw new StateException(message+"["+prefixField+"] has been released by another program.");
	}
	
	/**
	 * 将CDO对象转换成 xml  
	 * @return
	 */
	public String toXML()
	{
		if(this.hmItem==null){
			return null;
		}
		StringBuilder strbXML=new StringBuilder(500);
		strbXML.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");				
		strbXML.append("<CDO>");
		appendFieldXML(strbXML);
		strbXML.append("</CDO>");		
		return strbXML.toString();
	}

	 String toXML(StringBuilder strbXML){		 
		 if(this.hmItem==null){
				return strbXML.toString();
		 }		
		strbXML.append("<CDO>");
		appendFieldXML(strbXML);
		strbXML.append("</CDO>");
		
		return strbXML.toString();
	}
	
	 private void appendFieldXML(StringBuilder strbXML){
		 if(this.hmItem==null){
				return;
		 }		 
		Entry<String, Field> entry=null;
		for(Iterator<Map.Entry<String, Field>> it=this.entrySet().iterator();it.hasNext();){
			entry=it.next();		
			entry.getValue().toXML(strbXML);
		}
	}
	

	/**
	 * 将CDO转换成有换行符的XML 
	 * @return
	 */
	public String toXMLWithIndent(){
		if(this.hmItem==null){
			return null;
		}
		StringBuilder strbXML=new StringBuilder(500);
		strbXML.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		strbXML.append("<CDO>\r\n");
		Entry<String, Field> entry=null;
		for(Iterator<Map.Entry<String, Field>> it=this.entrySet().iterator();it.hasNext();){
			entry=it.next();
			entry.getValue().toXMLWithIndent(1,strbXML);
		}			
		strbXML.append("</CDO>\r\n");	
		
		return strbXML.toString();
	}
	
	void toXMLWithIndent(String strIndent,StringBuilder strbXML){
		 if(this.hmItem==null){
				return ;
		 }			
		strbXML.append(strIndent).append("<CDO>\r\n");		
		Entry<String, Field> entry=null;
		for(Iterator<Map.Entry<String, Field>> it=this.entrySet().iterator();it.hasNext();){
			entry=it.next();
			entry.getValue().toXMLWithIndent(1+strIndent.length(),strbXML);
		}			
		strbXML.append(strIndent).append("</CDO>\r\n");
	}

	/**
	 * 把CDO对象转换成JSON格式的字符串
	 * 
	 * @param cdo
	 * @return JSON格式的字符串
	 * 
	 */
	public String toJSON()
	{
		return buffer2JSON(B2S_JSON);
	}
	
	/**
	 * 把CDO对象转换成JSON格式的字符串,
	 * 其中value使用了html编码
	 *  
	 * @return
	 */
	public String toHtmlJSON()
	{
		return buffer2JSON(B2S_HtmlJSON);
	}
	/**把CDO对象转换成JSON格式的字符串,
	 * 其中value 特殊字符,同时进行 json转义及html编码
	 * @return
	 */
	public String toMixHtmlJSON(){		
		return buffer2JSON(B2S_MixHtmlJSON);
	}

	
	
//-----------------------------xml2CDO 反序列化方法    avro2CDO 参看AvroCDODeserialize-----------------------------------------------//
	
	public static CDO fromXML(String strXML)
	{
		CDO cdo=new CDO();
		xmlToCDO(strXML,cdo);			
		return cdo;
	}

    public void copyFrom(String strCDOXML)
    {
		xmlToCDO(strCDOXML,this);
    }

    
	public static void xmlToCDO(String strXML,CDO cdoOutPut)
	{
		XMLElement xmlNode=new XMLElement();
		xmlNode.parseString(strXML);
		
		ParseXmlCDO.xml2CDO(cdoOutPut, xmlNode, true);
	}	
	
	
    public CDO clone()
    {
    	CDO cdo=new CDO();
    	copy(this,cdo);
    	return cdo;
    }	
	
    public void copyFrom(CDO cdoSource)
    {
    	copy(cdoSource, this);
    }
    
    private void copy(CDO src,CDO dst){
    	for(Iterator<Map.Entry<String,Field>> iterator=src.iterator();iterator.hasNext();){
    		Entry<String,Field> entry=iterator.next();
    		DataBufferUtil.setField(dst, entry.getKey(), entry.getValue());    		
    	}
    }
    
   
    /**
     * 根据key,获取Field
     * @param fieldId
     * @param cdoRoot
     * @return
     */
    private Field getObject(FieldId fieldId,CDO cdoRoot)
	{
    	if(fieldId.nType==FieldId.SIMPLE)
    	{//简单类型    		
    		return this.hmItem.get(fieldId.strFieldId);
    	}

    	if(fieldId.nType==FieldId.MULTI_LEVEL)
    	{//多级FieldId
    		FieldId fieldIdMain=this.parseFieldId(fieldId.strMainFieldId);
    		if(fieldIdMain==null)
    		{
    			throw new RuntimeException("Invalid FieldId "+fieldId.strMainFieldId);
    		}
    		if(fieldIdMain.nType==FieldId.ARRAY_ELEMENT)
    		{
    			Field objExt=this.getObject(this.parseFieldId(fieldIdMain.strMainFieldId),cdoRoot);
    			if(objExt==null)
    			{
    				return null;
    			}
    			int nIndex=this.getIndexValue(fieldIdMain.strIndexFieldId,cdoRoot);    			
    			return (((CDOArrayField)objExt).getValueAt(nIndex)).getObject(fieldId.strFieldId);
    		}
    		Field cdoMainField=getObject(fieldIdMain,cdoRoot);
    		if(cdoMainField==null)
    		{
    			return null;
    		}
    		if(cdoMainField.getFieldType().getType()>=FieldType.BOOLEAN_ARRAY_TYPE && fieldId.strFieldId.equalsIgnoreCase("length")==true)
    		{//是数组类型
    			return new IntegerField(((ArrayField)cdoMainField).getLength());
    		}
    		return (((CDOField)cdoMainField).getValue()).hmItem.get(fieldId.strFieldId);
    	}

    	//数组元素
		int nIndex=this.getIndexValue(fieldId.strIndexFieldId,cdoRoot);
		FieldId fieldIdMain=this.parseFieldId(fieldId.strMainFieldId);
		Field objExt=this.getObject(fieldIdMain,cdoRoot);	

		return getValueAtExt(objExt,nIndex);
	}

    //根据带路径的FieldId获取对应的Value
    private int getIndexValue(String strIndex,CDO cdoRoot)
    {
		int nIndex=0;
		strIndex=strIndex.trim();
		if(strIndex.charAt(0)>='0' &&strIndex.charAt(0)<='9')
		{//下标是数字，直接使用
			nIndex=Integer.parseInt(strIndex);
		}
		else
		{//下标为字段Id，获取字段值当作索引
			Object objIndex=cdoRoot.getObjectValue(strIndex);
			if(objIndex instanceof Byte)
			{
				nIndex=((Byte)objIndex).byteValue();
			}
			else if(objIndex instanceof Short)
			{
				nIndex=((Short)objIndex).shortValue();
			}
			else if(objIndex instanceof Integer)
			{
				nIndex=((Integer)objIndex).intValue();
			}
			else if(objIndex instanceof Long)
			{
				nIndex=(int)((Long)objIndex).longValue();
			}
			else
			{
				throw new RuntimeException("Invalid array index");
			}
		}
		
		return nIndex;
    } 
    /**
     *  获取数组 指定index的值
     * @param field
     * @param nIndex
     * @return
     */
    private Field getValueAtExt(Field field,int nIndex)
	{
		switch(field.getFieldType().getType())
		{
			case FieldType.BOOLEAN_ARRAY_TYPE:
			{
				return new BooleanField(((BooleanArrayField)field).getValueAt(nIndex));
			}
			case FieldType.BYTE_ARRAY_TYPE:
			{
				return new ByteField(((ByteArrayField)field).getValueAt(nIndex));
			}
			case FieldType.SHORT_ARRAY_TYPE:
			{
				return new ShortField(((ShortArrayField)field).getValueAt(nIndex));
			}
			case FieldType.INTEGER_ARRAY_TYPE:
			{
				return new IntegerField(((IntegerArrayField)field).getValueAt(nIndex));
			}
			case FieldType.LONG_ARRAY_TYPE:
			{
				return  new LongField(((LongArrayField)field).getValueAt(nIndex));
			}
			case FieldType.FLOAT_ARRAY_TYPE:
			{
				return new FloatField(((FloatArrayField)field).getValueAt(nIndex));
			}
			case FieldType.DOUBLE_ARRAY_TYPE:
			{
				return new DoubleField(((DoubleArrayField)field).getValueAt(nIndex));
			}
			case FieldType.STRING_ARRAY_TYPE:
			{
				return new StringField("",((StringArrayField)field).getValueAt(nIndex));
			}
			case FieldType.DATE_ARRAY_TYPE:
			{
				return new DateField("",((DateArrayField)field).getValueAt(nIndex));
			}
			case FieldType.TIME_ARRAY_TYPE:
			{
				return new TimeField("",((TimeArrayField)field).getValueAt(nIndex));
			}
			case FieldType.DATETIME_ARRAY_TYPE:
			{
				return new DateTimeField("",((DateTimeArrayField)field).getValueAt(nIndex));
			}
			case FieldType.CDO_ARRAY_TYPE:
			{
				return new CDOField(((CDOArrayField)field).getValueAt(nIndex));
			}
		}
		throw new RuntimeException(field.getFieldType().getName()+" Type cast failed");	
	}
    
    /**
     * 根据 strFieldId 获取Field
     * @param strFieldId 
     * @return
     */
    public Field getObject(String strFieldId)
    {
		if(hmItem==null){
			throw new RuntimeException(" CDO is already release ,can'n  get field ["+strFieldId+"]");
		}
    	Field objExt=this.hmItem.get(strFieldId);
    	if(objExt!=null)
    	{//直接子节点
    		return objExt;
    	}
    	
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{//无效FieldId
    		logger.warn("Invalid FieldId ["+strFieldId+"],cdo data:"+this.toString());
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	
    	objExt=getObject(fieldId,this);
    	if(objExt==null)
    	{
    		logger.warn("Invalid FieldId ["+strFieldId+"],cdo data:"+this.toString());
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	
    	return objExt;
    }

    /**
     * 根据 strFieldId 获取Field的value值
     * @param strFieldId
     * @return
     */
    public Object getObjectValue(String strFieldId)
    {
    	Field objExt=this.getObject(strFieldId);
    	return objExt.getObjectValue();
	}

    //----------获取指定类型Field 的value------------------------//
    public boolean getBooleanValue(String strFieldId)
    {
    	BooleanField objExt=(BooleanField)this.getObject(strFieldId);
    	return objExt.getValue();
    }

    public byte getByteValue(String strFieldId)
    {
    	Field field=this.getObject(strFieldId);
    	return DataBufferUtil.getByte(field);
    }

    public short getShortValue(String strFieldId)
    {
    	Field field=this.getObject(strFieldId);
    	return DataBufferUtil.getShort(field);
    }

    public int getIntegerValue(String strFieldId)
    {
    	Field field=this.getObject(strFieldId);
    	return DataBufferUtil.getInteger(field);
    }

    public long getLongValue(String strFieldId)
    {
    	Field field=this.getObject(strFieldId);
    	return DataBufferUtil.getLong(field);
    }

    public float getFloatValue(String strFieldId)
    {
    	Field field=this.getObject(strFieldId);
    	return DataBufferUtil.getFloat(field);
    }

    public double getDoubleValue(String strFieldId)
    {
    	Field field=this.getObject(strFieldId);
    	return DataBufferUtil.getDouble(field);
    }

    public String getStringValue(String strFieldId)
    {
    	Field field=this.getObject(strFieldId);
    	return DataBufferUtil.getString(field);
    }

    public File getFileValue(String strFieldId)
    {
    	FileField objExt=(FileField)this.getObject(strFieldId);
    	return objExt.getValue();
    }
    public String getDateValue(String strFieldId)
    {
    	Field field=this.getObject(strFieldId);
    	return DataBufferUtil.getDate(field);
    }
    
    public long getDateLongValue(String strFieldId)
    {
    	Field field=this.getObject(strFieldId);
    	return DataBufferUtil.getDateValue(field);
    }
    
    public String getTimeValue(String strFieldId)
    {
    	Field field=this.getObject(strFieldId);
    	return DataBufferUtil.getTime(field);
    }
    public long getTimeLongValue(String strFieldId)
    {
    	Field field=this.getObject(strFieldId);
    	return DataBufferUtil.getTimeValue(field);
    }
    
    public String getDateTimeValue(String strFieldId)
    {
    	Field field=this.getObject(strFieldId);
    	return DataBufferUtil.getDateTime(field);
    }
    
    public long getDateTimeLongValue(String strFieldId)
    {
    	Field field=this.getObject(strFieldId);
    	return DataBufferUtil.getDateTimeValue(field);
    }
    
    public CDO getCDOValue(String strFieldId)
    {
    	CDOField objExt=(CDOField)this.getObject(strFieldId);
    	return objExt.getValue();
    }

    public boolean[] getBooleanArrayValue(String strFieldId)
    {
    	BooleanArrayField objExt=(BooleanArrayField)this.getObject(strFieldId);
    	return objExt.getValue();
    }

    public byte[] getByteArrayValue(String strFieldId)
    {
    	ByteArrayField objExt=(ByteArrayField)this.getObject(strFieldId);
    	return objExt.getValue();
    }

    public short[] getShortArrayValue(String strFieldId)
    {
    	ShortArrayField objExt=(ShortArrayField)this.getObject(strFieldId);
    	return objExt.getValue();
    }

    public int[] getIntegerArrayValue(String strFieldId)
    {
    	IntegerArrayField objExt=(IntegerArrayField)this.getObject(strFieldId);
    	return objExt.getValue();
    }

    public long[] getLongArrayValue(String strFieldId)
    {
    	LongArrayField objExt=(LongArrayField)this.getObject(strFieldId);
    	return objExt.getValue();
    }

    public float[] getFloatArrayValue(String strFieldId)
    {
    	FloatArrayField objExt=(FloatArrayField)this.getObject(strFieldId);
    	return objExt.getValue();
    }

    public double[] getDoubleArrayValue(String strFieldId)
    {
    	DoubleArrayField objExt=(DoubleArrayField)this.getObject(strFieldId);
    	return objExt.getValue();
    }

    public String[] getStringArrayValue(String strFieldId)
    {
    	StringArrayField objExt=(StringArrayField)this.getObject(strFieldId);
    	return objExt.getValue();
    }

    public String[] getDateArrayValue(String strFieldId)
    {
    	DateArrayField objExt=(DateArrayField)this.getObject(strFieldId);
    	return objExt.getValue();
    }
    
    public long[] getDateArrayLongValue(String strFieldId)
    {
    	DateArrayField objExt=(DateArrayField)this.getObject(strFieldId);
    	return objExt.getLongValue();
    }
    
    public String[] getTimeArrayValue(String strFieldId)
    {
    	TimeArrayField objExt=(TimeArrayField)this.getObject(strFieldId);
    	return objExt.getValue();
    }
    
    public long[] getTimeArrayLongValue(String strFieldId)
    {
    	TimeArrayField objExt=(TimeArrayField)this.getObject(strFieldId);
    	return objExt.getLongValue();
    }
    
    public String[] getDateTimeArrayValue(String strFieldId)
    {
    	DateTimeArrayField objExt=(DateTimeArrayField)this.getObject(strFieldId);
    	return objExt.getValue();
    }
    public long[] getDateTimeArrayLongValue(String strFieldId)
    {
    	DateTimeArrayField objExt=(DateTimeArrayField)this.getObject(strFieldId);
    	return objExt.getLongValue();
    }
    public CDO[] getCDOArrayValue(String strFieldId)
    {
    	CDOArrayField objExt=(CDOArrayField)this.getObject(strFieldId);
    	return objExt.getValue().toArray(new CDO[objExt.getValue().size()]);
    	
    }
    
    public List<CDO> getCDOListValue(String strFieldId)
    {
    	CDOArrayField objExt=(CDOArrayField)this.getObject(strFieldId);
    	return objExt.getValue();
    }   

    
    //----------设置 指定类型Field的name和value------------------------//
   
    public CDO setBooleanValue(String strFieldId,boolean bValue)
    {    	
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	Field field=new BooleanField(fieldId.strFieldId,bValue);
   		this.setObjectValue(fieldId,FieldType.BOOLEAN_TYPE,bValue,field,this);
   		return this;
    }
  
    public CDO setByteValue(String strFieldId,byte byValue)
    {
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	Field field=new ByteField(fieldId.strFieldId,byValue);
		this.setObjectValue(fieldId,FieldType.BYTE_TYPE,byValue,field,this);
		return this;
    }
    
    
    public CDO setShortValue(String strFieldId,short shValue)
    {
    	

    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}    	
    	
    	Field field=new ShortField(fieldId.strFieldId,shValue);
		this.setObjectValue(fieldId,FieldType.SHORT_TYPE,shValue,field,this);
		return this;
    }

    public CDO setIntegerValue(String strFieldId,int nValue)
    {
    	

    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	Field field=new IntegerField(fieldId.strFieldId,nValue);
		this.setObjectValue(fieldId,FieldType.INTEGER_TYPE,nValue,field,this);
		return this;
    }

    public CDO setLongValue(String strFieldId,long lValue)
    {
    	
    	
    	FieldId fieldId=this.parseFieldId(strFieldId);

    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	Field field=new LongField(fieldId.strFieldId,lValue);
		this.setObjectValue(fieldId,FieldType.LONG_TYPE,lValue,field,this);
		return this;
    }

    public CDO setFloatValue(String strFieldId,float fValue)
    {    
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	Field field=new FloatField(fieldId.strFieldId,fValue);
		this.setObjectValue(fieldId,FieldType.FLOAT_TYPE,fValue,field,this);
		return this;
    }

    public CDO setDoubleValue(String strFieldId,double dValue)
    {
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	Field field=new DoubleField(fieldId.strFieldId,dValue);
		this.setObjectValue(fieldId,FieldType.DOUBLE_TYPE,dValue,field,this);
		return this;
    }

    public CDO setStringValue(String strFieldId,String strValue)
    {
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	Field field=new StringField(fieldId.strFieldId,strValue);
		this.setObjectValue(fieldId,FieldType.STRING_TYPE,strValue,field,this);
		return this;
    }
    /**
     * 存储一个特殊的字段[NullField]用来保存Null值, 与其他字段【如:StringField,IntegerField】等地位相同
     * 多用于插入或更新数据库字段为为null值时使用
     * @param strFieldId
     */
    public CDO setNullValue(String strFieldId){

    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	Field field=new NullField(fieldId.strFieldId);
		this.setObjectValue(fieldId,FieldType.NULL_TYPE,null,field,this);
		return this;
    }
    /**
     * 该字段是否存储了Null值
     * @param strFieldId
     * @return
     */
    public boolean isNull(String strFieldId){    	
    	Field field=this.getObject(strFieldId);    	
    	if(field.getFieldType().getType()==FieldType.NULL_TYPE)
    			return true;    	
    	return false;
    }    
    /**
     * 
     * @param strFieldId
     * @param file
     */
    public CDO setFileValue(String strFieldId,File file)
    {
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	Field field=new FileField(fieldId.strFieldId,file);
		this.setObjectValue(fieldId,FieldType.FILE_TYPE,file,field,this);
		if(fieldId.nType==FieldId.SIMPLE)
			fileCount++;
		return this;
    }
    
    public CDO setDateValue(String strFieldId,String strValue)
    {    	
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	Field field=new DateField(fieldId.strFieldId,strValue);
		this.setObjectValue(fieldId,FieldType.DATE_TYPE,strValue,field,this);
		return this;
    }
    
    public CDO setDateValue(String strFieldId,long lValue)
    {    	

    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	Field field=new DateField(fieldId.strFieldId,lValue);
		this.setObjectValue(fieldId,FieldType.DATE_TYPE,lValue,field,this);
		return this;
    }
    
    public CDO setTimeValue(String strFieldId,String strValue)
    {
    	

    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	Field field=new TimeField(fieldId.strFieldId,strValue);
		this.setObjectValue(fieldId,FieldType.TIME_TYPE,strValue,field,this);
		return this;
    }

    public CDO setTimeValue(String strFieldId,long lValue)
    {
    	

    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	Field field=new TimeField(fieldId.strFieldId,lValue);
		this.setObjectValue(fieldId,FieldType.TIME_TYPE,lValue,field,this);
		return this;
    }
    
    public CDO setDateTimeValue(String strFieldId,String strValue)
    {
    	

    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	Field field=new DateTimeField(fieldId.strFieldId,strValue);
		this.setObjectValue(fieldId,FieldType.DATETIME_TYPE,strValue,field,this);
		return this;
    }

    public CDO setDateTimeValue(String strFieldId,long lValue)
    {
    	
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	Field field=new DateTimeField(fieldId.strFieldId,lValue);
		this.setObjectValue(fieldId,FieldType.DATETIME_TYPE,lValue,field,this);
		return this;
    }
    

    
    public CDO setCDOValue(String strFieldId,CDO cdoValue)
    {
    	

    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	Field field=new CDOField(fieldId.strFieldId,cdoValue);
		this.setObjectValue(fieldId,FieldType.CDO_TYPE,cdoValue,field,this);
		return this;
    }

    public CDO setBooleanArrayValue(String strFieldId,boolean[] bsValue)
    {    
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	
    	if(fieldId.nType==FieldId.ARRAY_ELEMENT)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	else
    	{
    		Field field=new BooleanArrayField(fieldId.strFieldId,bsValue);
    		this.setObjectValue(fieldId,FieldType.BOOLEAN_ARRAY_TYPE,bsValue,field,this);
    	}
    	return this;
    }

    public CDO setByteArrayValue(String strFieldId,byte[] bysValue)
    {    	
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	
    	if(fieldId.nType==FieldId.ARRAY_ELEMENT)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	else
    	{
    		Field field=new ByteArrayField(fieldId.strFieldId,bysValue);
    		this.setObjectValue(fieldId,FieldType.BYTE_ARRAY_TYPE,bysValue,field,this);
    	}
    	return this;
    }

    public CDO setShortArrayValue(String strFieldId,short[] shsValue)
    {    
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	
    	if(fieldId.nType==FieldId.ARRAY_ELEMENT)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	else
    	{
    		Field field=new ShortArrayField(fieldId.strFieldId,shsValue);
    		this.setObjectValue(fieldId,FieldType.SHORT_ARRAY_TYPE,shsValue,field,this);
    	}
    	return this;
    }

    public CDO setIntegerArrayValue(String strFieldId,int[] nsValue)
    {    
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	
    	if(fieldId.nType==FieldId.ARRAY_ELEMENT)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	else
    	{
    		Field field=new IntegerArrayField(fieldId.strFieldId,nsValue); 
    		this.setObjectValue(fieldId,FieldType.INTEGER_ARRAY_TYPE,nsValue,field,this);
    	}
    	return this;
    }
    
    public CDO setFloatArrayValue(String strFieldId,float[] fsValue)
    {    	
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	
    	if(fieldId.nType==FieldId.ARRAY_ELEMENT)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	else
    	{
    		Field field=new FloatArrayField(fieldId.strFieldId,fsValue); 
    		this.setObjectValue(fieldId,FieldType.FLOAT_ARRAY_TYPE,fsValue,field,this);
    	}
    	return this;
    }
    
    public CDO setDoubleArrayValue(String strFieldId,double[] dblsValue)
    {
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	
    	if(fieldId.nType==FieldId.ARRAY_ELEMENT)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	else
    	{
    		Field field=new DoubleArrayField(fieldId.strFieldId,dblsValue); 
    		this.setObjectValue(fieldId,FieldType.DOUBLE_ARRAY_TYPE,dblsValue,field,this);
    	}
    	return this;
    }
    public CDO setLongArrayValue(String strFieldId,long[] lsValue)
    {
    	

    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	
    	if(fieldId.nType==FieldId.ARRAY_ELEMENT)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	else
    	{
    		Field field=new LongArrayField(fieldId.strFieldId,lsValue); 
    		this.setObjectValue(fieldId,FieldType.LONG_ARRAY_TYPE,lsValue,field,this);
    	}
    	return this;
    }
    public CDO setStringArrayValue(String strFieldId,String[] strsValue)
    {
    	

    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	
    	if(fieldId.nType==FieldId.ARRAY_ELEMENT)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	else
    	{
    		Field field=new StringArrayField(fieldId.strFieldId,strsValue);  
    		this.setObjectValue(fieldId,FieldType.STRING_ARRAY_TYPE,strsValue,field,this);
    	}    	
    	return this;    	
    }

    public CDO setDateArrayValue(String strFieldId,String[] strsValue)
    {    	
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	
    	if(fieldId.nType==FieldId.ARRAY_ELEMENT)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	else
    	{
    		Field field=new DateArrayField(fieldId.strFieldId,strsValue);  
    		this.setObjectValue(fieldId,FieldType.DATE_ARRAY_TYPE,strsValue,field,this);
    	}
    	return this;
    }

    public CDO setDateArrayValue(String strFieldId,long[] lsValue)
    {    	
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	
    	if(fieldId.nType==FieldId.ARRAY_ELEMENT)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	else
    	{
    		Field field=new DateArrayField(fieldId.strFieldId,lsValue);  
    		this.setObjectValue(fieldId,FieldType.DATE_ARRAY_TYPE,lsValue,field,this);
    	}
    	return this;
    }
    
    public CDO setTimeArrayValue(String strFieldId,String[] strsValue)
    {
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	
    	if(fieldId.nType==FieldId.ARRAY_ELEMENT)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	else
    	{
    		Field field=new TimeArrayField(fieldId.strFieldId,strsValue);  
    		this.setObjectValue(fieldId,FieldType.TIME_ARRAY_TYPE,strsValue,field,this);
    	}
    	return this;
    }
    public CDO setTimeArrayValue(String strFieldId,long[] lsValue)
    {
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	
    	if(fieldId.nType==FieldId.ARRAY_ELEMENT)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	else
    	{
    		Field field=new TimeArrayField(fieldId.strFieldId,lsValue);  
    		this.setObjectValue(fieldId,FieldType.TIME_ARRAY_TYPE,lsValue,field,this);
    	}
    	return this;
    }
    
    public CDO setDateTimeArrayValue(String strFieldId,String[] strsValue)
    {    
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	
    	if(fieldId.nType==FieldId.ARRAY_ELEMENT)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	else
    	{
    		Field field=new DateTimeArrayField(fieldId.strFieldId,strsValue);     		
    		this.setObjectValue(fieldId,FieldType.DATETIME_ARRAY_TYPE,strsValue,field,this);
    	}
    	return this;
    }

    public CDO setDateTimeArrayValue(String strFieldId,long[] lsValue)
    {    	
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	
    	if(fieldId.nType==FieldId.ARRAY_ELEMENT)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	else
    	{
    		Field field=new DateTimeArrayField(fieldId.strFieldId,lsValue);     		
    		this.setObjectValue(fieldId,FieldType.DATETIME_ARRAY_TYPE,lsValue,field,this);
    	}
    	return this;
    }
    
    public CDO setCDOArrayValue(String strFieldId,CDO[] cdosValue)
    {    	
    	this.setCDOListValue(strFieldId, Arrays.asList(cdosValue));
    	return this;
    }
    
    public CDO setCDOListValue(String strFieldId, List<CDO> cdosValue) {
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	
    	if(fieldId.nType==FieldId.ARRAY_ELEMENT)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	else
    	{
    		Field field=new CDOArrayField(fieldId.strFieldId,cdosValue); 
    		this.setObjectValue(fieldId,FieldType.CDO_ARRAY_TYPE,cdosValue,field,this);
    	}
    	return this;
	}
  
    
    /**
     *  构造结构，保存数值到指定位置
     * @param fieldId
     * @param nType
     * @param objValue
     * @param field
     * @param cdoRoot
     */
   void setObjectValue(FieldId fieldId,int nType,Object objValue,Field field,CDO cdoRoot)
	{
   	if(fieldId.nType==FieldId.SIMPLE)
   	{
   		this.putItem(fieldId.strFieldId,field);
   	}
   	else if(fieldId.nType==FieldId.MULTI_LEVEL)
   	{
   		CDO cdoMain=this.getCDOValue(fieldId.strMainFieldId);    	
   		cdoMain.putItem(fieldId.strFieldId, field);
   	}
   	else
   	{//Array Element    		
   		FieldId fieldIdMain=this.parseFieldId(fieldId.strMainFieldId);
   		if(fieldIdMain==null)
   		{
   			throw new RuntimeException("Invalid FieldId "+fieldId.strFieldId);
   		}
   		
   		Field arrField= null;
   		int nIndex =-1;
		arrField=this.getObject(fieldIdMain,this);
	    if(arrField==null)
	    {
			throw new RuntimeException("FieldId "+fieldId.strMainFieldId+" not exist");
	    }
		nIndex=this.getIndexValue(fieldId.strIndexFieldId,cdoRoot);	
		
   		switch(arrField.getFieldType().getType())
   		{
   			case FieldType.BOOLEAN_ARRAY_TYPE:
   			{
   				((BooleanArrayField)arrField).setValueAt(nIndex,(Boolean)objValue);
   				break;
   			}
   			case FieldType.BYTE_ARRAY_TYPE:
   			{
   				((ByteArrayField)arrField).setValueAt(nIndex,(Byte)objValue);
   				break;
   			}
   			case FieldType.SHORT_ARRAY_TYPE:
   			{
   				((ShortArrayField)arrField).setValueAt(nIndex,(Short)objValue);
   				break;
   			}
   			case FieldType.INTEGER_ARRAY_TYPE:
   			{
   				((IntegerArrayField)arrField).setValueAt(nIndex,(Integer)objValue);
   				break;
   			}
   			case FieldType.LONG_ARRAY_TYPE:
   			{
   				((LongArrayField)arrField).setValueAt(nIndex,(Long)objValue);
   				break;
   			}
   			case FieldType.STRING_ARRAY_TYPE:
   			{
   				((StringArrayField)arrField).setValueAt(nIndex,(String)objValue);
   				break;
   			}
   			case FieldType.DATE_ARRAY_TYPE:
   			{
   				if(objValue instanceof String){
   					((DateArrayField)arrField).setValueAt(nIndex,(String)objValue);
   				}else{
   					((DateArrayField)arrField).setLongValueAt(nIndex,(Long)objValue);
   				}
   				break;
   			}
   			case FieldType.TIME_ARRAY_TYPE:
   			{	
   				if(objValue instanceof String){
   					((TimeArrayField)arrField).setValueAt(nIndex,(String)objValue);
   				}else{
   					((TimeArrayField)arrField).setLongValueAt(nIndex,(Long)objValue);
   				}
   				break;
   			}
   			case FieldType.DATETIME_ARRAY_TYPE:
   			{
   				if(objValue instanceof String){
   					((DateTimeArrayField)arrField).setValueAt(nIndex,(String)objValue);
   				}else{
   					((DateTimeArrayField)arrField).setLongValueAt(nIndex,(Long)objValue);
   				}
   				break;
   			}
	   		case FieldType.CDO_ARRAY_TYPE:
	   		{
	   				((CDOArrayField)arrField).setValueAt(nIndex,(CDO)objValue);
	   				break;
	   		}
   		}
   	}
	}
    
    /**
     * 检查一个字段是否存在
     * 
     * @param strFieldId
     * @return
     */
    public boolean exists(String strFieldId)
    {
    	try
		{
			return checkField(strFieldId);
		}
		catch(Exception e)
		{
			return false;
		}
    }
    
    /**
     * 提供非抛异常的方式检查
     * 
     * @param strFieldId
     * @return
     */
    private boolean checkField(String strFieldId)
    {
    	Field objExt=this.hmItem.get(strFieldId);
    	if(objExt!=null)
    	{//直接子节点
    		return true;
    	}
    	
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
    		return false;
    	}
    	
    	objExt=getObject(fieldId,this);
    	return objExt!=null;
    }
    
    /**
     * 判断CDO容器中是否有对象
     * CDO容器中如果没有任何对象则返回true
     * CDO容器中有对象,则返回false
     * @return true:
     */
    public boolean isEmpty()
    {
    	if(this.hmItem.size()==0)
    	{
    		return true;
    	}
    	return false;
    }
    
    /**
     * 判断CDO容器中指定对象是否为空
     * 如果指定对象不存在,则返回true;如果指定对象也是个容器且容器为空,则返回true;其它情况为false.
     * @param strFieldId
     * @return
     */
    public boolean isEmpty(String strFieldId)
    {
    	Field objExt = null;
    	try
		{    		
    		objExt = getObject(strFieldId);  
		}catch(Exception e)
		{//不存在
			return true;
		}
		if(objExt==null)
		{
			return true;
		}
		try
		{
			int length=0;
			if(objExt.getFieldType().getType()>=FieldType.BOOLEAN_ARRAY_TYPE){
				length=((ArrayField)objExt).getLength();
			}
			return length==0;
			
		}catch(Exception e)
		{//有值,但不是数据组类型
			return false;
		}
    }
        
   
    public Field getField(String strFieldId)
    {
    	return this.getObject(strFieldId);
    }
    
    
    public CDO setObjectExt(String strFieldId, Field field) throws RuntimeException
    {
    	FieldId fieldId=this.parseFieldId(strFieldId);
    	if(fieldId==null)
    	{
			throw new RuntimeException("Invalid FieldId "+strFieldId);
    	}
    	
   		this.setObjectValue(fieldId,field.getFieldType().getType(),field.getObjectValue(),field,this);
   		return this;
    }
	
	public void remove(String key)
	{
		Field objExt=hmItem.remove(key);
		if(objExt!=null && objExt.getFieldType().getType()==FieldType.FILE_TYPE && !key.contains(".")){
			fileCount--;
		}
	}

	public void clear(){
		hmItem.clear();
	}
	
	public Set<String> keySet()
	{
		return hmItem.keySet();
	}
	
	/**
	 * 深层释放，释放所有数据
	 */
	 public void deepRelease(){		
		if(hmItem!=null){
			Entry<String, Field> entry=null;
			for(Iterator<Map.Entry<String, Field>> it=this.entrySet().iterator();it.hasNext();){
				entry=it.next();
				entry.getValue().release();								
			}				
			hmItem.clear();
			hmItem=null;		
		}		
	}

	 /**
	  * 浅层释放,仅释放 顶层CDO里常规变量[如：string,int],嵌套本层CDO及CDO数组里的数据, 并未进行有效释放
	  */
	 public void release(){
		 if(hmItem!=null){	
			Entry<String, Field> entry=null;
			Field field=null;
			for(Iterator<Map.Entry<String, Field>> it=this.entrySet().iterator();it.hasNext();){
				entry=it.next();
				field=entry.getValue();
				byte type=field.getFieldType().getType();
				if(type==FieldType.CDO_TYPE || type==FieldType.CDO_ARRAY_TYPE){
					continue;
				}
				field.release();								
			}				
			hmItem.clear();
			hmItem=null;	
		 }
	 }

	
	public int size(){
		return hmItem.size();
	}
	public int getSerialFileCount(){
		return this.fileCount;
	}
	
	public Set<Map.Entry<String,Field>> entrySet(){
		return this.hmItem.entrySet();
	}
    
    public Iterator<Map.Entry<String,Field>> iterator(){
    	return this.hmItem.entrySet().iterator();
    }
    
 
	//接口实现,所有实现接口函数的实现在此定义--------------------------------------------------------------------

	//构造函数,所有构造函数在此定义------------------------------------------------------------------------------

	public CDO(){
		//请在此加入初始化代码,内部对象和属性对象负责创建或赋初值,引用对象初始化为null，初始化完成后在设置各对象之间的关系
		hmItem	=new LinkedHashMap<String,Field>();		
	}
	
	/**
	 * toString 使用JSON格式形式输出
	 */
	public String toString()
	{
		return buffer2JSON(B2S_String);
	}	
	
	final static byte B2S_String=1;
	final static byte B2S_JSON=2;
	final static byte B2S_HtmlJSON=3;	
	final static byte B2S_MixHtmlJSON=4;
	
	private String buffer2JSON(int type){
		
		if(this.hmItem==null){
			return null;
		}
		
		StringBuilder strJSON=new StringBuilder(500);
		strJSON.append("{");		
		Entry<String, Field> entry=null;
		
		for(Iterator<Map.Entry<String, Field>> it=this.entrySet().iterator();it.hasNext();){
			entry=it.next();
			switch(type){
				case B2S_String:
					strJSON.append(entry.getValue().toString());
					break;
				case B2S_JSON:
					strJSON.append(entry.getValue().toJSON());
					break;	
				case B2S_HtmlJSON:
					strJSON.append(entry.getValue().toHtmlJSON());
					break;						
				case B2S_MixHtmlJSON:
					strJSON.append(entry.getValue().toMixHtmlJSON());
					break;
			}			
		}		
		// ugly 方法去掉最后一个","
		int _lastComma=strJSON.lastIndexOf(",");
		int _length=strJSON.length();
		if(_lastComma==_length-1)
		{
			strJSON.replace(_lastComma,_lastComma+1,"");
		}

		strJSON.append("}");
		return strJSON.toString();
	}
}
