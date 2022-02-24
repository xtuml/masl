//
// Filename : SetTraceEvents.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

public class SetTraceEvents extends ToggleCommand {

    public SetTraceEvents(final boolean enabled) {
        super(ServerCommandId.TRACE_EVENTS, enabled);
    }
}
