// 
// Filename : SetStepEvents.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

public class SetStepEvents extends ToggleCommand {

    public SetStepEvents(final boolean enabled) {
        super(ServerCommandId.STEP_EVENTS, enabled);
    }
}
