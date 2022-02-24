// 
// Filename : SetTraceExceptions.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

public class SetTraceExceptions extends ToggleCommand {

    public SetTraceExceptions(final boolean enabled) {
        super(ServerCommandId.TRACE_EXCEPTIONS, enabled);
    }
}
