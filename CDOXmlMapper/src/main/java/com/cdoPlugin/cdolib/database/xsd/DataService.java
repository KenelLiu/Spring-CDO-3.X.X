/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.4.1</a>, using an XML
 * Schema.
 * $Id$
 */

package com.cdoPlugin.cdolib.database.xsd;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cdoPlugin.cdolib.database.TransDefine;
import com.cdoPlugin.cdolib.servicebus.IServicePlugin;
import com.cdoPlugin.cdolib.servicebus.Service;
/**
 * 
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class DataService implements java.io.Serializable {

    /**
     * Internal choice value storage
     */
    private java.lang.Object _choiceValue;

    /**
     * Field dataServiceChoice.
     */
    private com.cdoPlugin.cdolib.database.xsd.DataServiceChoice dataServiceChoice;

    public DataService() {
        super();
    }

    /**
     * Returns the value of field 'choiceValue'. The field
     * 'choiceValue' has the following description: Internal choice
     * value storage
     * 
     * @return the value of field 'ChoiceValue'.
     */
    public java.lang.Object getChoiceValue() {
        return this._choiceValue;
    }

    /**
     * Returns the value of field 'dataServiceChoice'.
     * 
     * @return the value of field 'DataServiceChoice'.
     */
    public com.cdoPlugin.cdolib.database.xsd.DataServiceChoice getDataServiceChoice() {
        return this.dataServiceChoice;
    }

    /**
     * Method isValid.
     * 
     * @return true if this object is valid according to the schema
     */
    public boolean isValid() {
        try {
            validate();
        } catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    }

    /**
     * 
     * 
     * @param out
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void marshal(final java.io.Writer out) throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Marshaller.marshal(this, out);
    }

    /**
     * 
     * 
     * @param handler
     * @throws java.io.IOException if an IOException occurs during
     * marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     */
    public void marshal(final org.xml.sax.ContentHandler handler) throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Marshaller.marshal(this, handler);
    }

    /**
     * Sets the value of field 'dataServiceChoice'.
     * 
     * @param dataServiceChoice the value of field
     * 'dataServiceChoice'.
     */
    public void setDataServiceChoice(final com.cdoPlugin.cdolib.database.xsd.DataServiceChoice dataServiceChoice) {
        this.dataServiceChoice = dataServiceChoice;
        this._choiceValue = dataServiceChoice;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled
     * com.cdoframework.cdolib.database.xsd.DataService
     */
    public static com.cdoPlugin.cdolib.database.xsd.DataService unmarshal(final java.io.Reader reader) throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (com.cdoPlugin.cdolib.database.xsd.DataService) org.exolab.castor.xml.Unmarshaller.unmarshal(com.cdoPlugin.cdolib.database.xsd.DataService.class, reader);
    }

    /**
     * 
     * 
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void validate() throws org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    }
    /**
   	 * ***************************************manual  code *************************************************
   	 * ?????????DataService??????
   	 */
     //????????????,??????static????????????????????????------------------------------------------------------------------------  
   	private static Log logger=LogFactory.getLog(DataService.class);
   	//????????????,??????????????????????????????????????????????????????--------------------------------------------------------------
   	private Map<String,TransDefine> hmTransDefine = new HashMap<String,TransDefine>(30);


   	//????????????,??????????????????????????????????????????????????????????????????set??????-----------------------------------------------
   	private String strServiceName;
   	private IServicePlugin servicePlugin;

   	public String getStrServiceName() {
 		return strServiceName;
 	}

 	public IServicePlugin getServicePlugin() {
 		return servicePlugin;
 	}

 	//????????????,???????????????????????????????????????????????????public??????------------------------------------------------------
   	public static DataService fromXML(String strXML) throws Exception
   	{
   		StringReader reader=null;
   		try{
   			reader=new StringReader(strXML);  				
   			return DataService.unmarshal(reader);
   		}finally{
   			if(reader!=null){
   				try{reader.close();}catch(Exception e){
   					logger.error("fromXML:"+e.getMessage(),e);
   				}
   			}
   		}
   	}
   	
   	/**
   	 * 
   	 * @param strServiceName
   	 * @param service
   	 * @param servicePlugin
   	 * @param serviceBus
   	 * @return
   	 */
       public com.cdoframework.cdolib.base.Return init(String strServiceName,Service service,com.cdoPlugin.cdolib.servicebus.ServicePlugin servicePlugin,com.cdoPlugin.cdolib.servicebus.ServiceBus serviceBus
       		)
   	{
       	this.servicePlugin = servicePlugin;
       	this.strServiceName = strServiceName;
   		try{

   			DataServiceChoice[] dataServiceChoices = {this.getDataServiceChoice()};
    
   			for(DataServiceChoice dataServiceChoice:dataServiceChoices)
   			{
   				DataServiceChoiceItem[] dataServiceChoiceItems = dataServiceChoice.getDataServiceChoiceItem();
   				if(dataServiceChoiceItems==null)
   				{
   					continue;
   				}
   				for(DataServiceChoiceItem dataServiceChoiceItem:dataServiceChoiceItems)
   				{
   					if(dataServiceChoiceItem==null)
   					{
   						continue;
   					}  					
   					//sqlTrans
   					SQLTrans sqlTransTemp=dataServiceChoiceItem.getSQLTrans();
   					if(sqlTransTemp!=null)
   					{  					
   						com.cdoframework.cdolib.base.Return ret = parseSqlTrans(sqlTransTemp,service);
   						if(ret.getCode()!=0)
   						{
   							return ret;
   						}
   					}					
   				}				
   			}
   		}
   		catch(Exception e)
   		{
   			logger.error("init:"+e.getMessage(),e);
   			return com.cdoframework.cdolib.base.Return.valueOf(-1,e.getLocalizedMessage());
   		}
   		return com.cdoframework.cdolib.base.Return.OK;
   	}

       private com.cdoframework.cdolib.base.Return parseSqlTrans(SQLTrans sqlTransTemp,Service service)
       {
   		String strTransName = sqlTransTemp.getTransName();
   		sqlTransTemp.setDataService(this);
   		
   		TransDefine transDefine = new TransDefine();
   		transDefine.setSqlTrans(sqlTransTemp);
   		this.hmTransDefine.put(strTransName,transDefine);
   		TransDefine oldValue = service.putTransDefine(strTransName,transDefine);
   		if(oldValue!=null)
   		{
   			//TODO output log
   			return com.cdoframework.cdolib.base.Return.valueOf(-1,"duplicated transName :"+strTransName);
   		}
   		return com.cdoframework.cdolib.base.Return.OK;
       }
      

   	public Map<String,TransDefine> getTransMap()
   	{
   		return hmTransDefine;
   	} 
}
