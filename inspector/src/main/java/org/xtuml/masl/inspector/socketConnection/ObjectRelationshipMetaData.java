//
// Filename : ObjectRelationshipMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;

public class ObjectRelationshipMetaData extends org.xtuml.masl.inspector.processInterface.ObjectRelationshipMetaData
        implements ReadableObject {

    @Override
    public ObjectMetaData getDestObject() {
        return getObject().getDomain().getObject(destObjectId);
    }

    @Override
    public String getNumber() {
        return number;
    }

    public ObjectMetaData getObject() {
        return object;
    }

    @Override
    public String getRolePhrase() {
        return rolePhrase;
    }

    @Override
    public boolean isConditional() {
        return conditional;
    }

    @Override
    public boolean isMultiple() {
        return multiple;
    }

    @Override
    public boolean isSuperSubtype() {
        return supersub;
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        number = channel.readString();
        destObjectId = channel.readInt();
        conditional = channel.readBoolean();
        supersub = channel.readBoolean();
        if (supersub) {
            multiple = false;
            rolePhrase = null;
        } else {
            rolePhrase = channel.readString();
            multiple = channel.readBoolean();
        }
    }

    public void setObject(final ObjectMetaData object) {
        this.object = object;
    }

    protected String rolePhrase;

    protected int destObjectId;

    protected String number;

    protected boolean multiple;

    protected boolean conditional;

    protected boolean supersub;

    private ObjectMetaData object = null;
}
