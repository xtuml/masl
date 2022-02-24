//
// Filename : SetTraceLines.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

public class SetTraceLines extends ToggleCommand {

    public SetTraceLines(final boolean enabled) {
        super(ServerCommandId.TRACE_LINES, enabled);
    }
}
