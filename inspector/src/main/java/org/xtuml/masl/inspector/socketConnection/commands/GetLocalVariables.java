// 
// Filename : GetLocalVariables.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.inspector.processInterface.ServiceMetaData.ServiceType;
import org.xtuml.masl.inspector.processInterface.StateMetaData.StateType;
import org.xtuml.masl.inspector.socketConnection.LocalVarData;
import org.xtuml.masl.inspector.socketConnection.LocalVariableMetaData;
import org.xtuml.masl.inspector.socketConnection.ObjectServiceMetaData;
import org.xtuml.masl.inspector.socketConnection.ParameterMetaData;
import org.xtuml.masl.inspector.socketConnection.SourcePosition;
import org.xtuml.masl.inspector.socketConnection.StateMetaData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class GetLocalVariables extends CommandStub<LocalVarData[]> {

    private final int stackDepth;
    private final SourcePosition position;

    public GetLocalVariables(final int stackDepth, final SourcePosition position) {
        super(ServerCommandId.GET_LOCAL_VARIABLES);
        this.stackDepth = stackDepth;
        this.position = position;
    }

    @Override
    public LocalVarData[] execute(final CommunicationChannel channel) throws IOException {
        channel.writeData(stackDepth);
        channel.flush();

        final List<LocalVarData> result = new ArrayList<LocalVarData>();

        if ((position.getSource() instanceof ObjectServiceMetaData
                && ((ObjectServiceMetaData) position.getSource()).getType() == ServiceType.Instance)) {
            final LocalVarData var = new LocalVarData("this",
                    ((ObjectServiceMetaData) position.getSource()).getObject().getInstanceType());
            var.read(channel);
            result.add(var);
        } else if ((position.getSource() instanceof StateMetaData
                && (((StateMetaData) position.getSource()).getType() == StateType.Normal
                        || ((StateMetaData) position.getSource()).getType() == StateType.Terminal))) {
            final LocalVarData var = new LocalVarData("this",
                    ((StateMetaData) position.getSource()).getObject().getInstanceType());
            var.read(channel);
            result.add(var);
        }

        for (final ParameterMetaData param : (ParameterMetaData[]) position.getSource().getParameters()) {
            final LocalVarData var = new LocalVarData(param.getName(), param.getType());
            var.read(channel);
            result.add(var);
        }

        final int noVars = channel.readInt();

        for (int i = 0; i < noVars; ++i) {
            final int id = channel.readInt();
            final LocalVariableMetaData meta = (LocalVariableMetaData) position.getSource().getLocalVariables()[id];
            final LocalVarData var = new LocalVarData(meta.getName(), meta.getType());
            var.read(channel);
            result.add(var);
        }

        return result.toArray(new LocalVarData[0]);
    }
}
