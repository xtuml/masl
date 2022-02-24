//
// Filename : StackList.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.xtuml.masl.inspector.processInterface.ProcessConnection;
import org.xtuml.masl.inspector.processInterface.ProcessStatusEvent;
import org.xtuml.masl.inspector.processInterface.ProcessStatusListener;
import org.xtuml.masl.inspector.processInterface.StackFrame;
import org.xtuml.masl.inspector.processInterface.WeakProcessStatusListener;

public class StackList extends ComboList {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static class StackListModel extends AbstractListModel implements ProcessStatusListener {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        StackListModel() {
            ProcessConnection.getConnection().addProcessStatusListener(new WeakProcessStatusListener(this));
            if (ProcessConnection.getConnection().getCurrentStatus() == ProcessConnection.PAUSED) {
                stack = null;
            } else {
                stack = emptyStack;
            }
        }

        @Override
        public Object getElementAt(final int index) {
            if (stack == null) {
                lazyLoadStack();
            }
            return stack[stack.length - index - 1];
        }

        @Override
        public int getSize() {
            if (stack == null) {
                lazyLoadStack();
            }
            return stack.length;
        }

        private final static StackFrame[] emptyStack = new StackFrame[0];
        private StackFrame[] stack;

        private void lazyLoadStack() {
            try {
                stack = ProcessConnection.getConnection().getStack();
            } catch (final java.rmi.RemoteException re) {
                stack = emptyStack;
                re.printStackTrace();
            }
        }

        @Override
        public void processStatusChanged(final ProcessStatusEvent e) {
            if (e.getStatus() == ProcessConnection.IDLE) {
                stack = emptyStack;
            } else if (e.getStatus() == ProcessConnection.PAUSED) {
                stack = null;
            } else if (e.getStatus() == ProcessConnection.RUNNING) {
                // Leave it alone
            }
            fireContentsChanged(this, 0, Integer.MAX_VALUE);
        }
    }

    StackList() {
        super(new StackListModel());
        model = (StackListModel) getModel();

        setCellRenderer(new DefaultListCellRenderer() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public java.awt.Component getListCellRendererComponent(final JList list, final Object value,
                    final int index, final boolean isSelected, final boolean cellHasFocus) {
                return super.getListCellRendererComponent(list,
                        ((StackFrame) value).getPosition().getSource().getFullyQualifiedName(), index, isSelected,
                        cellHasFocus);
            }
        });

        model.addListDataListener(new ListDataListener() {

            @Override
            public void contentsChanged(final ListDataEvent e) {
                clearSelection();
                if (model.getSize() > 0) {
                    setSelectedIndex(0);
                }
            }

            @Override
            public void intervalAdded(final ListDataEvent e) {
                clearSelection();
                if (model.getSize() > 0) {
                    setSelectedIndex(0);
                }
            }

            @Override
            public void intervalRemoved(final ListDataEvent e) {
                clearSelection();
                if (model.getSize() > 0) {
                    setSelectedIndex(0);
                }
            }

        });

    }

    private final StackListModel model;

}
