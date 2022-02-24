// 
// Filename : GetStack.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.StackFrame;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class GetStack extends CommandStub<StackFrame[]> {

    public GetStack() {
        super(ServerCommandId.GET_STACK);
    }

    @Override
    public StackFrame[] execute(final CommunicationChannel channel) throws IOException {
        channel.flush();
        final StackFrame[] stack = channel.readObjectArray(StackFrame[].class, StackFrame.class);

        for (int i = 0; i < stack.length; i++) {
            stack[i].setStackDepth(i);
        }
        return stack;
    }
}
