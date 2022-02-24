// 
// Filename : EnumerateMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;

public class EnumerateMetaData extends org.xtuml.masl.inspector.processInterface.EnumerateMetaData
        implements ReadableObject {

    private int archId;

    int getArchId() {
        return archId;
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        archId = channel.readInt();
        name = channel.readString();

        final int size = channel.readInt();

        for (int i = 0; i < size; i++) {
            final Integer value = new Integer(channel.readInt());
            final String name = channel.readString();

            addEnum(value, name);
        }
    }

    @Override
    public EnumerateData getEnumerate(final int index) {
        return new EnumerateData(index, this);
    }

    private DomainMetaData domain = null;

    @Override
    public DomainMetaData getDomain() {
        return domain;
    }

    public void setDomain(final DomainMetaData domain) {
        this.domain = domain;
    }

}
