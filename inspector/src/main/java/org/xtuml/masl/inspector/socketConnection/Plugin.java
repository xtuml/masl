//
// File: Plugin.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;

public class Plugin implements org.xtuml.masl.inspector.processInterface.Plugin, ReadableObject {

    public static class Value implements Flag, Property, ReadableObject {

        @Override
        public String getName() {
            return valueName;
        }

        @Override
        public boolean isReadable() {
            return isReadable;
        }

        @Override
        public boolean isWriteable() {
            return isWriteable;
        }

        @Override
        public void read(final CommunicationChannel channel) throws IOException {
            valueName = channel.readString();
            isReadable = channel.readBoolean();
            isWriteable = channel.readBoolean();
        }

        private String valueName;
        private boolean isReadable;
        private boolean isWriteable;
    }

    @Override
    public String[] getActions() {
        return actions;
    }

    @Override
    public Flag[] getFlags() {
        return flags;
    }

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public Property[] getProperties() {
        return properties;
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        pluginName = channel.readString();
        actions = channel.readData(String[].class);
        flags = channel.readData(Value[].class);
        properties = channel.readData(Value[].class);
    }

    private String pluginName;
    private Property[] properties;
    private String[] actions;
    private Flag[] flags;

}
