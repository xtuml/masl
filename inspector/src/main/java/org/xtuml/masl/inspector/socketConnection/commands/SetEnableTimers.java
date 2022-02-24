// 
// Filename : SetEnableTimers.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

public class SetEnableTimers extends ToggleCommand {

    public SetEnableTimers(final boolean enabled) {
        super(ServerCommandId.ENABLE_TIMERS, enabled);
    }
}
