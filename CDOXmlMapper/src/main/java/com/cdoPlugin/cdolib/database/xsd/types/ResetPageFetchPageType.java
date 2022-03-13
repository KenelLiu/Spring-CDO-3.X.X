/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.4.1</a>, using an XML
 * Schema.
 * $Id$
 */

package com.cdoPlugin.cdolib.database.xsd.types;

/**
 * Enumeration ResetPageFetchPageType.
 * 
 * @version $Revision$ $Date$
 */
public enum ResetPageFetchPageType {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant FIRST
     */
    FIRST("FIRST"),
    /**
     * Constant LAST
     */
    LAST("LAST");
    /**
     * Field value.
     */
    private final java.lang.String value;

    /**
     * Field enumConstants.
     */
    private static final java.util.Map<java.lang.String, ResetPageFetchPageType> enumConstants = new java.util.HashMap<java.lang.String, ResetPageFetchPageType>();


    static {
        for (ResetPageFetchPageType c: ResetPageFetchPageType.values()) {
            ResetPageFetchPageType.enumConstants.put(c.value, c);
        }

    }

    private ResetPageFetchPageType(final java.lang.String value) {
        this.value = value;
    }

    /**
     * Method fromValue.
     * 
     * @param value
     * @return the constant for this value
     */
    public static com.cdoPlugin.cdolib.database.xsd.types.ResetPageFetchPageType fromValue(final java.lang.String value) {
        ResetPageFetchPageType c = ResetPageFetchPageType.enumConstants.get(value);
        if (c != null) {
            return c;
        }
        throw new IllegalArgumentException(value);
    }

    /**
     * 
     * 
     * @param value
     */
    public void setValue(final java.lang.String value) {
    }

    /**
     * Method toString.
     * 
     * @return the value of this constant
     */
    public java.lang.String toString() {
        return this.value;
    }

    /**
     * Method value.
     * 
     * @return the value of this constant
     */
    public java.lang.String value() {
        return this.value;
    }

}
