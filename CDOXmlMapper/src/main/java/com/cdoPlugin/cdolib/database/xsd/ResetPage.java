/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.4.1</a>, using an XML
 * Schema.
 * $Id$
 */

package com.cdoPlugin.cdolib.database.xsd;

/**
 * 
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class ResetPage implements java.io.Serializable {

    /**
     * Field pageIndex.
     */
    private java.lang.String pageIndex = "{nPageIndex}";

    /**
     * Field pageSize.
     */
    private java.lang.String pageSize = "{nPageSize}";

    /**
     * Field totalCount.
     */
    private java.lang.String totalCount = "{nCount}";

    /**
     * Field fetchPage.
     */
    private com.cdoPlugin.cdolib.database.xsd.types.ResetPageFetchPageType fetchPage = com.cdoPlugin.cdolib.database.xsd.types.ResetPageFetchPageType.fromValue("FIRST");

    /**
     * Field startIndex.
     */
    private java.lang.String startIndex = "{nStartIndex}";

    /**
     * Field rowNum.
     */
    private java.lang.String rowNum = "{nRowNum}";

    public ResetPage() {
        super();
        setPageIndex("{nPageIndex}");
        setPageSize("{nPageSize}");
        setTotalCount("{nCount}");
        setFetchPage(com.cdoPlugin.cdolib.database.xsd.types.ResetPageFetchPageType.fromValue("FIRST"));
        setStartIndex("{nStartIndex}");
        setRowNum("{nRowNum}");
    }

    /**
     * Returns the value of field 'fetchPage'.
     * 
     * @return the value of field 'FetchPage'.
     */
    public com.cdoPlugin.cdolib.database.xsd.types.ResetPageFetchPageType getFetchPage() {
        return this.fetchPage;
    }

    /**
     * Returns the value of field 'pageIndex'.
     * 
     * @return the value of field 'PageIndex'.
     */
    public java.lang.String getPageIndex() {
        return this.pageIndex;
    }

    /**
     * Returns the value of field 'pageSize'.
     * 
     * @return the value of field 'PageSize'.
     */
    public java.lang.String getPageSize() {
        return this.pageSize;
    }

    /**
     * Returns the value of field 'rowNum'.
     * 
     * @return the value of field 'RowNum'.
     */
    public java.lang.String getRowNum() {
        return this.rowNum;
    }

    /**
     * Returns the value of field 'startIndex'.
     * 
     * @return the value of field 'StartIndex'.
     */
    public java.lang.String getStartIndex() {
        return this.startIndex;
    }

    /**
     * Returns the value of field 'totalCount'.
     * 
     * @return the value of field 'TotalCount'.
     */
    public java.lang.String getTotalCount() {
        return this.totalCount;
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
     * Sets the value of field 'fetchPage'.
     * 
     * @param fetchPage the value of field 'fetchPage'.
     */
    public void setFetchPage(final com.cdoPlugin.cdolib.database.xsd.types.ResetPageFetchPageType fetchPage) {
        this.fetchPage = fetchPage;
    }

    /**
     * Sets the value of field 'pageIndex'.
     * 
     * @param pageIndex the value of field 'pageIndex'.
     */
    public void setPageIndex(final java.lang.String pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * Sets the value of field 'pageSize'.
     * 
     * @param pageSize the value of field 'pageSize'.
     */
    public void setPageSize(final java.lang.String pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Sets the value of field 'rowNum'.
     * 
     * @param rowNum the value of field 'rowNum'.
     */
    public void setRowNum(final java.lang.String rowNum) {
        this.rowNum = rowNum;
    }

    /**
     * Sets the value of field 'startIndex'.
     * 
     * @param startIndex the value of field 'startIndex'.
     */
    public void setStartIndex(final java.lang.String startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * Sets the value of field 'totalCount'.
     * 
     * @param totalCount the value of field 'totalCount'.
     */
    public void setTotalCount(final java.lang.String totalCount) {
        this.totalCount = totalCount;
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
     * com.cdoframework.cdolib.database.xsd.ResetPage
     */
    public static com.cdoPlugin.cdolib.database.xsd.ResetPage unmarshal(final java.io.Reader reader) throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (com.cdoPlugin.cdolib.database.xsd.ResetPage) org.exolab.castor.xml.Unmarshaller.unmarshal(com.cdoPlugin.cdolib.database.xsd.ResetPage.class, reader);
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

}
