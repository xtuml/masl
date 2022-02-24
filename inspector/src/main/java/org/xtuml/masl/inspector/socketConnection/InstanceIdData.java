//
// Filename : InstanceIdData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.processInterface.DomainMetaData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;
import org.xtuml.masl.inspector.socketConnection.ipc.WriteableObject;

public class InstanceIdData extends org.xtuml.masl.inspector.processInterface.InstanceIdData
        implements ReadableObject, WriteableObject {

    public InstanceIdData(final DomainMetaData domain) {
        super(domain);
    }

    public InstanceIdData(final Integer pk, final org.xtuml.masl.inspector.processInterface.ObjectMetaData meta) {
        super(meta);
        this.id = pk;
    }

    public InstanceIdData(final org.xtuml.masl.inspector.processInterface.ObjectMetaData meta) {
        super(meta);
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        final boolean valid = channel.readBoolean();
        if (valid) {
            id = channel.readInt();
        } else {
            id = null;
        }
    }

    @Override
    public void write(final CommunicationChannel channel) throws IOException {
        if (id != null) {
            channel.writeData(true);
            channel.writeData(id.intValue());
        } else {
            channel.writeData(false);
        }
    }
}
