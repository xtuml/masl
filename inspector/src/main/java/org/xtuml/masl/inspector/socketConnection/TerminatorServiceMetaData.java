//
// Filename : ObjectServiceMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;

public class TerminatorServiceMetaData extends org.xtuml.masl.inspector.processInterface.TerminatorServiceMetaData
        implements ReadableObject {

    @Override
    public TypeMetaData getReturnType() {
        return returnType;
    }

    private TypeMetaData returnType;
    private TerminatorMetaData terminator = null;
    private ParameterMetaData[] parameters;
    private LocalVariableMetaData[] localVars;

    @Override
    public ParameterMetaData[] getParameters() {
        return parameters;
    }

    @Override
    public LocalVariableMetaData[] getLocalVariables() {
        return localVars;
    }

    @Override
    public TerminatorMetaData getTerminator() {
        return terminator;
    }

    public void setTerminator(final TerminatorMetaData terminator) {
        this.terminator = terminator;
    }

    protected int archId;

    public int getArchId() {
        return archId;
    }

    private boolean override;

    @Override
    protected boolean isOverride() {
        return override;
    }

    private static final int TERMINATOR = 3;

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        archId = channel.readInt();
        override = channel.readInt() != TERMINATOR;

        type = ServiceType.Terminator;

        name = channel.readString();
        parameters = channel.readData(ParameterMetaData[].class);
        localVars = channel.readData(LocalVariableMetaData[].class);

        isFunction = channel.readBoolean();
        if (isFunction) {
            returnTypeName = channel.readString();
            returnType = channel.readData(TypeMetaData.class);
        }
        fileName = channel.readString();
        fileHash = channel.readString();

    }

    @Override
    public void initSourceFileFilter() {
        sourceFileFilter = new SourceFileFilter(getName() + " Terminator Service", fileName);
    }

    @Override
    public SourcePosition getSourcePosition(final int lineNo) {
        return new SourcePosition(this, lineNo);
    }

    public void setDomain(final DomainMetaData domain) {
        if (returnType != null) {
            returnType.setDomain(domain);
        }
        for (final ParameterMetaData parameter : parameters) {
            parameter.setDomain(domain);
        }
        for (final LocalVariableMetaData localVar : localVars) {
            localVar.setDomain(domain);
        }
    }

}
