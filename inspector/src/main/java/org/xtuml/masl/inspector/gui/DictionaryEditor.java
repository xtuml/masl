//
// File: EnumerateEditor.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.inspector.gui;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.xtuml.masl.inspector.gui.form.FormModel;
import org.xtuml.masl.inspector.gui.form.FormValueEditor;
import org.xtuml.masl.inspector.processInterface.DictionaryData;

public class DictionaryEditor implements FormValueEditor {

    public DictionaryEditor(final FormModel formModel, final int fieldNo) {
        table = new DictionaryTable(new DictionaryTableModel((DictionaryData) formModel.getValueAt(fieldNo),
                formModel.isValueEditable(fieldNo)));

        scrollpane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollpane.addMouseListener(table.getHeaderMouseAdapter());

    }

    @Override
    public Component getFormValueEditorComponent() {
        return scrollpane;
    }

    private final DictionaryTable table;
    private final JScrollPane scrollpane;

}
