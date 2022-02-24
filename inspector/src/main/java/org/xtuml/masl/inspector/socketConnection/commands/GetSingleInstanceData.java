// 
// Filename : GetSingleInstanceData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.InstanceData;
import org.xtuml.masl.inspector.socketConnection.ObjectMetaData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class GetSingleInstanceData extends CommandStub<InstanceData> {

    private final ObjectMetaData meta;
    private final Object pk;

    public GetSingleInstanceData(final ObjectMetaData meta, final Object pk) {
        super(ServerCommandId.GET_SINGLE_INSTANCE_DATA);
        this.meta = meta;
        this.pk = pk;
    }

    @Override
    public InstanceData execute(final CommunicationChannel channel) throws IOException {
        channel.writeData(meta.getDomain().getId());
        channel.writeData(meta.getArchId());
        channel.writeData(pk);
        channel.flush();
        final InstanceData instance = new InstanceData(meta);
        instance.read(channel);
        return instance.isValid() ? instance : null;
    }
}
