// 
// Filename : GetSelectedInstanceData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.processInterface.InstanceDataListener;
import org.xtuml.masl.inspector.socketConnection.ObjectMetaData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class GetSelectedInstanceData extends CommandStub<VoidType> {

    private final ObjectMetaData meta;
    private final Object[] pks;
    private final InstanceReader reader;

    public GetSelectedInstanceData(final ObjectMetaData meta, final Object[] pks, final InstanceDataListener listener) {
        super(ServerCommandId.GET_SELECTED_INSTANCE_DATA);
        reader = new InstanceReader(meta, listener);
        this.meta = meta;
        this.pks = pks;
    }

    @Override
    public VoidType execute(final CommunicationChannel channel) throws IOException {
        channel.writeData(meta.getDomain().getId());
        channel.writeData(meta.getArchId());
        channel.writeData(pks);
        channel.flush();

        reader.readInstances(channel);

        return null;
    }
}
