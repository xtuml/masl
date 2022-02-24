//
// File: ParameterBox.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.inspector.gui;

import org.xtuml.masl.inspector.processInterface.CollectionData;
import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.processInterface.DictionaryData;
import org.xtuml.masl.inspector.processInterface.EnumerateData;
import org.xtuml.masl.inspector.processInterface.InstanceData;
import org.xtuml.masl.inspector.processInterface.ParameterMetaData;
import org.xtuml.masl.inspector.processInterface.StructureData;

public class ParameterForm extends org.xtuml.masl.inspector.gui.form.Form {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ParameterForm(final ParameterMetaData[] parameters) {
        super(new ParameterModel(parameters));
    }

    public DataValue<?>[] getValues() {
        return ((ParameterModel) getModel()).getValues();
    }

    @Override
    protected void installEditors() {
        setDefaultValueEditor(EnumerateData.class, EnumerateEditor.class);
        setDefaultValueEditor(StructureData.class, AttributesForm.StructureEditor.class);
        setDefaultValueEditor(InstanceData.class, AttributesForm.InstanceEditor.class);
        setDefaultValueEditor(CollectionData.class, CollectionEditor.class);
        setDefaultValueEditor(DictionaryData.class, DictionaryEditor.class);
    }

}
