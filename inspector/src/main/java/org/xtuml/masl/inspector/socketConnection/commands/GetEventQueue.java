// 
// Filename : GetEventQueue.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.EventData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class GetEventQueue extends CommandStub<EventData[]> {

    public GetEventQueue() {
        super(ServerCommandId.GET_EVENT_QUEUE);
    }

    @Override
    public EventData[] execute(final CommunicationChannel channel) throws IOException {
        channel.flush();
        return channel.readObjectArray(EventData[].class, EventData.class);
    }
}
