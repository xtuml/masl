// 
// Filename : GetAssignerState.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ObjectMetaData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class GetAssignerState extends CommandStub<Integer> {

    private final ObjectMetaData meta;

    public GetAssignerState(final ObjectMetaData meta) {
        super(ServerCommandId.GET_ASSIGNER_STATE);
        this.meta = meta;
    }

    @Override
    public Integer execute(final CommunicationChannel channel) throws IOException {
        channel.writeData(meta.getDomain().getId());
        channel.writeData(meta.getArchId());
        channel.flush();
        return channel.readInt();
    }
}
