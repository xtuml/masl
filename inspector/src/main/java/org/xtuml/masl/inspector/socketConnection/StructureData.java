//
// Filename : StructureData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;
import org.xtuml.masl.inspector.socketConnection.ipc.WriteableObject;

public class StructureData extends org.xtuml.masl.inspector.processInterface.StructureData
        implements ReadableObject, WriteableObject {

    public StructureData(final org.xtuml.masl.inspector.processInterface.StructureMetaData meta) {
        super(meta);
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        for (int i = 0; i < meta.getAttributes().length; i++) {
            attributes[i] = meta.getAttributes()[i].getType().getDataObject();
            ((ReadableObject) attributes[i]).read(channel);
        }
    }

    @Override
    public void write(final CommunicationChannel channel) throws IOException {
        for (int i = 0; i < meta.getAttributes().length; i++) {
            channel.writeData(attributes[i]);
        }
    }

}
