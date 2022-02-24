// 
// Filename : RelationshipData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;
import org.xtuml.masl.inspector.socketConnection.ipc.WriteableObject;

public class RelationshipData extends org.xtuml.masl.inspector.processInterface.RelationshipData
        implements ReadableObject, WriteableObject {

    public RelationshipData(final RelationshipMetaData meta) {
        super(meta);
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        leftId = new Integer(channel.readInt());
        rightId = new Integer(channel.readInt());
        if (meta.getAssocObject() != null) {
            assocId = new Integer(channel.readInt());
        }
    }

    @Override
    public void write(final CommunicationChannel channel) throws IOException {
        channel.writeData(leftId);
        channel.writeData(rightId);
        if (meta.getAssocObject() != null) {
            channel.writeData(assocId);
        }
    }

}
