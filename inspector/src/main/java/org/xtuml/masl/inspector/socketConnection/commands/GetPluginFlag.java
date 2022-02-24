//
// Filename : GetTraceIPC.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class GetPluginFlag extends CommandStub<Boolean> {

    private final String pluginName;
    private final String flagName;

    public GetPluginFlag(final String pluginName, final String flagName) {
        super(ServerCommandId.GET_PLUGIN_FLAG);
        this.pluginName = pluginName;
        this.flagName = flagName;
    }

    @Override
    public Boolean execute(final CommunicationChannel channel) throws IOException {
        channel.writeData(pluginName);
        channel.writeData(flagName);
        channel.flush();
        return channel.readBoolean();
    }

}
