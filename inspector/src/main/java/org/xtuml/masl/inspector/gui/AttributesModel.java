// 
// Filename : ParameterBox.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import org.xtuml.masl.inspector.gui.form.AbstractFormModel;
import org.xtuml.masl.inspector.processInterface.AttributeMetaData;
import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.processInterface.InstanceData;
import org.xtuml.masl.inspector.processInterface.StructureData;

class AttributesModel extends AbstractFormModel {

    AttributesModel(final StructureData structure, final boolean editable) {
        this.attributes = structure.getMetaData().getAttributes();
        this.attributeValues = structure.getAttributes();
        this.editable = editable;
    }

    AttributesModel(final InstanceData instance, final boolean editable) {
        this.attributes = instance.getMetaData().getAttributes();
        this.attributeValues = instance.getAttributes();
        this.editable = editable;
    }

    @Override
    public Class<?> getFieldClass(final int fieldIndex) {
        return attributes[fieldIndex].getType().getDataObject().getValue().getClass();
    }

    @Override
    public int getFieldCount() {
        return attributes.length;
    }

    @Override
    public String getFieldName(final int fieldIndex) {
        return attributes[fieldIndex].getName();
    }

    @Override
    public Object getValueAt(final int fieldIndex) {
        final DataValue<?> att = attributeValues[fieldIndex];
        if (att == null) {
            return "";
        } else {
            return att.getValue();
        }
    }

    public DataValue<?>[] getValues() {
        return attributeValues;
    }

    @Override
    public boolean isValueEditable(final int fieldIndex) {
        return editable && !attributes[fieldIndex].isReadOnly();
    }

    @Override
    public void setValueAt(final Object aValue, final int fieldIndex) {
        attributeValues[fieldIndex].setUncheckedValue(aValue);
    }

    private final AttributeMetaData[] attributes;
    private final DataValue<?>[] attributeValues;
    private final boolean editable;

}
