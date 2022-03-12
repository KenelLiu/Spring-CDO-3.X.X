/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.4.1</a>, using an XML
 * Schema.
 * $Id$
 */

package com.cdoframework.cdolib.database.xsd;

/**
 * 
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class SQLBlockTypeItem implements java.io.Serializable {

    /**
     * Internal choice value storage
     */
    private java.lang.Object _choiceValue;

    /**
     * Field outputTable.
     */
    private java.lang.String outputTable;

    /**
     * Field outputSQL.
     */
    private java.lang.String outputSQL;

    /**
     * Field outputField.
     */
    private java.lang.String outputField;

    /**
     * Field SQLIf.
     */
    private com.cdoframework.cdolib.database.xsd.SQLIf SQLIf;

    /**
     * Field SQLFor.
     */
    private com.cdoframework.cdolib.database.xsd.SQLFor SQLFor;

    /**
     * Field SQLSwitch.
     */
    private com.cdoframework.cdolib.database.xsd.SQLSwitch SQLSwitch;

    public SQLBlockTypeItem() {
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
     * Returns the value of field 'outputField'.
     * 
     * @return the value of field 'OutputField'.
     */
    public java.lang.String getOutputField() {
        return this.outputField;
    }

    /**
     * Returns the value of field 'outputSQL'.
     * 
     * @return the value of field 'OutputSQL'.
     */
    public java.lang.String getOutputSQL() {
        return this.outputSQL;
    }

    /**
     * Returns the value of field 'outputTable'.
     * 
     * @return the value of field 'OutputTable'.
     */
    public java.lang.String getOutputTable() {
        return this.outputTable;
    }

    /**
     * Returns the value of field 'SQLFor'.
     * 
     * @return the value of field 'SQLFor'.
     */
    public com.cdoframework.cdolib.database.xsd.SQLFor getSQLFor() {
        return this.SQLFor;
    }

    /**
     * Returns the value of field 'SQLIf'.
     * 
     * @return the value of field 'SQLIf'.
     */
    public com.cdoframework.cdolib.database.xsd.SQLIf getSQLIf() {
        return this.SQLIf;
    }

    /**
     * Returns the value of field 'SQLSwitch'.
     * 
     * @return the value of field 'SQLSwitch'.
     */
    public com.cdoframework.cdolib.database.xsd.SQLSwitch getSQLSwitch() {
        return this.SQLSwitch;
    }

    /**
     * Sets the value of field 'outputField'.
     * 
     * @param outputField the value of field 'outputField'.
     */
    public void setOutputField(final java.lang.String outputField) {
        this.outputField = outputField;
        this._choiceValue = outputField;
    }

    /**
     * Sets the value of field 'outputSQL'.
     * 
     * @param outputSQL the value of field 'outputSQL'.
     */
    public void setOutputSQL(final java.lang.String outputSQL) {
        this.outputSQL = outputSQL;
        this._choiceValue = outputSQL;
    }

    /**
     * Sets the value of field 'outputTable'.
     * 
     * @param outputTable the value of field 'outputTable'.
     */
    public void setOutputTable(final java.lang.String outputTable) {
        this.outputTable = outputTable;
        this._choiceValue = outputTable;
    }

    /**
     * Sets the value of field 'SQLFor'.
     * 
     * @param SQLFor the value of field 'SQLFor'.
     */
    public void setSQLFor(final com.cdoframework.cdolib.database.xsd.SQLFor SQLFor) {
        this.SQLFor = SQLFor;
        this._choiceValue = SQLFor;
    }

    /**
     * Sets the value of field 'SQLIf'.
     * 
     * @param SQLIf the value of field 'SQLIf'.
     */
    public void setSQLIf(final com.cdoframework.cdolib.database.xsd.SQLIf SQLIf) {
        this.SQLIf = SQLIf;
        this._choiceValue = SQLIf;
    }

    /**
     * Sets the value of field 'SQLSwitch'.
     * 
     * @param SQLSwitch the value of field 'SQLSwitch'.
     */
    public void setSQLSwitch(final com.cdoframework.cdolib.database.xsd.SQLSwitch SQLSwitch) {
        this.SQLSwitch = SQLSwitch;
        this._choiceValue = SQLSwitch;
    }

}
