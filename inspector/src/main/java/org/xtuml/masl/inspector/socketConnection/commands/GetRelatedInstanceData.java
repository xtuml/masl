// 
// Filename : GetRelatedInstanceData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.processInterface.InstanceDataListener;
import org.xtuml.masl.inspector.socketConnection.ObjectMetaData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class GetRelatedInstanceData extends CommandStub<VoidType> {

    private final ObjectMetaData sourceMeta;
    private final Object pk;
    private final int relId;
    private final InstanceReader reader;

    public GetRelatedInstanceData(final ObjectMetaData meta, final Object pk, final int relId,
            final InstanceDataListener listener) {
        super(ServerCommandId.GET_RELATED_INSTANCE_DATA);
        reader = new InstanceReader(meta.getRelationships()[relId].getDestObject(), listener);
        this.sourceMeta = meta;
        this.pk = pk;
        this.relId = relId;
    }

    @Override
    public VoidType execute(final CommunicationChannel channel) throws IOException {
        channel.writeData(sourceMeta.getDomain().getId());
        channel.writeData(sourceMeta.getArchId());
        channel.writeData(pk);
        channel.writeData(relId);
        channel.flush();

        reader.readInstances(channel);
        return null;
    }
}
