//
// Filename : FrameCloseAction.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

public class FrameCloseAction extends javax.swing.AbstractAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final javax.swing.JFrame frame;

    public FrameCloseAction(final javax.swing.JFrame frame) {
        super("Close");
        this.frame = frame;
    }

    @Override
    public void actionPerformed(final java.awt.event.ActionEvent e) {
        frame.dispose();
    }
}
