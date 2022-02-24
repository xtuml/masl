// 
// Filename : CurrentPositionCommand.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import org.xtuml.masl.inspector.socketConnection.ProcessConnection;
import org.xtuml.masl.inspector.socketConnection.SourcePosition;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class CurrentPositionCommand extends CommandImpl {

    public CurrentPositionCommand() {
    }

    @Override
    protected void execute(final CommunicationChannel channel) throws java.io.IOException {
        SourcePosition position = channel.readData(SourcePosition.class);

        if (position.getSource() == null) {
            position = null;
        }

        ProcessConnection.getConnection().setCurrentPosition(position);
    }
}
