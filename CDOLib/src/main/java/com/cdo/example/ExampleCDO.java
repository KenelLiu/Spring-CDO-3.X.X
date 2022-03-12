package com.cdo.example;

import java.io.File;

import com.cdo.field.array.BooleanArrayField;
import com.cdo.field.array.ByteArrayField;
import com.cdo.field.array.DateArrayField;
import com.cdo.field.array.DateTimeArrayField;
import com.cdo.field.array.DoubleArrayField;
import com.cdo.field.array.FloatArrayField;
import com.cdo.field.array.IntegerArrayField;
import com.cdo.field.array.LongArrayField;
import com.cdo.field.array.ShortArrayField;
import com.cdo.field.array.TimeArrayField;
import com.cdoframework.cdolib.data.cdo.CDO;

public class ExampleCDO {



	public static CDO getCDO(){
			CDO cdo = new CDO();		
			cdo.setByteValue("byte1", (byte)2);
			cdo.setByteArrayValue("bytes", new byte[]{1,2,3});
			cdo.setBooleanValue("boolean1", true);
			cdo.setBooleanArrayValue("booleanArr", new boolean[]{false,true,true,false});
			cdo.setShortValue("short1", (short)100);
			cdo.setShortArrayValue("shortArr", new short[]{100,200,300});
			cdo.setIntegerValue("int1", 300);
			cdo.setIntegerArrayValue("intArr", new int[]{400,500,600});
			cdo.setLongValue("long1", 7000);
			cdo.setLongArrayValue("longArr", new long[]{9000,10000});
			cdo.setFloatValue("float1", 3.0f);
			cdo.setFloatArrayValue("floatArr", new float[]{1.0f,2.0f,3.0f});
			cdo.setDoubleValue("double1", 5.0);
			cdo.setDoubleArrayValue("doubleArr", new double[]{6.0,7.0,8.0});
			cdo.setStringValue("str1", "张三");
			cdo.setStringArrayValue("strArr", new String[]{ "张3", "张4", "张5",null});
			cdo.setDateValue("date1", "2016-05-01");
			cdo.setDateArrayValue("dateArr", new String[]{"2012-05-01","2013-05-01","2014-05-01"});
			cdo.setTimeValue("time1", "20:00:00");
			cdo.setTimeArrayValue("timeArr", new String[]{"17:00:00","18:00:00","20:00:00"});
			cdo.setDateTimeValue("dateTime1", "2012-05-01 20:00:00");
			cdo.setDateTimeArrayValue("dateTimeArr", new String[]{"2012-05-01 20:00:00","2013-05-01 21:00:00","2014-05-01 22:00:00"});
			cdo.setFileValue("file", new File("D:/test/excel.xlsx"));
			cdo.setNullValue("KK");
			for(int i=0;i<5;i++){

				System.out.println(((ByteArrayField)cdo.getField("bytes")).getValue());
				System.out.println(((ByteArrayField)cdo.getField("bytes")).getValue());
			    ((ByteArrayField)cdo.getField("bytes")).setValueAt(0, (byte)2);			   
				System.out.println(((ByteArrayField)cdo.getField("bytes")).getValue());
				System.out.println(((ByteArrayField)cdo.getField("bytes")).getValueAt(0));
				System.out.println(((ByteArrayField)cdo.getField("bytes")).getValueAt(1));
				System.out.println(((ByteArrayField)cdo.getField("bytes")).getValueAt(2));
				
				System.out.println(cdo.getBooleanValue("boolean1"));
				System.out.println(((BooleanArrayField)cdo.getField("booleanArr")).getValue());
				System.out.println(((BooleanArrayField)cdo.getField("booleanArr")).getValue());
			    ((BooleanArrayField)cdo.getField("booleanArr")).setValueAt(3, true);
				System.out.println(((BooleanArrayField)cdo.getField("booleanArr")).getValue());
				System.out.println(((BooleanArrayField)cdo.getField("booleanArr")).getValueAt(0));				
				System.out.println(((BooleanArrayField)cdo.getField("booleanArr")).getValueAt(3));
				
				System.out.println(((DateArrayField)cdo.getField("dateArr")).getValueAt(1));
				System.out.println(((DateArrayField)cdo.getField("dateArr")).getValueAt(1));
				System.out.println(((DateArrayField)cdo.getField("dateArr")).getValue()[2]);
				System.out.println(((DateArrayField)cdo.getField("dateArr")).getValue()[2]);
				System.out.println(((DateArrayField)cdo.getField("dateArr")).getLength());
				System.out.println(((DateArrayField)cdo.getField("dateArr")).getLength());	
				
				System.out.println(((TimeArrayField)cdo.getField("timeArr")).getValueAt(1));
				System.out.println(((TimeArrayField)cdo.getField("timeArr")).getValueAt(1));
				System.out.println(((TimeArrayField)cdo.getField("timeArr")).getValue()[0]);
				System.out.println(((TimeArrayField)cdo.getField("timeArr")).getValue()[0]);
				System.out.println(((TimeArrayField)cdo.getField("timeArr")).getLength());
				System.out.println(((TimeArrayField)cdo.getField("timeArr")).getLength());
				
				System.out.println(((DateTimeArrayField)cdo.getField("dateTimeArr")).getValueAt(1));
				System.out.println(((DateTimeArrayField)cdo.getField("dateTimeArr")).getValueAt(0));
				System.out.println(((DateTimeArrayField)cdo.getField("dateTimeArr")).getValue()[0]);
				System.out.println(((DateTimeArrayField)cdo.getField("dateTimeArr")).getValue()[0]);
				System.out.println(((DateTimeArrayField)cdo.getField("dateTimeArr")).getLength());
				System.out.println(((DateTimeArrayField)cdo.getField("dateTimeArr")).getLength());
				
				System.out.println(((ShortArrayField)cdo.getField("shortArr")).getValueAt(1));
				System.out.println(((ShortArrayField)cdo.getField("shortArr")).getValueAt(1));
				System.out.println(((ShortArrayField)cdo.getField("shortArr")).getLength());
				System.out.println(((ShortArrayField)cdo.getField("shortArr")).getLength());			
				System.out.println(((ShortArrayField)cdo.getField("shortArr")).getValue()[0]);
				System.out.println(((ShortArrayField)cdo.getField("shortArr")).getValue()[0]);

				System.out.println(((IntegerArrayField)cdo.getField("intArr")).getValueAt(1));
				System.out.println(((IntegerArrayField)cdo.getField("intArr")).getValueAt(1));
				System.out.println(((IntegerArrayField)cdo.getField("intArr")).getLength());
				System.out.println(((IntegerArrayField)cdo.getField("intArr")).getLength());			
				System.out.println(((IntegerArrayField)cdo.getField("intArr")).getValue()[0]);
				System.out.println(((IntegerArrayField)cdo.getField("intArr")).getValue()[0]);
				
				System.out.println(((LongArrayField)cdo.getField("longArr")).getValueAt(1));
				System.out.println(((LongArrayField)cdo.getField("longArr")).getValueAt(1));
				System.out.println(((LongArrayField)cdo.getField("longArr")).getLength());
				System.out.println(((LongArrayField)cdo.getField("longArr")).getLength());			
				System.out.println(((LongArrayField)cdo.getField("longArr")).getValue()[0]);
				System.out.println(((LongArrayField)cdo.getField("longArr")).getValue()[0]);
				
				System.out.println(((FloatArrayField)cdo.getField("floatArr")).getValueAt(1));
				System.out.println(((FloatArrayField)cdo.getField("floatArr")).getValueAt(1));
				System.out.println(((FloatArrayField)cdo.getField("floatArr")).getLength());
				System.out.println(((FloatArrayField)cdo.getField("floatArr")).getLength());			
				System.out.println(((FloatArrayField)cdo.getField("floatArr")).getValue()[0]);
				System.out.println(((FloatArrayField)cdo.getField("floatArr")).getValue()[0]);
				
				System.out.println(((DoubleArrayField)cdo.getField("doubleArr")).getValueAt(1));
				System.out.println(((DoubleArrayField)cdo.getField("doubleArr")).getValueAt(1));
				System.out.println(((DoubleArrayField)cdo.getField("doubleArr")).getLength());
				System.out.println(((DoubleArrayField)cdo.getField("doubleArr")).getLength());			
				System.out.println(((DoubleArrayField)cdo.getField("doubleArr")).getValue()[0]);
				System.out.println(((DoubleArrayField)cdo.getField("doubleArr")).getValue()[0]);				
			}	

			return cdo;
	 }
}
