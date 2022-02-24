// 
// Filename : CreateInstancePopulation.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.processInterface.InstanceData;
import org.xtuml.masl.inspector.socketConnection.ObjectMetaData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class CreateInstancePopulation extends CommandStub<VoidType> {

    private final ObjectMetaData meta;
    private final org.xtuml.masl.inspector.processInterface.InstanceData[] data;

    public CreateInstancePopulation(final ObjectMetaData meta,
            final org.xtuml.masl.inspector.processInterface.InstanceData[] data) {
        super(ServerCommandId.CREATE_INSTANCE_POPULATION);
        this.meta = meta;
        this.data = data;
    }

    @Override
    public VoidType execute(final CommunicationChannel channel) throws IOException {
        channel.writeData(meta.getDomain().getId());
        channel.writeData(meta.getArchId());
        channel.writeData(data.length);
        int inIdx = 0;
        for (final InstanceData element : data) {
            channel.writeData(element);
            while (inIdx < data.length && channel.available() > 0) {
                data[inIdx++].setPrimaryKey(new Integer(channel.readInt()));
            }
        }
        channel.flush();
        while (inIdx < data.length) {
            data[inIdx++].setPrimaryKey(new Integer(channel.readInt()));
        }

        return null;
    }
}
