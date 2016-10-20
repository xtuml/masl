// 
// Filename : StateMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.File;
import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;


public class StateMetaData extends org.xtuml.masl.inspector.processInterface.StateMetaData
    implements ReadableObject
{

  private ObjectMetaData object = null;

  @Override
  public ObjectMetaData getObject ()
  {
    return object;
  }

  public void setObject ( final ObjectMetaData object )
  {
    this.object = object;
  }

  private int                     id;
  private StateType               type;
  private String                  name;
  private ParameterMetaData[]     parameters;
  private LocalVariableMetaData[] localVars;

  @Override
  public int getId ()
  {
    return id;
  }

  @Override
  public String getName ()
  {
    return name;
  }

  @Override
  public StateType getType ()
  {
    return type;
  }

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

  @Override
  public File getDirectory ()
  {
    return getObject().getDomain().getDirectory();
  }

  private static final int ASSIGNER = 0;
  private static final int START    = 1;
  private static final int NORMAL   = 2;
  private static final int CREATION = 3;
  private static final int TERMINAL = 4;

  public void read ( final CommunicationChannel channel ) throws IOException
  {
    id = channel.readInt();
    final int typeId = channel.readInt();
    switch ( typeId )
    {
      case ASSIGNER:
        type = StateType.Assigner;
        break;
      case START:
        type = StateType.Start;
        break;
      case NORMAL:
        type = StateType.Normal;
        break;
      case CREATION:
        type = StateType.Creation;
        break;
      case TERMINAL:
        type = StateType.Terminal;
        break;
    }
    name = channel.readString();
    parameters = channel.readData(ParameterMetaData[].class);
    localVars = channel.readData(LocalVariableMetaData[].class);
    fileName = channel.readString();
    fileHash = channel.readString();


  }

  @Override
  public void initSourceFileFilter ()
  {
    sourceFileFilter = new SourceFileFilter(getName() + " States", fileName);
  }

  @Override
  public SourcePosition getSourcePosition ( final int lineNo )
  {
    return new SourcePosition(this, lineNo);
  }

  public void setDomain ( final DomainMetaData domain )
  {
    for ( final ParameterMetaData parameter : parameters )
    {
      parameter.setDomain(domain);
    }
    for ( final LocalVariableMetaData localVar : localVars )
    {
      localVar.setDomain(domain);
    }
  }

}
