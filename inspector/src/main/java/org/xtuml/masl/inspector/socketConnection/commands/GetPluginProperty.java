//
// Filename : GetTraceIPC.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class GetPluginProperty extends CommandStub<String> {

    private final String pluginName;
    private final String propertyName;

    public GetPluginProperty(final String pluginName, final String propertyName) {
        super(ServerCommandId.GET_PLUGIN_PROPERTY);
        this.pluginName = pluginName;
        this.propertyName = propertyName;
    }

    @Override
    public String execute(final CommunicationChannel channel) throws IOException {
        channel.writeData(pluginName);
        channel.writeData(propertyName);
        channel.flush();
        return channel.readString();
    }

}
