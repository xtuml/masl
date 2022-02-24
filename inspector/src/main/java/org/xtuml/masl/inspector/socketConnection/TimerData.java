//
// Filename : InstanceData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;
import org.xtuml.masl.inspector.socketConnection.ipc.WriteableObject;

public class TimerData extends org.xtuml.masl.inspector.processInterface.TimerData
        implements ReadableObject, WriteableObject {

    public TimerData() {
        this.domain = null;
    }

    public TimerData(final DomainMetaData domain) {
        this.domain = domain;
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        id = channel.readInt();
        isScheduled = channel.readBoolean();
        if (isScheduled) {
            expiryTime = channel.readData(TimestampData.class);
            period = channel.readData(DurationData.class);
            if (period.getNanoseconds() == 0) {
                period = null;
            }

            final EventData event = new EventData(domain);
            event.read(channel);
            this.event = event;
        } else {
            expiryTime = null;
            period = null;
            event = null;
        }
    }

    @Override
    public void write(final CommunicationChannel channel) throws IOException {
    }

    @Override
    public org.xtuml.masl.inspector.processInterface.TimerData getValue() {
        return this;
    }

    @Override
    public void setValue(final org.xtuml.masl.inspector.processInterface.TimerData value) {
    }

    @Override
    public int compareTo(final org.xtuml.masl.inspector.processInterface.TimerData o) {
        if (expiryTime == null && o.getExpiryTime() == null) {
            return 0;
        }
        if (expiryTime == null) {
            return 1;
        } else if (o.getExpiryTime() == null) {
            return -1;
        } else {
            return (expiryTime.compareTo(o.getExpiryTime()));
        }
    }

    @Override
    protected TimestampData getExpiryObject() {
        return new TimestampData();
    }

    @Override
    protected DurationData getPeriodObject() {
        return new DurationData();
    }

    @Override
    protected EventData getEventObject() {
        return new EventData(domain);
    }

    protected DomainMetaData domain;

}
