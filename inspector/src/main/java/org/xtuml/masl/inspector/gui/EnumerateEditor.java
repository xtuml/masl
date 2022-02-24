//
// File: EnumerateEditor.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.inspector.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.xtuml.masl.inspector.gui.form.FormModel;
import org.xtuml.masl.inspector.gui.form.FormValueEditor;
import org.xtuml.masl.inspector.processInterface.EnumerateData;
import org.xtuml.masl.inspector.processInterface.EnumerateMetaData;

public class EnumerateEditor implements FormValueEditor {

    private Component component;
    private final int field;

    private final FormModel model;

    protected FormModel getModel() {
        return model;
    }

    public EnumerateEditor(final FormModel model, final int fieldNo) {
        this.model = model;
        this.field = fieldNo;

        if (getModel().isValueEditable(field)) {

            final EnumerateMetaData metaData = ((EnumerateData) getModel().getValueAt(field)).getMetaData();

            final JComboBox comboBox = new JComboBox(metaData.getEnums().toArray());
            comboBox.setSelectedItem(getModel().getValueAt(field));
            comboBox.setEditable(getModel().isValueEditable(field));
            comboBox.setEnabled(getModel().isValueEditable(field));
            comboBox.addActionListener((new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    getModel().setValueAt(comboBox.getSelectedItem(), field);
                }
            }));

            component = comboBox;
        } else {
            final JTextField textField = new JTextField();
            textField.setText(getModel().getValueAt(field).toString());
            textField.setEditable(false);
            component = textField;
        }
    }

    @Override
    public Component getFormValueEditorComponent() {
        return component;
    }

}
