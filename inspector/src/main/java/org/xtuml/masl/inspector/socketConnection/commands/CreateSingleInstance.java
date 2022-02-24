//
// Filename : CreateSingleInstance.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.InstanceData;
import org.xtuml.masl.inspector.socketConnection.ObjectMetaData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class CreateSingleInstance extends CommandStub<VoidType> {

    private final ObjectMetaData meta;
    private final InstanceData data;

    public CreateSingleInstance(final ObjectMetaData meta, final InstanceData data) {
        super(ServerCommandId.CREATE_SINGLE_INSTANCE);
        this.meta = meta;
        this.data = data;
    }

    @Override
    public VoidType execute(final CommunicationChannel channel) throws IOException {
        channel.writeData(meta.getDomain().getId());
        channel.writeData(meta.getArchId());
        channel.writeData(data);
        channel.flush();
        data.setPrimaryKey(Integer.valueOf(channel.readInt()));
        return null;
    }
}
