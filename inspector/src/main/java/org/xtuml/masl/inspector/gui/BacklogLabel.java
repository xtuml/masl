//
// Filename : ProcessStatusLabel.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import javax.swing.JLabel;

import org.xtuml.masl.inspector.processInterface.BacklogListener;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;
import org.xtuml.masl.inspector.processInterface.WeakBacklogListener;

class BacklogLabel extends JLabel implements BacklogListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public BacklogLabel() {
        ProcessConnection.getConnection().addBacklogListener(new WeakBacklogListener(this));
    }

    public void setBacklog(final long backlogMillis) {
        if (backlogMillis >= 100) {
            final long minutes = (backlogMillis / (60 * 1000));
            // round seconds to 1 decimal place
            final double seconds = ((double) ((backlogMillis % 60000) / 100) / 10);

            setText("Backlog: " + (minutes > 0 ? minutes + "m " : "") + seconds + "s");
        } else {
            setText("");
        }
    }

    @Override
    public void backlogChanged(final org.xtuml.masl.inspector.processInterface.BacklogEvent e) {
        setBacklog(e.getBacklogMillis());
    }
}
