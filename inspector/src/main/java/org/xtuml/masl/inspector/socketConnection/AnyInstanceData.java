//
// Filename : DeviceData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;
import org.xtuml.masl.inspector.socketConnection.ipc.WriteableObject;

public class AnyInstanceData extends org.xtuml.masl.inspector.processInterface.AnyInstanceData
        implements ReadableObject, WriteableObject {

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        valid = channel.readBoolean();
    }

    @Override
    public void write(final CommunicationChannel channel) throws IOException {
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    private boolean valid;
}
