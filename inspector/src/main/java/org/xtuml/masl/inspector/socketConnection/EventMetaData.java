// 
// Filename : EventMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;


public class EventMetaData extends org.xtuml.masl.inspector.processInterface.EventMetaData
    implements ReadableObject
{

  private int                 archId;
  private int                 parentObjectId;
  private String              name;
  private EventType           type;
  private ParameterMetaData[] parameters;

  public int getArchId ()
  {
    return archId;
  }

  private static final int ASSIGNER = 0;
  private static final int CREATION = 1;
  private static final int NORMAL   = 2;

  private ObjectMetaData   object   = null;

  @Override
  public ObjectMetaData getObject ()
  {
    return object;
  }

  public void setObject ( final ObjectMetaData object )
  {
    this.object = object;
  }

  @Override
  public ObjectMetaData getParentObject ()
  {
    return getObject().getDomain().getObject(parentObjectId);
  }

  public void read ( final CommunicationChannel channel ) throws IOException
  {
    archId = channel.readInt();
    parentObjectId = channel.readInt();
    final int typeId = channel.readInt();
    switch ( typeId )
    {
      case ASSIGNER:
        type = EventType.Assigner;
        break;
      case NORMAL:
        type = EventType.Normal;
        break;
      case CREATION:
        type = EventType.Creation;
        break;
    }

    name = channel.readString();
    parameters = channel.readData(ParameterMetaData[].class);
  }

  @Override
  public String getName ()
  {
    return name;
  }

  @Override
  public EventType getType ()
  {
    return type;
  }

  @Override
  public ParameterMetaData[] getParameters ()
  {
    return parameters;
  }

  public void setDomain ( final DomainMetaData domain )
  {
    for ( final ParameterMetaData parameter : parameters )
    {
      parameter.setDomain(domain);
    }
  }
}
