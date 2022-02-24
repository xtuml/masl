// 
// Filename : CommandImpl.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public abstract class CommandImpl {

    protected abstract void execute(CommunicationChannel channel) throws java.io.IOException;

    public void perform(final CommunicationChannel channel) throws java.io.IOException {
        execute(channel);
        channel.flush();
    }
}
