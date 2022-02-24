//
// Filename : GetInstanceData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.processInterface.InstanceDataListener;
import org.xtuml.masl.inspector.socketConnection.ObjectMetaData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class GetInstanceData extends CommandStub<VoidType> {

    private final ObjectMetaData meta;
    private final InstanceReader reader;

    public GetInstanceData(final ObjectMetaData meta, final InstanceDataListener listener) {
        super(ServerCommandId.GET_INSTANCE_DATA);
        reader = new InstanceReader(meta, listener);
        this.meta = meta;
    }

    @Override
    public VoidType execute(final CommunicationChannel channel) throws IOException {
        channel.writeData(meta.getDomain().getId());
        channel.writeData(meta.getArchId());
        channel.flush();

        reader.readInstances(channel);

        return null;
    }
}
