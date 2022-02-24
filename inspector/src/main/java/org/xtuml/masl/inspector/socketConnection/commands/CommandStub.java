// 
// Filename : CommandStub.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.EOFException;
import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ProcessConnection;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

abstract class VoidType {
}

public abstract class CommandStub<ReturnType> {

    private final ServerCommandId commandId;

    public CommandStub(final ServerCommandId commandId) {
        this.commandId = commandId;
    }

    public ReturnType perform(final CommunicationChannel channel) throws IOException {
        synchronized (channel) {
            try {
                channel.writeData(commandId);
                final ReturnType ret = execute(channel);
                channel.flush();
                channel.readBoolean(); // ignore ack
                return ret;
            } catch (final EOFException e) {
                if (ProcessConnection.getConnection() != null) {
                    ProcessConnection.getConnection().lostConnection();
                }
                throw e;
            }
        }
    }

    protected abstract ReturnType execute(CommunicationChannel channel) throws java.io.IOException;

}
