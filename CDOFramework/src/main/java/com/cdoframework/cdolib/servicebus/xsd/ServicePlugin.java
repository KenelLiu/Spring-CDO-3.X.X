/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.4.1</a>, using an XML
 * Schema.
 * $Id$
 */

package com.cdoframework.cdolib.servicebus.xsd;

import java.io.StringReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class ServicePlugin implements java.io.Serializable {

    private java.util.List<com.cdoframework.cdolib.servicebus.xsd.ServiceConfig> serviceConfigList;

    private java.util.List<com.cdoframework.cdolib.servicebus.xsd.DataService> dataServiceList;

    private java.util.List<com.cdoframework.cdolib.servicebus.xsd.TransService> transServiceList;

    public ServicePlugin() {
        super();
        this.serviceConfigList = new java.util.ArrayList<com.cdoframework.cdolib.servicebus.xsd.ServiceConfig>();
        this.dataServiceList = new java.util.ArrayList<com.cdoframework.cdolib.servicebus.xsd.DataService>();
        this.transServiceList = new java.util.ArrayList<com.cdoframework.cdolib.servicebus.xsd.TransService>();
    }

    /**
     * 
     * 
     * @param vDataService
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addDataService(final com.cdoframework.cdolib.servicebus.xsd.DataService vDataService) throws java.lang.IndexOutOfBoundsException {
        this.dataServiceList.add(vDataService);
    }

    /**
     * 
     * 
     * @param index
     * @param vDataService
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addDataService(final int index,final com.cdoframework.cdolib.servicebus.xsd.DataService vDataService) throws java.lang.IndexOutOfBoundsException {
        this.dataServiceList.add(index, vDataService);
    }

    /**
     * 
     * 
     * @param vServiceConfig
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addServiceConfig(final com.cdoframework.cdolib.servicebus.xsd.ServiceConfig vServiceConfig) throws java.lang.IndexOutOfBoundsException {
        this.serviceConfigList.add(vServiceConfig);
    }

    /**
     * 
     * 
     * @param index
     * @param vServiceConfig
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addServiceConfig(final int index,final com.cdoframework.cdolib.servicebus.xsd.ServiceConfig vServiceConfig) throws java.lang.IndexOutOfBoundsException {
        this.serviceConfigList.add(index, vServiceConfig);
    }

    /**
     * 
     * 
     * @param vTransService
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addTransService(final com.cdoframework.cdolib.servicebus.xsd.TransService vTransService) throws java.lang.IndexOutOfBoundsException {
        this.transServiceList.add(vTransService);
    }

    /**
     * 
     * 
     * @param index
     * @param vTransService
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addTransService(final int index,final com.cdoframework.cdolib.servicebus.xsd.TransService vTransService) throws java.lang.IndexOutOfBoundsException {
        this.transServiceList.add(index, vTransService);
    }

    /**
     * Method enumerateDataService.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends com.cdoframework.cdolib.servicebus.xsd.DataService> enumerateDataService() {
        return java.util.Collections.enumeration(this.dataServiceList);
    }

    /**
     * Method enumerateServiceConfig.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends com.cdoframework.cdolib.servicebus.xsd.ServiceConfig> enumerateServiceConfig() {
        return java.util.Collections.enumeration(this.serviceConfigList);
    }

    /**
     * Method enumerateTransService.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends com.cdoframework.cdolib.servicebus.xsd.TransService> enumerateTransService() {
        return java.util.Collections.enumeration(this.transServiceList);
    }

    /**
     * Method getDataService.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * com.cdoframework.cdolib.servicebus.xsd.DataService at the
     * given index
     */
    public com.cdoframework.cdolib.servicebus.xsd.DataService getDataService(final int index) throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this.dataServiceList.size()) {
            throw new IndexOutOfBoundsException("getDataService: Index value '" + index + "' not in range [0.." + (this.dataServiceList.size() - 1) + "]");
        }

        return dataServiceList.get(index);
    }

    /**
     * Method getDataService.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public com.cdoframework.cdolib.servicebus.xsd.DataService[] getDataService() {
        com.cdoframework.cdolib.servicebus.xsd.DataService[] array = new com.cdoframework.cdolib.servicebus.xsd.DataService[0];
        return this.dataServiceList.toArray(array);
    }

    /**
     * Method getDataServiceCount.
     * 
     * @return the size of this collection
     */
    public int getDataServiceCount() {
        return this.dataServiceList.size();
    }

    /**
     * Method getServiceConfig.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * com.cdoframework.cdolib.servicebus.xsd.ServiceConfig at the
     * given index
     */
    public com.cdoframework.cdolib.servicebus.xsd.ServiceConfig getServiceConfig(final int index) throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this.serviceConfigList.size()) {
            throw new IndexOutOfBoundsException("getServiceConfig: Index value '" + index + "' not in range [0.." + (this.serviceConfigList.size() - 1) + "]");
        }

        return serviceConfigList.get(index);
    }

    /**
     * Method getServiceConfig.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public com.cdoframework.cdolib.servicebus.xsd.ServiceConfig[] getServiceConfig() {
        com.cdoframework.cdolib.servicebus.xsd.ServiceConfig[] array = new com.cdoframework.cdolib.servicebus.xsd.ServiceConfig[0];
        return this.serviceConfigList.toArray(array);
    }

    /**
     * Method getServiceConfigCount.
     * 
     * @return the size of this collection
     */
    public int getServiceConfigCount() {
        return this.serviceConfigList.size();
    }

    /**
     * Method getTransService.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * com.cdoframework.cdolib.servicebus.xsd.TransService at the
     * given index
     */
    public com.cdoframework.cdolib.servicebus.xsd.TransService getTransService(final int index) throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this.transServiceList.size()) {
            throw new IndexOutOfBoundsException("getTransService: Index value '" + index + "' not in range [0.." + (this.transServiceList.size() - 1) + "]");
        }

        return transServiceList.get(index);
    }

    /**
     * Method getTransService.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public com.cdoframework.cdolib.servicebus.xsd.TransService[] getTransService() {
        com.cdoframework.cdolib.servicebus.xsd.TransService[] array = new com.cdoframework.cdolib.servicebus.xsd.TransService[0];
        return this.transServiceList.toArray(array);
    }

    /**
     * Method getTransServiceCount.
     * 
     * @return the size of this collection
     */
    public int getTransServiceCount() {
        return this.transServiceList.size();
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
     * Method iterateDataService.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends com.cdoframework.cdolib.servicebus.xsd.DataService> iterateDataService() {
        return this.dataServiceList.iterator();
    }

    /**
     * Method iterateServiceConfig.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends com.cdoframework.cdolib.servicebus.xsd.ServiceConfig> iterateServiceConfig() {
        return this.serviceConfigList.iterator();
    }

    /**
     * Method iterateTransService.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends com.cdoframework.cdolib.servicebus.xsd.TransService> iterateTransService() {
        return this.transServiceList.iterator();
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
     */
    public void removeAllDataService() {
        this.dataServiceList.clear();
    }

    /**
     */
    public void removeAllServiceConfig() {
        this.serviceConfigList.clear();
    }

    /**
     */
    public void removeAllTransService() {
        this.transServiceList.clear();
    }

    /**
     * Method removeDataService.
     * 
     * @param vDataService
     * @return true if the object was removed from the collection.
     */
    public boolean removeDataService(final com.cdoframework.cdolib.servicebus.xsd.DataService vDataService) {
        boolean removed = dataServiceList.remove(vDataService);
        return removed;
    }

    /**
     * Method removeDataServiceAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public com.cdoframework.cdolib.servicebus.xsd.DataService removeDataServiceAt(final int index) {
        java.lang.Object obj = this.dataServiceList.remove(index);
        return (com.cdoframework.cdolib.servicebus.xsd.DataService) obj;
    }

    /**
     * Method removeServiceConfig.
     * 
     * @param vServiceConfig
     * @return true if the object was removed from the collection.
     */
    public boolean removeServiceConfig(final com.cdoframework.cdolib.servicebus.xsd.ServiceConfig vServiceConfig) {
        boolean removed = serviceConfigList.remove(vServiceConfig);
        return removed;
    }

    /**
     * Method removeServiceConfigAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public com.cdoframework.cdolib.servicebus.xsd.ServiceConfig removeServiceConfigAt(final int index) {
        java.lang.Object obj = this.serviceConfigList.remove(index);
        return (com.cdoframework.cdolib.servicebus.xsd.ServiceConfig) obj;
    }

    /**
     * Method removeTransService.
     * 
     * @param vTransService
     * @return true if the object was removed from the collection.
     */
    public boolean removeTransService(final com.cdoframework.cdolib.servicebus.xsd.TransService vTransService) {
        boolean removed = transServiceList.remove(vTransService);
        return removed;
    }

    /**
     * Method removeTransServiceAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public com.cdoframework.cdolib.servicebus.xsd.TransService removeTransServiceAt(final int index) {
        java.lang.Object obj = this.transServiceList.remove(index);
        return (com.cdoframework.cdolib.servicebus.xsd.TransService) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vDataService
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setDataService(final int index,final com.cdoframework.cdolib.servicebus.xsd.DataService vDataService) throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this.dataServiceList.size()) {
            throw new IndexOutOfBoundsException("setDataService: Index value '" + index + "' not in range [0.." + (this.dataServiceList.size() - 1) + "]");
        }

        this.dataServiceList.set(index, vDataService);
    }

    /**
     * 
     * 
     * @param vDataServiceArray
     */
    public void setDataService(final com.cdoframework.cdolib.servicebus.xsd.DataService[] vDataServiceArray) {
        //-- copy array
        dataServiceList.clear();

        for (int i = 0; i < vDataServiceArray.length; i++) {
                this.dataServiceList.add(vDataServiceArray[i]);
        }
    }

    /**
     * 
     * 
     * @param index
     * @param vServiceConfig
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setServiceConfig(final int index,final com.cdoframework.cdolib.servicebus.xsd.ServiceConfig vServiceConfig) throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this.serviceConfigList.size()) {
            throw new IndexOutOfBoundsException("setServiceConfig: Index value '" + index + "' not in range [0.." + (this.serviceConfigList.size() - 1) + "]");
        }

        this.serviceConfigList.set(index, vServiceConfig);
    }

    /**
     * 
     * 
     * @param vServiceConfigArray
     */
    public void setServiceConfig(final com.cdoframework.cdolib.servicebus.xsd.ServiceConfig[] vServiceConfigArray) {
        //-- copy array
        serviceConfigList.clear();

        for (int i = 0; i < vServiceConfigArray.length; i++) {
                this.serviceConfigList.add(vServiceConfigArray[i]);
        }
    }

    /**
     * 
     * 
     * @param index
     * @param vTransService
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setTransService(final int index,final com.cdoframework.cdolib.servicebus.xsd.TransService vTransService) throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this.transServiceList.size()) {
            throw new IndexOutOfBoundsException("setTransService: Index value '" + index + "' not in range [0.." + (this.transServiceList.size() - 1) + "]");
        }

        this.transServiceList.set(index, vTransService);
    }

    /**
     * 
     * 
     * @param vTransServiceArray
     */
    public void setTransService(final com.cdoframework.cdolib.servicebus.xsd.TransService[] vTransServiceArray) {
        //-- copy array
        transServiceList.clear();

        for (int i = 0; i < vTransServiceArray.length; i++) {
                this.transServiceList.add(vTransServiceArray[i]);
        }
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
     * com.cdoframework.cdolib.servicebus.xsd.ServicePlugin
     */
    public static com.cdoframework.cdolib.servicebus.xsd.ServicePlugin unmarshal(final java.io.Reader reader) throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (com.cdoframework.cdolib.servicebus.xsd.ServicePlugin) org.exolab.castor.xml.Unmarshaller.unmarshal(com.cdoframework.cdolib.servicebus.xsd.ServicePlugin.class, reader);
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
	 * 序列化ServicePlugin对象
	 */	
	private static Log logger=LogFactory.getLog(ServicePlugin.class);
	public static ServicePlugin   fromXML(String strXML) throws Exception{
		StringReader reader=null;
		try{
			reader=new StringReader(strXML);
			return ServicePlugin.unmarshal(reader);
		}finally{
			if(reader!=null){try{reader.close();}catch(Exception e){
					logger.error("fromXML:"+e.getMessage(),e);
				}
			}
		}
	}
}
