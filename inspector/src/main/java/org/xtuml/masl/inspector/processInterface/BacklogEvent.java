//
// Filename : ProcessStatusEvent.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public class BacklogEvent extends java.util.EventObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final long backlogMillis;

    public BacklogEvent(final Object source, final long backlogMillis) {
        super(source);
        this.backlogMillis = backlogMillis;
    }

    public long getBacklogMillis() {
        return backlogMillis;
    }

}
