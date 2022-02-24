//
// File: ParameterMetaData.java
//
// UK Crown Copyright (c) 2007. All Rights Reserved.
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;

public class ParameterMetaData extends org.xtuml.masl.inspector.processInterface.ParameterMetaData
        implements ReadableObject {

    private String name;
    private String typeName;
    private TypeMetaData type;
    private boolean out;

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        name = channel.readString();
        typeName = channel.readString();
        type = channel.readData(TypeMetaData.class);
        out = channel.readBoolean();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TypeMetaData getType() {
        return type;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public boolean isOut() {
        return out;
    }

    public void setDomain(final DomainMetaData domain) {
        type.setDomain(domain);
    }
}
