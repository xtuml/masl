// 
// Filename : DeleteSingleInstance.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ObjectMetaData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class DeleteSingleInstance extends CommandStub<VoidType> {

    private final ObjectMetaData meta;
    private final Object pk;

    public DeleteSingleInstance(final ObjectMetaData meta, final Object pk) {
        super(ServerCommandId.DELETE_SINGLE_INSTANCE);
        this.meta = meta;
        this.pk = pk;
    }

    @Override
    public VoidType execute(final CommunicationChannel channel) throws IOException {
        channel.writeData(meta.getDomain().getId());
        channel.writeData(meta.getArchId());
        channel.writeData(pk);
        channel.flush();
        return null;
    }
}
