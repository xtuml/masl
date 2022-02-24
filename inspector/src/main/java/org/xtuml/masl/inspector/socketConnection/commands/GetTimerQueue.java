// 
// Filename : GetEventQueue.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.TimerData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class GetTimerQueue extends CommandStub<TimerData[]> {

    public GetTimerQueue() {
        super(ServerCommandId.GET_TIMER_QUEUE);
    }

    @Override
    public TimerData[] execute(final CommunicationChannel channel) throws IOException {
        channel.flush();
        return channel.readObjectArray(TimerData[].class, TimerData.class);
    }
}
