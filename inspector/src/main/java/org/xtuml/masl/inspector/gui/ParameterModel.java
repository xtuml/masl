//
// Filename : ParameterBox.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import org.xtuml.masl.inspector.gui.form.AbstractFormModel;
import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.processInterface.ParameterMetaData;

class ParameterModel extends AbstractFormModel {

    ParameterModel(final ParameterMetaData[] parameters) {
        this.parameters = parameters;
        this.parameterValues = new DataValue<?>[parameters.length];

        for (int i = 0; i < parameters.length; ++i) {
            parameterValues[i] = parameters[i].getType().getDataObject();
        }
    }

    @Override
    public Class<?> getFieldClass(final int fieldIndex) {
        return parameters[fieldIndex].getType().getDataObject().getValue().getClass();
    }

    @Override
    public int getFieldCount() {
        return parameters.length;
    }

    @Override
    public String getFieldName(final int fieldIndex) {
        return parameters[fieldIndex].getName();
    }

    @Override
    public Object getValueAt(final int fieldIndex) {
        return parameterValues[fieldIndex].getValue();
    }

    public DataValue<?>[] getValues() {
        return parameterValues;
    }

    @Override
    public boolean isValueEditable(final int fieldIndex) {
        return true;
    }

    @Override
    public void setValueAt(final Object aValue, final int fieldIndex) {
        parameterValues[fieldIndex].setUncheckedValue(aValue);
    }

    private final ParameterMetaData[] parameters;

    private final DataValue<?>[] parameterValues;

}
