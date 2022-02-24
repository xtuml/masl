//
// Filename : RunningCommand.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import org.xtuml.masl.inspector.processInterface.ProcessConnection;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class RunningCommand extends CommandImpl {

    public RunningCommand() {
    }

    @Override
    protected void execute(final CommunicationChannel channel) throws java.io.IOException {
        ProcessConnection.getConnection().setRunning();
    }
}
