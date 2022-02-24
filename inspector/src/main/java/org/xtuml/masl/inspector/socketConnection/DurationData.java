//
// Filename : LongData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;
import java.text.ParseException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;
import org.xtuml.masl.inspector.socketConnection.ipc.WriteableObject;

public class DurationData extends org.xtuml.masl.inspector.processInterface.DurationData
        implements ReadableObject, WriteableObject {

    public DurationData(final String formatted) throws ParseException {
        super(formatted);
    }

    public DurationData() {
        super();
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        setNanoseconds(channel.readLong());
    }

    @Override
    public void write(final CommunicationChannel channel) throws IOException {
        channel.writeData(getNanoseconds());
    }

}
