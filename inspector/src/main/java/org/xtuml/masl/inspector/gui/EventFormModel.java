// 
// Filename : ParameterBox.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import org.xtuml.masl.inspector.gui.form.AbstractFormModel;
import org.xtuml.masl.inspector.processInterface.EventData;
import org.xtuml.masl.inspector.processInterface.InstanceIdData;

class EventFormModel extends AbstractFormModel {

    EventFormModel(final EventData event) {
        this.event = event;
        nameField = 0;
        sourceField = nameField + (event.getSourceInstanceId() == null ? 0 : 1);
        destField = sourceField + (event.getDestInstanceId() == null ? 0 : 1);
        paramStart = destField + 1;
    }

    @Override
    public Class<?> getFieldClass(final int fieldIndex) {
        if (fieldIndex == nameField) {
            return Void.class;
        } else if (fieldIndex == sourceField) {
            return InstanceIdData.class;
        } else if (fieldIndex == destField) {
            return InstanceIdData.class;
        } else {
            return event.getParameters()[fieldIndex - paramStart].getClass();
        }
    }

    @Override
    public int getFieldCount() {
        return paramStart + event.getParameters().length;
    }

    @Override
    public String getFieldName(final int fieldIndex) {
        if (fieldIndex == nameField) {
            return event.getEvent().getParentObject().getName() + "." + event.getEvent().getName();
        } else if (fieldIndex == sourceField) {
            return "source";
        } else if (fieldIndex == destField) {
            return "destination";
        } else {
            return event.getEvent().getParameters()[fieldIndex - paramStart].getName();
        }
    }

    @Override
    public Object getValueAt(final int fieldIndex) {
        if (fieldIndex == nameField) {
            return null;
        }
        if (fieldIndex == sourceField) {
            return event.getSourceInstanceId();
        } else if (fieldIndex == destField) {
            return event.getDestInstanceId();
        } else {
            return event.getParameters()[fieldIndex - paramStart];
        }
    }

    @Override
    public boolean isValueEditable(final int fieldIndex) {
        return false;
    }

    @Override
    public void setValueAt(final Object aValue, final int fieldIndex) {
    }

    private final EventData event;
    private final int nameField;
    private final int sourceField;
    private final int destField;
    private final int paramStart;

}
