// 
// Filename : LongData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;
import java.math.BigInteger;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;
import org.xtuml.masl.inspector.socketConnection.ipc.WriteableObject;

public class LongNaturalData extends org.xtuml.masl.inspector.processInterface.LongNaturalData
        implements ReadableObject, WriteableObject {

    // 0xFFFFFFFFFFFFFFFF
    private final static BigInteger mask = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        final BigInteger signedValue = BigInteger.valueOf(channel.readLong());

        setValue(signedValue.and(mask));
    }

    @Override
    public void write(final CommunicationChannel channel) throws IOException {
        channel.writeData(getValue());
    }

}
