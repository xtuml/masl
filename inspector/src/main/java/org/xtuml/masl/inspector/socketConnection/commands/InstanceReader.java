//
// File: InstanceReader.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.processInterface.InstanceDataListener;
import org.xtuml.masl.inspector.socketConnection.InstanceData;
import org.xtuml.masl.inspector.socketConnection.ObjectMetaData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;


public class InstanceReader
{

  public InstanceReader ( final ObjectMetaData meta, final InstanceDataListener listener )
  {
    this.meta = meta;
    this.listener = listener;
  }

  private final ObjectMetaData       meta;
  private final InstanceDataListener listener;

  protected void readInstances ( final CommunicationChannel channel ) throws IOException
  {
    listener.setInstanceCount(channel.readInt());

    while ( channel.readBoolean() )
    {
      final InstanceData instance = new InstanceData(meta);
      instance.read(channel);
      if ( listener.addInstanceData(instance.isValid() ? instance : null) )
      {
        // Request load cancel
        channel.writeData(true);
        channel.flush();
      }
    }
    // Revoke the cancel request (if any)
    channel.writeData(false);
    channel.flush();
    listener.finished();

  }

}
