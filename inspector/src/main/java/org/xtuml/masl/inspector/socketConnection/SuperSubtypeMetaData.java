//
// Filename : SuperSubtypeMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;
import java.util.ArrayList;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;

public class SuperSubtypeMetaData extends org.xtuml.masl.inspector.processInterface.SuperSubtypeMetaData
        implements ReadableObject {

    private int archId;

    public int getArchId() {
        return archId;
    }

    private DomainMetaData domain = null;

    @Override
    public DomainMetaData getDomain() {
        return domain;
    }

    public void setDomain(final DomainMetaData domain) {
        this.domain = domain;
    }

    private String number;
    private int supertype;
    private int[] subtypeIds;
    private ObjectMetaData[] subtypes = null;

    @Override
    public String getNumber() {
        return number;
    }

    @Override
    public ObjectMetaData getSupertype() {
        return getDomain().getObject(supertype);
    }

    @Override
    public ObjectMetaData[] getSubtypes() {
        if (subtypes == null) {
            final ArrayList<ObjectMetaData> subtypeList = new ArrayList<ObjectMetaData>(subtypeIds.length);
            for (final int subtype : subtypeIds) {
                subtypeList.add(getDomain().getObject(subtype));
            }
            subtypes = subtypeList.toArray(new ObjectMetaData[subtypeList.size()]);
        }
        return subtypes;
    }

    @Override
    public org.xtuml.masl.inspector.processInterface.SuperSubtypeData getSuperSubtypeData() {
        return new SuperSubtypeData(this);
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        archId = channel.readInt();
        number = channel.readString();
        supertype = channel.readInt();
        subtypeIds = channel.readIntArray();

    }
}
