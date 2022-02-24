// 
// Filename : ProcessStatusEvent.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public class ProcessStatusEvent extends java.util.EventObject {

    private final int status;
    private final SourcePosition position;

    public ProcessStatusEvent(final Object source, final int status, final SourcePosition position) {
        super(source);
        this.status = status;
        this.position = position;
    }

    public int getStatus() {
        return status;
    }

    public SourcePosition getPosition() {
        return position;
    }

}
