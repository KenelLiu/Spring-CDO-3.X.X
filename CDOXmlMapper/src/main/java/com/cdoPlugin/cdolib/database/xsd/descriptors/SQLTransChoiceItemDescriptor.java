/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.4.1</a>, using an XML
 * Schema.
 * $Id$
 */

package com.cdoPlugin.cdolib.database.xsd.descriptors;

  import com.cdoPlugin.cdolib.database.xsd.SQLTransChoiceItem;

/**
 * 
 * 
 * @version $Revision$ $Date$
 */
public class SQLTransChoiceItemDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {

    /**
     * Field _elementDefinition.
     */
    private boolean _elementDefinition;

    /**
     * Field _nsPrefix.
     */
    private java.lang.String _nsPrefix;

    /**
     * Field _nsURI.
     */
    private java.lang.String _nsURI;

    /**
     * Field _xmlName.
     */
    private java.lang.String _xmlName;

    /**
     * Field _identity.
     */
    private org.exolab.castor.xml.XMLFieldDescriptor _identity;

    public SQLTransChoiceItemDescriptor() {
        super();
        _elementDefinition = false;

        //-- set grouping compositor
        setCompositorAsChoice();
        org.exolab.castor.xml.util.XMLFieldDescriptorImpl  desc           = null;
        org.exolab.castor.mapping.FieldHandler             handler        = null;
        org.exolab.castor.xml.FieldValidator               fieldValidator = null;
        //-- initialize attribute descriptors

        //-- initialize element descriptors

        //-- selectTable
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.cdoPlugin.cdolib.database.xsd.SelectTable.class, "selectTable", "SelectTable", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                return target.getSelectTable();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                    target.setSelectTable( (com.cdoPlugin.cdolib.database.xsd.SelectTable) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return new com.cdoPlugin.cdolib.database.xsd.SelectTable();
            }
        };
        desc.setSchemaType("com.cdoframework.cdolib.database.xsd.SelectTable");
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: selectTable
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- insert
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.cdoPlugin.cdolib.database.xsd.Insert.class, "insert", "Insert", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                return target.getInsert();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                    target.setInsert( (com.cdoPlugin.cdolib.database.xsd.Insert) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return new com.cdoPlugin.cdolib.database.xsd.Insert();
            }
        };
        desc.setSchemaType("com.cdoframework.cdolib.database.xsd.Insert");
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: insert
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- update
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.cdoPlugin.cdolib.database.xsd.Update.class, "update", "Update", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                return target.getUpdate();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                    target.setUpdate( (com.cdoPlugin.cdolib.database.xsd.Update) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return new com.cdoPlugin.cdolib.database.xsd.Update();
            }
        };
        desc.setSchemaType("com.cdoframework.cdolib.database.xsd.Update");
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: update
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- delete
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.cdoPlugin.cdolib.database.xsd.Delete.class, "delete", "Delete", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                return target.getDelete();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                    target.setDelete( (com.cdoPlugin.cdolib.database.xsd.Delete) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return new com.cdoPlugin.cdolib.database.xsd.Delete();
            }
        };
        desc.setSchemaType("com.cdoframework.cdolib.database.xsd.Delete");
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: delete
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- selectField
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.cdoPlugin.cdolib.database.xsd.SelectField.class, "selectField", "SelectField", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                return target.getSelectField();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                    target.setSelectField( (com.cdoPlugin.cdolib.database.xsd.SelectField) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return new com.cdoPlugin.cdolib.database.xsd.SelectField();
            }
        };
        desc.setSchemaType("com.cdoframework.cdolib.database.xsd.SelectField");
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: selectField
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- selectRecord
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.cdoPlugin.cdolib.database.xsd.SelectRecord.class, "selectRecord", "SelectRecord", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                return target.getSelectRecord();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                    target.setSelectRecord( (com.cdoPlugin.cdolib.database.xsd.SelectRecord) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return new com.cdoPlugin.cdolib.database.xsd.SelectRecord();
            }
        };
        desc.setSchemaType("com.cdoframework.cdolib.database.xsd.SelectRecord");
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: selectRecord
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- selectRecordSet
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.cdoPlugin.cdolib.database.xsd.SelectRecordSet.class, "selectRecordSet", "SelectRecordSet", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                return target.getSelectRecordSet();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                    target.setSelectRecordSet( (com.cdoPlugin.cdolib.database.xsd.SelectRecordSet) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return new com.cdoPlugin.cdolib.database.xsd.SelectRecordSet();
            }
        };
        desc.setSchemaType("com.cdoframework.cdolib.database.xsd.SelectRecordSet");
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: selectRecordSet
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _if
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.cdoPlugin.cdolib.database.xsd.If.class, "_if", "If", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                return target.getIf();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                    target.setIf( (com.cdoPlugin.cdolib.database.xsd.If) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return new com.cdoPlugin.cdolib.database.xsd.If();
            }
        };
        desc.setSchemaType("com.cdoframework.cdolib.database.xsd.If");
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _if
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _for
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.cdoPlugin.cdolib.database.xsd.For.class, "_for", "For", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                return target.getFor();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                    target.setFor( (com.cdoPlugin.cdolib.database.xsd.For) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return new com.cdoPlugin.cdolib.database.xsd.For();
            }
        };
        desc.setSchemaType("com.cdoframework.cdolib.database.xsd.For");
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _for
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _switch
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.cdoPlugin.cdolib.database.xsd.Switch.class, "_switch", "Switch", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                return target.getSwitch();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                    target.setSwitch( (com.cdoPlugin.cdolib.database.xsd.Switch) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return new com.cdoPlugin.cdolib.database.xsd.Switch();
            }
        };
        desc.setSchemaType("com.cdoframework.cdolib.database.xsd.Switch");
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _switch
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- setVar
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.cdoPlugin.cdolib.database.xsd.SetVar.class, "setVar", "SetVar", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                return target.getSetVar();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                    target.setSetVar( (com.cdoPlugin.cdolib.database.xsd.SetVar) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return new com.cdoPlugin.cdolib.database.xsd.SetVar();
            }
        };
        desc.setSchemaType("com.cdoframework.cdolib.database.xsd.SetVar");
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: setVar
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- resetPage
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.cdoPlugin.cdolib.database.xsd.ResetPage.class, "resetPage", "ResetPage", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                return target.getResetPage();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    SQLTransChoiceItem target = (SQLTransChoiceItem) object;
                    target.setResetPage( (com.cdoPlugin.cdolib.database.xsd.ResetPage) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return new com.cdoPlugin.cdolib.database.xsd.ResetPage();
            }
        };
        desc.setSchemaType("com.cdoframework.cdolib.database.xsd.ResetPage");
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: resetPage
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
    }

    /**
     * Method getAccessMode.
     * 
     * @return the access mode specified for this class.
     */
    @Override()
    public org.exolab.castor.mapping.AccessMode getAccessMode() {
        return null;
    }

    /**
     * Method getIdentity.
     * 
     * @return the identity field, null if this class has no
     * identity.
     */
    @Override()
    public org.exolab.castor.mapping.FieldDescriptor getIdentity() {
        return _identity;
    }

    /**
     * Method getJavaClass.
     * 
     * @return the Java class represented by this descriptor.
     */
    @Override()
    public java.lang.Class getJavaClass() {
        return com.cdoPlugin.cdolib.database.xsd.SQLTransChoiceItem.class;
    }

    /**
     * Method getNameSpacePrefix.
     * 
     * @return the namespace prefix to use when marshaling as XML.
     */
    @Override()
    public java.lang.String getNameSpacePrefix() {
        return _nsPrefix;
    }

    /**
     * Method getNameSpaceURI.
     * 
     * @return the namespace URI used when marshaling and
     * unmarshaling as XML.
     */
    @Override()
    public java.lang.String getNameSpaceURI() {
        return _nsURI;
    }

    /**
     * Method getValidator.
     * 
     * @return a specific validator for the class described by this
     * ClassDescriptor.
     */
    @Override()
    public org.exolab.castor.xml.TypeValidator getValidator() {
        return this;
    }

    /**
     * Method getXMLName.
     * 
     * @return the XML Name for the Class being described.
     */
    @Override()
    public java.lang.String getXMLName() {
        return _xmlName;
    }

    /**
     * Method isElementDefinition.
     * 
     * @return true if XML schema definition of this Class is that
     * of a global
     * element or element with anonymous type definition.
     */
    public boolean isElementDefinition() {
        return _elementDefinition;
    }

}
