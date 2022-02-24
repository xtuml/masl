//
// Filename : GetInstanceCount.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.socketConnection.InstanceIdData;
import org.xtuml.masl.inspector.socketConnection.ObjectServiceMetaData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class InvokeObjectService extends CommandStub<VoidType> {

    private final ObjectServiceMetaData meta;
    private final DataValue<?>[] parameters;
    private final Integer pk;

    public InvokeObjectService(final ObjectServiceMetaData meta, final Integer pk, final DataValue<?>[] parameters) {
        super(ServerCommandId.RUN_OBJECT_SERVICE);
        this.meta = meta;
        this.parameters = parameters;
        this.pk = pk;
    }

    @Override
    public VoidType execute(final CommunicationChannel channel) throws IOException {
        channel.writeData(meta.getObject().getDomain().getId());
        channel.writeData(meta.getObject().getArchId());
        channel.writeData(meta.getArchId());
        if (pk != null) {
            channel.writeData(new InstanceIdData(pk, meta.getObject()));
        }
        for (final DataValue<?> param : parameters) {
            channel.writeData(param);
        }
        channel.flush();
        return null;

    }
}
