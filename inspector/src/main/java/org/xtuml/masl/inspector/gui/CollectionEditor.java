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
import org.xtuml.masl.inspector.processInterface.CollectionData;

public class CollectionEditor implements FormValueEditor {

    public CollectionEditor(final FormModel formModel, final int fieldNo) {
        table = new CollectionTable(new CollectionTableModel((CollectionData) formModel.getValueAt(fieldNo),
                formModel.isValueEditable(fieldNo)));

        scrollpane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollpane.addMouseListener(table.getHeaderMouseAdapter());

    }

    @Override
    public Component getFormValueEditorComponent() {
        return scrollpane;
    }

    private final CollectionTable table;
    private final JScrollPane scrollpane;

}
