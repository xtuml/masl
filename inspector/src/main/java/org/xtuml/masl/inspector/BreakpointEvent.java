// 
// Filename : BreakpointEvent.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector;

import org.xtuml.masl.inspector.processInterface.SourcePosition;

public class BreakpointEvent extends java.util.EventObject {

    private final SourcePosition pos;
    private final Boolean active;

    public BreakpointEvent(final Object source, final SourcePosition pos, final Boolean active) {
        super(source);
        this.pos = pos;
        this.active = active;
    }

    public SourcePosition getPosition() {
        return pos;
    }

    public Boolean getActive() {
        return active;
    }
}
