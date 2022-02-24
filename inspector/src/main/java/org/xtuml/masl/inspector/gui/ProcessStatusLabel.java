// 
// Filename : ProcessStatusLabel.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import javax.swing.JLabel;

import org.xtuml.masl.inspector.processInterface.ProcessConnection;
import org.xtuml.masl.inspector.processInterface.ProcessStatusListener;
import org.xtuml.masl.inspector.processInterface.SourcePosition;
import org.xtuml.masl.inspector.processInterface.WeakProcessStatusListener;

class ProcessStatusLabel extends JLabel implements ProcessStatusListener {

    public ProcessStatusLabel() {
        setStatus(ProcessConnection.getConnection().getCurrentStatus(),
                ProcessConnection.getConnection().getCurrentPosition());
        ProcessConnection.getConnection().addProcessStatusListener(new WeakProcessStatusListener(this));
    }

    public void setStatus(final int status, final SourcePosition position) {
        switch (ProcessConnection.getConnection().getCurrentStatus()) {
        case ProcessConnection.RUNNING:
            setText("Running");
            break;
        case ProcessConnection.IDLE:
            setText("Idle");
            break;
        case ProcessConnection.PAUSED:
            if (position != null) {
                final int line = position.getLineNo();

                if (line == SourcePosition.LAST_LINE) {
                    setText("Paused leaving " + position.getSource().getFullyQualifiedName());
                } else if (line == SourcePosition.FIRST_LINE) {
                    setText("Paused entering " + position.getSource().getFullyQualifiedName());
                } else {
                    setText("Paused at " + position.getSource().getFullyQualifiedName() + " line " + line);
                }
            } else {
                setText("Paused (idle)");
            }
            break;
        default:
            setText("Disconnected");
            break;
        }
    }

    @Override
    public void processStatusChanged(final org.xtuml.masl.inspector.processInterface.ProcessStatusEvent e) {
        setStatus(e.getStatus(), e.getPosition());
    }
}
