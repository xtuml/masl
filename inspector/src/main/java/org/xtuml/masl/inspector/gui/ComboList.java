// 
// Filename : ComboList.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public abstract class ComboList extends JList implements java.awt.ItemSelectable {

    public ComboList(final ListModel model) {
        super(model);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (getSelectedValue() != null) {
                        fireItemStateChanged(new ItemEvent(ComboList.this, ItemEvent.ITEM_STATE_CHANGED,
                                getSelectedValue(), ItemEvent.SELECTED));
                    } else {
                        fireItemStateChanged(new ItemEvent(ComboList.this, ItemEvent.ITEM_STATE_CHANGED,
                                getSelectedValue(), ItemEvent.DESELECTED));
                    }
                }
            }
        });
    }

    @Override
    public Object[] getSelectedObjects() {
        final Object obj = getSelectedValue();
        if (obj != null) {
            return new Object[] { obj };
        } else {
            return null;
        }
    }

    @Override
    public void addItemListener(final ItemListener aListener) {
        listenerList.add(ItemListener.class, aListener);
    }

    @Override
    public void removeItemListener(final ItemListener aListener) {
        listenerList.remove(ItemListener.class, aListener);
    }

    protected void fireItemStateChanged(final ItemEvent e) {
        // Guaranteed to return a non-null array
        final Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ItemListener.class) {
                ((ItemListener) listeners[i + 1]).itemStateChanged(e);
            }
        }
    }

}
