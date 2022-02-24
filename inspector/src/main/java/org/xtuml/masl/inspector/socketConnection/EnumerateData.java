// 
// Filename : EnumerateData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;
import org.xtuml.masl.inspector.socketConnection.ipc.WriteableObject;

public class EnumerateData extends org.xtuml.masl.inspector.processInterface.EnumerateData
        implements ReadableObject, WriteableObject {

    public EnumerateData(final org.xtuml.masl.inspector.processInterface.EnumerateMetaData meta) {
        super(meta);
    }

    public EnumerateData(final int index, final org.xtuml.masl.inspector.processInterface.EnumerateMetaData meta) {
        super(meta);
        this.index = index;
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        index = channel.readInt();
    }

    @Override
    public void write(final CommunicationChannel channel) throws IOException {
        channel.writeData(index);
    }
}
