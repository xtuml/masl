// 
// Filename : PauseExecution.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class PauseExecution extends CommandStub<VoidType> {

    public PauseExecution() {
        super(ServerCommandId.PAUSE_EXECUTION);
    }

    @Override
    public VoidType execute(final CommunicationChannel channel) throws IOException {
        return null;
    }

}
