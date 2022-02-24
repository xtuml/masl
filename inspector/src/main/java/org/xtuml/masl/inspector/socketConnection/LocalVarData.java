// 
// Filename : LocalVarData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;

public class LocalVarData extends org.xtuml.masl.inspector.processInterface.LocalVarData implements ReadableObject {

    private final String name;
    private final DataValue<?> value;

    public LocalVarData(final String name, final TypeMetaData type) {
        this.name = name;
        this.value = type.getDataObject();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DataValue<?> getValue() {
        return value;
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        ((ReadableObject) value).read(channel);
    }
}
