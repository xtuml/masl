//
// Filename : InstanceData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;
import org.xtuml.masl.inspector.socketConnection.ipc.WriteableObject;


public class InstanceData extends org.xtuml.masl.inspector.processInterface.InstanceData
implements ReadableObject, WriteableObject
{

  public InstanceData ( final ObjectMetaData meta )
  {
    super(meta);
    attributes = new DataValue[meta.getAttributes().length];
    for ( int i = 0; i < attributes.length; ++i )
    {
      attributes[i] = meta.getAttributes()[i].getDefaultValue();
    }

  }

  public boolean isValid ()
  {
    return primaryKey != null;
  }

  public void read ( final CommunicationChannel channel ) throws IOException
  {
    final boolean valid = channel.readBoolean();
    if ( valid )
    {
      primaryKey = channel.readInt();
      for ( int i = 0; i < getMetaData().getAttributes().length; i++ )
      {
        if ( getMetaData().getAttributes()[i].isIdentifier() ||
            !getMetaData().getAttributes()[i].isReferential() ||
            channel.readBoolean() )
        {
          attributes[i] = getMetaData().getAttributes()[i].getType().getDataObject();
          ((ReadableObject)attributes[i]).read(channel);
        }
        else
        {
          attributes[i] = null;
        }
      }
      if ( getMetaData().isActive() )
      {
        currentState = channel.readInt();
      }
      else
      {
        currentState = null;
      }
      for ( int i = 0; i < getMetaData().getRelationships().length; i++ )
      {
        if ( getMetaData().getRelationships()[i].isMultiple() )
        {
          relCounts[i] = channel.readInt();
          relatedIds[i] = null;
        }
        else
        {
          if ( channel.readBoolean() )
          {
            relatedIds[i] = channel.readInt();
            relCounts[i] = 1;
          }
          else
          {
            relatedIds[i] = null;
            relCounts[i] = 0;
          }
        }
      }
    }
  }

  @Override
  public ObjectMetaData getMetaData ()
  {
    return (ObjectMetaData)meta;
  }

  public void write ( final CommunicationChannel channel ) throws IOException
  {
    for ( int i = 0; i < meta.getAttributes().length; i++ )
    {
      if ( !meta.getAttributes()[i].isReadOnly() )
      {
        channel.writeData(attributes[i]);
      }
    }
    if ( getMetaData().isActive() )
    {
      channel.writeData(currentState);
    }

  }

}
