// 
// Filename : SetTraceIPC.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class SetPluginFlag extends CommandStub<VoidType> {

    private final String pluginName;
    private final String flagName;
    private final boolean value;

    public SetPluginFlag(final String pluginName, final String flagName, final boolean value) {
        super(ServerCommandId.SET_PLUGIN_FLAG);
        this.pluginName = pluginName;
        this.flagName = flagName;
        this.value = value;

    }

    @Override
    public VoidType execute(final CommunicationChannel channel) throws IOException {
        channel.writeData(pluginName);
        channel.writeData(flagName);
        channel.writeData(value);
        return null;
    }

}
