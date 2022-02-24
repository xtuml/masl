//
// Filename : SetTraceIPC.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class SetPluginProperty extends CommandStub<VoidType> {

    private final String pluginName;
    private final String propertyName;
    private final String value;

    public SetPluginProperty(final String pluginName, final String propertyName, final String value) {
        super(ServerCommandId.SET_PLUGIN_PROPERTY);
        this.pluginName = pluginName;
        this.propertyName = propertyName;
        this.value = value;

    }

    @Override
    public VoidType execute(final CommunicationChannel channel) throws IOException {
        channel.writeData(pluginName);
        channel.writeData(propertyName);
        channel.writeData(value);
        return null;
    }

}
