//
// Filename : CollectionData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;
import java.util.Map;

import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;
import org.xtuml.masl.inspector.socketConnection.ipc.WriteableObject;

public class DictionaryData extends org.xtuml.masl.inspector.processInterface.DictionaryData
        implements ReadableObject, WriteableObject {

    public DictionaryData(final org.xtuml.masl.inspector.processInterface.TypeMetaData keyType,
            final org.xtuml.masl.inspector.processInterface.TypeMetaData valueType) {
        super(keyType, valueType);
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        final int size = channel.readInt();

        data.clear();

        for (int i = 0; i < size; ++i) {
            final DataValue<?> key = keyType.getDataObject();
            final DataValue<?> value = valueType.getDataObject();
            ((ReadableObject) key).read(channel);
            ((ReadableObject) value).read(channel);
            data.put(key, value);
        }
    }

    @Override
    public void write(final CommunicationChannel channel) throws IOException {
        channel.writeData(data.size());

        for (final Map.Entry<DataValue<?>, DataValue<?>> entry : data.entrySet()) {
            channel.writeData(entry.getKey());
            channel.writeData(entry.getValue());
        }
    }
}
