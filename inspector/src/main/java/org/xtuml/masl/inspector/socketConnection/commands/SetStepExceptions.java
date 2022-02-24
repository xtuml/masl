// 
// Filename : SetStepExceptions.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

public class SetStepExceptions extends ToggleCommand {

    public SetStepExceptions(final boolean enabled) {
        super(ServerCommandId.STEP_EXCEPTIONS, enabled);
    }
}
