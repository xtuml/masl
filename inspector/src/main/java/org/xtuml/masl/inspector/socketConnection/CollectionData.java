//
// Filename : CollectionData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;
import org.xtuml.masl.inspector.socketConnection.ipc.WriteableObject;

public class CollectionData extends org.xtuml.masl.inspector.processInterface.CollectionData
        implements ReadableObject, WriteableObject {

    public CollectionData(final org.xtuml.masl.inspector.processInterface.TypeMetaData type) {
        super(type);
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        startIndex = channel.readInt();
        final int endIndex = channel.readInt();

        data.clear();

        for (int i = 0; i < endIndex - startIndex + 1; ++i) {
            data.add(type.getDataObject());
            ((ReadableObject) data.get(i)).read(channel);
        }
    }

    @Override
    public void write(final CommunicationChannel channel) throws IOException {
        channel.writeData(startIndex);
        channel.writeData(getEndIndex());

        for (int i = 0; i < getLength(); ++i) {
            channel.writeData(data.get(i));
        }
    }
}
