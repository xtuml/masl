// 
// Filename : DomainServiceMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;


public class DomainServiceMetaData extends org.xtuml.masl.inspector.processInterface.DomainServiceMetaData
    implements ReadableObject
{

  private int                     archId;
  private ParameterMetaData[]     parameters;
  private LocalVariableMetaData[] localVars;

  @Override
  public ParameterMetaData[] getParameters ()
  {
    return parameters;
  }

  @Override
  public LocalVariableMetaData[] getLocalVariables ()
  {
    return localVars;
  }

  public int getArchId ()
  {
    return archId;
  }

  private TypeMetaData returnType;

  @Override
  public TypeMetaData getReturnType ()
  {
    return returnType;
  }

  final static int SCENARIO = 0;
  final static int EXTERNAL = 1;
  final static int DOMAIN   = 2;

  public void read ( final CommunicationChannel channel ) throws IOException
  {
    archId = channel.readInt();
    final int typeId = channel.readInt();

    switch ( typeId )
    {
      case SCENARIO:
        type = ServiceType.Scenario;
        break;
      case EXTERNAL:
        type = ServiceType.External;
        break;
      case DOMAIN:
        type = ServiceType.Domain;
        break;
    }
    name = channel.readString();
    parameters = channel.readData(ParameterMetaData[].class);
    localVars = channel.readData(LocalVariableMetaData[].class);

    isFunction = channel.readBoolean();
    if ( isFunction )
    {
      returnTypeName = channel.readString();
      returnType = channel.readData(TypeMetaData.class);
    }

    fileName = channel.readString();
    fileHash = channel.readString();

  }

  private DomainMetaData domain = null;

  @Override
  public DomainMetaData getDomain ()
  {
    return domain;
  }

  public void setDomain ( final DomainMetaData domain )
  {
    this.domain = domain;
    if ( returnType != null )
    {
      returnType.setDomain(domain);
    }
    for ( final ParameterMetaData parameter : parameters )
    {
      parameter.setDomain(domain);
    }
    for ( final LocalVariableMetaData localVar : localVars )
    {
      localVar.setDomain(domain);
    }
  }

  @Override
  public void initSourceFileFilter ()
  {
    sourceFileFilter = new SourceFileFilter(
                                            getName() + " Domain Services", fileName);
  }

  @Override
  public SourcePosition getSourcePosition ( final int lineNo )
  {
    return new SourcePosition(this, lineNo);
  }

}
