//
// File: InvokeDialog.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.inspector.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import org.xtuml.masl.inspector.processInterface.CollectionData;
import org.xtuml.masl.inspector.processInterface.DictionaryData;
import org.xtuml.masl.inspector.processInterface.EventData;
import org.xtuml.masl.inspector.processInterface.InstanceData;
import org.xtuml.masl.inspector.processInterface.StringData;
import org.xtuml.masl.inspector.processInterface.StructureData;
import org.xtuml.masl.inspector.processInterface.TimerData;

public class EditDataDialog extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JScrollPane editor;

    public EditDataDialog(final Object data, final boolean editable) {
        super();
        setModal(editable);

        if (data instanceof CollectionData) {
            final CollectionTable seqTable = new CollectionTable((CollectionData) data, editable);
            editor = new JScrollPane(seqTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            editor.addMouseListener(seqTable.getHeaderMouseAdapter());
        }
        if (data instanceof DictionaryData) {
            final DictionaryTable dictTable = new DictionaryTable((DictionaryData) data, editable);
            editor = new JScrollPane(dictTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            editor.addMouseListener(dictTable.getHeaderMouseAdapter());
        } else if (data instanceof StructureData) {
            final AttributesForm form = new AttributesForm((StructureData) data, editable);
            editor = new JScrollPane(form, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        } else if (data instanceof InstanceData) {
            final AttributesForm form = new AttributesForm((InstanceData) data, editable);
            editor = new JScrollPane(form, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        } else if (data instanceof TimerData) {
            final AttributesForm form = new AttributesForm((TimerData) data);
            editor = new JScrollPane(form, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        } else if (data instanceof EventData) {
            final AttributesForm form = new AttributesForm((EventData) data);
            editor = new JScrollPane(form, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        } else if (data instanceof StringData) {
            final JTextArea area = new JTextArea(((StringData) data).getValue());
            area.setEditable(editable);
            editor = new JScrollPane(area, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            area.setInputVerifier(new InputVerifier() {

                @Override
                public boolean shouldYieldFocus(final JComponent component) {
                    ((StringData) data).setValue(area.getText());
                    return super.shouldYieldFocus(component);
                }

                @Override
                public boolean verify(final JComponent component) {
                    return true;
                }

            });
        } else {
            editor = null;
        }

        setLayout(new BorderLayout());

        final JPanel buttons = new JPanel();

        if (editor != null) {
            getContentPane().add(editor, BorderLayout.CENTER);
        }
        getContentPane().add(buttons, BorderLayout.SOUTH);

        final JButton okButton = new JButton("Done");

        buttons.add(okButton);

        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (okButton.isFocusOwner()) {
                    dispose();
                }
            }
        });

        setResizable(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();
    }

    protected void display() {
        setVisible(true);
    }

}
