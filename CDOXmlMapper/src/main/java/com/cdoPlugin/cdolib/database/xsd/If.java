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
public class If implements java.io.Serializable {

    /**
     * Field value1.
     */
    private java.lang.String value1;

    /**
     * Field operator.
     */
    private com.cdoPlugin.cdolib.database.xsd.types.IfOperatorType operator;

    /**
     * Field value2.
     */
    private java.lang.String value2;

    /**
     * Field type.
     */
    private com.cdoPlugin.cdolib.database.xsd.types.IfTypeType type;

    /**
     * Field nullThen.
     */
    private com.cdoPlugin.cdolib.database.xsd.NullThen nullThen;

    /**
     * Field then.
     */
    private com.cdoPlugin.cdolib.database.xsd.Then then;

    /**
     * Field _else.
     */
    private com.cdoPlugin.cdolib.database.xsd.Else _else;

    public If() {
        super();
    }

    /**
     * Returns the value of field 'else'.
     * 
     * @return the value of field 'Else'.
     */
    public com.cdoPlugin.cdolib.database.xsd.Else getElse() {
        return this._else;
    }

    /**
     * Returns the value of field 'nullThen'.
     * 
     * @return the value of field 'NullThen'.
     */
    public com.cdoPlugin.cdolib.database.xsd.NullThen getNullThen() {
        return this.nullThen;
    }

    /**
     * Returns the value of field 'operator'.
     * 
     * @return the value of field 'Operator'.
     */
    public com.cdoPlugin.cdolib.database.xsd.types.IfOperatorType getOperator() {
        return this.operator;
    }

    /**
     * Returns the value of field 'then'.
     * 
     * @return the value of field 'Then'.
     */
    public com.cdoPlugin.cdolib.database.xsd.Then getThen() {
        return this.then;
    }

    /**
     * Returns the value of field 'type'.
     * 
     * @return the value of field 'Type'.
     */
    public com.cdoPlugin.cdolib.database.xsd.types.IfTypeType getType() {
        return this.type;
    }

    /**
     * Returns the value of field 'value1'.
     * 
     * @return the value of field 'Value1'.
     */
    public java.lang.String getValue1() {
        return this.value1;
    }

    /**
     * Returns the value of field 'value2'.
     * 
     * @return the value of field 'Value2'.
     */
    public java.lang.String getValue2() {
        return this.value2;
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
     * Sets the value of field 'else'.
     * 
     * @param _else
     * @param else the value of field 'else'.
     */
    public void setElse(final com.cdoPlugin.cdolib.database.xsd.Else _else) {
        this._else = _else;
    }

    /**
     * Sets the value of field 'nullThen'.
     * 
     * @param nullThen the value of field 'nullThen'.
     */
    public void setNullThen(final com.cdoPlugin.cdolib.database.xsd.NullThen nullThen) {
        this.nullThen = nullThen;
    }

    /**
     * Sets the value of field 'operator'.
     * 
     * @param operator the value of field 'operator'.
     */
    public void setOperator(final com.cdoPlugin.cdolib.database.xsd.types.IfOperatorType operator) {
        this.operator = operator;
    }

    /**
     * Sets the value of field 'then'.
     * 
     * @param then the value of field 'then'.
     */
    public void setThen(final com.cdoPlugin.cdolib.database.xsd.Then then) {
        this.then = then;
    }

    /**
     * Sets the value of field 'type'.
     * 
     * @param type the value of field 'type'.
     */
    public void setType(final com.cdoPlugin.cdolib.database.xsd.types.IfTypeType type) {
        this.type = type;
    }

    /**
     * Sets the value of field 'value1'.
     * 
     * @param value1 the value of field 'value1'.
     */
    public void setValue1(final java.lang.String value1) {
        this.value1 = value1;
    }

    /**
     * Sets the value of field 'value2'.
     * 
     * @param value2 the value of field 'value2'.
     */
    public void setValue2(final java.lang.String value2) {
        this.value2 = value2;
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
     * com.cdoframework.cdolib.database.xsd.If
     */
    public static com.cdoPlugin.cdolib.database.xsd.If unmarshal(final java.io.Reader reader) throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (com.cdoPlugin.cdolib.database.xsd.If) org.exolab.castor.xml.Unmarshaller.unmarshal(com.cdoPlugin.cdolib.database.xsd.If.class, reader);
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
