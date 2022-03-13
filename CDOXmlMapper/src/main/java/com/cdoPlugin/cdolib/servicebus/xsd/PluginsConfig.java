/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.4.1</a>, using an XML
 * Schema.
 * $Id$
 */

package com.cdoPlugin.cdolib.servicebus.xsd;

import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class PluginsConfig implements java.io.Serializable {

    private java.util.List<java.lang.String> pluginXMLResourceList;

    public PluginsConfig() {
        super();
        this.pluginXMLResourceList = new java.util.ArrayList<java.lang.String>();
    }

    /**
     * 
     * 
     * @param vPluginXMLResource
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPluginXMLResource(final java.lang.String vPluginXMLResource) throws java.lang.IndexOutOfBoundsException {
        this.pluginXMLResourceList.add(vPluginXMLResource);
    }

    /**
     * 
     * 
     * @param index
     * @param vPluginXMLResource
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPluginXMLResource(final int index,final java.lang.String vPluginXMLResource) throws java.lang.IndexOutOfBoundsException {
        this.pluginXMLResourceList.add(index, vPluginXMLResource);
    }

    /**
     * Method enumeratePluginXMLResource.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends java.lang.String> enumeratePluginXMLResource() {
        return java.util.Collections.enumeration(this.pluginXMLResourceList);
    }

    /**
     * Method getPluginXMLResource.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the java.lang.String at the given index
     */
    public java.lang.String getPluginXMLResource(final int index) throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this.pluginXMLResourceList.size()) {
            throw new IndexOutOfBoundsException("getPluginXMLResource: Index value '" + index + "' not in range [0.." + (this.pluginXMLResourceList.size() - 1) + "]");
        }

        return (java.lang.String) pluginXMLResourceList.get(index);
    }

    /**
     * Method getPluginXMLResource.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public java.lang.String[] getPluginXMLResource() {
        java.lang.String[] array = new java.lang.String[0];
        return this.pluginXMLResourceList.toArray(array);
    }

    /**
     * Method getPluginXMLResourceCount.
     * 
     * @return the size of this collection
     */
    public int getPluginXMLResourceCount() {
        return this.pluginXMLResourceList.size();
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
     * Method iteratePluginXMLResource.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends java.lang.String> iteratePluginXMLResource() {
        return this.pluginXMLResourceList.iterator();
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
    public void removeAllPluginXMLResource() {
        this.pluginXMLResourceList.clear();
    }

    /**
     * Method removePluginXMLResource.
     * 
     * @param vPluginXMLResource
     * @return true if the object was removed from the collection.
     */
    public boolean removePluginXMLResource(final java.lang.String vPluginXMLResource) {
        boolean removed = pluginXMLResourceList.remove(vPluginXMLResource);
        return removed;
    }

    /**
     * Method removePluginXMLResourceAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public java.lang.String removePluginXMLResourceAt(final int index) {
        java.lang.Object obj = this.pluginXMLResourceList.remove(index);
        return (java.lang.String) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vPluginXMLResource
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setPluginXMLResource(final int index,final java.lang.String vPluginXMLResource) throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this.pluginXMLResourceList.size()) {
            throw new IndexOutOfBoundsException("setPluginXMLResource: Index value '" + index + "' not in range [0.." + (this.pluginXMLResourceList.size() - 1) + "]");
        }

        this.pluginXMLResourceList.set(index, vPluginXMLResource);
    }

    /**
     * 
     * 
     * @param vPluginXMLResourceArray
     */
    public void setPluginXMLResource(final java.lang.String[] vPluginXMLResourceArray) {
        //-- copy array
        pluginXMLResourceList.clear();

        for (int i = 0; i < vPluginXMLResourceArray.length; i++) {
                this.pluginXMLResourceList.add(vPluginXMLResourceArray[i]);
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
     * com.cdoframework.cdolib.servicebus.xsd.PluginsConfig
     */
    public static com.cdoPlugin.cdolib.servicebus.xsd.PluginsConfig unmarshal(final java.io.Reader reader) throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (com.cdoPlugin.cdolib.servicebus.xsd.PluginsConfig) org.exolab.castor.xml.Unmarshaller.unmarshal(com.cdoPlugin.cdolib.servicebus.xsd.PluginsConfig.class, reader);
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
  	 */    
    private static Log logger=LogFactory.getLog(PluginsConfig.class);
    public static PluginsConfig  fromXML(java.lang.String strXML) throws Exception{
    	StringReader reader=null;
    	try{
    		reader=new StringReader(strXML);    		
    		return PluginsConfig.unmarshal(reader);
    	}finally{
    		if(reader!=null){
    			try{reader.close();}catch(Exception e){
    				logger.error("fromXML:"+e.getMessage(),e);
    			}
    		}
    	}
    }  
}
