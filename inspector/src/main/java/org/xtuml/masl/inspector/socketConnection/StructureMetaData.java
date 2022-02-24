//
// Filename : StructureMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;

public class StructureMetaData extends org.xtuml.masl.inspector.processInterface.StructureMetaData
        implements ReadableObject {

    private int archId;
    private String name;
    private AttributeMetaData[] attributes;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AttributeMetaData[] getAttributes() {
        return attributes;
    }

    int getArchId() {
        return archId;
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        archId = channel.readInt();
        name = channel.readString();

        attributes = channel.readData(AttributeMetaData[].class);
        for (final AttributeMetaData att : getAttributes()) {
            att.setStructure(this);
        }
    }

    private DomainMetaData domain = null;

    @Override
    public DomainMetaData getDomain() {
        return domain;
    }

    public void setDomain(final DomainMetaData domain) {
        this.domain = domain;
        for (final AttributeMetaData attribute : attributes) {
            attribute.setDomain(domain);
        }

    }

}
