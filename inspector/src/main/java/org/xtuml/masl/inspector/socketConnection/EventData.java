//
// Filename : EventData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;

public class EventData extends org.xtuml.masl.inspector.processInterface.EventData implements ReadableObject {

    public EventData() {
    }

    public EventData(final DomainMetaData domain) {
        this.domain = domain;
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        final int domainId = channel.readInt();
        final int objectId = channel.readInt();

        final ProcessMetaData process = ProcessConnection.getConnection().getMetaData();
        final DomainMetaData domain = process.getDomain(domainId);
        this.domain = domain;
        destObject = domain.getObject(objectId);

        final int eventId = channel.readInt();
        event = domain.getObject(objectId).getEvent(eventId);

        destInstanceId = new InstanceIdData(destObject);
        ((ReadableObject) destInstanceId).read(channel);

        final boolean hasSource = channel.readBoolean();

        if (hasSource) {
            final int sourceObjectId = channel.readInt();
            sourceObject = process.getDomain(domainId).getObject(sourceObjectId);

            sourceInstanceId = new InstanceIdData(sourceObject);
            ((ReadableObject) sourceInstanceId).read(channel);
        } else {
            sourceObject = null;
            sourceInstanceId = null;
        }

        parameters = new org.xtuml.masl.inspector.processInterface.DataValue[event.getParameters().length];

        for (int i = 0; i < event.getParameters().length; i++) {
            parameters[i] = event.getParameters()[i].getType().getDataObject();
            ((ReadableObject) parameters[i]).read(channel);
        }

    }

    @Override
    protected org.xtuml.masl.inspector.processInterface.InstanceIdData createInstanceId() {
        return new InstanceIdData(domain);
    }

}
