// 
// Filename : SetStepBlocks.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

public class SetStepBlocks extends ToggleCommand {

    public SetStepBlocks(final boolean enabled) {
        super(ServerCommandId.STEP_BLOCKS, enabled);
    }
}
