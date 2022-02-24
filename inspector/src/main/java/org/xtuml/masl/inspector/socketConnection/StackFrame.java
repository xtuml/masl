// 
// Filename : StackFrame.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;

public class StackFrame extends org.xtuml.masl.inspector.processInterface.StackFrame implements ReadableObject {

    @Override
    public org.xtuml.masl.inspector.processInterface.LocalVarData[] getLocalVars() {
        if (localVars == null) {
            try {
                localVars = ProcessConnection.getConnection().getLocalVariables(stackDepth, position);
            } catch (final java.rmi.RemoteException e) {
                localVars = new LocalVarData[0];
            }
        }
        return localVars;
    }

    @Override
    public SourcePosition getPosition() {
        return position;
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        position = channel.readData(SourcePosition.class);
        localVars = null;
    }

    public void setStackDepth(final int depth) {
        stackDepth = depth;
    }

    protected int stackDepth;
    private SourcePosition position;

    private LocalVarData[] localVars;

}
