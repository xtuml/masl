// 
// Filename : ObjectServiceMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;

public class ObjectServiceMetaData extends org.xtuml.masl.inspector.processInterface.ObjectServiceMetaData
        implements ReadableObject {

    @Override
    public TypeMetaData getReturnType() {
        return returnType;
    }

    private TypeMetaData returnType;
    private ObjectMetaData object = null;
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
    public ObjectMetaData getObject() {
        return object;
    }

    public void setObject(final ObjectMetaData object) {
        this.object = object;
    }

    protected int archId;

    public int getArchId() {
        return archId;
    }

    private final static int OBJECT = 4;
    private final static int INSTANCE = 5;

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        archId = channel.readInt();
        final int typeId = channel.readInt();

        switch (typeId) {
        case OBJECT:
            type = ServiceType.Object;
            break;
        case INSTANCE:
            type = ServiceType.Instance;
            break;
        }

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
        sourceFileFilter = new SourceFileFilter(getName() + " Object Services", fileName);
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
