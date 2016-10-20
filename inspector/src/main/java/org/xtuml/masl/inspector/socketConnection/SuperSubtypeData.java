// 
// Filename : SuperSubtypeData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;
import org.xtuml.masl.inspector.socketConnection.ipc.WriteableObject;


public class SuperSubtypeData extends org.xtuml.masl.inspector.processInterface.SuperSubtypeData
    implements ReadableObject, WriteableObject
{

  public SuperSubtypeData ( final SuperSubtypeMetaData meta )
  {
    super(meta);
  }

  public void read ( final CommunicationChannel channel ) throws IOException
  {
    supertypeId = new Integer(channel.readInt());
    subtypeIndex = channel.readInt();
    subtypeId = new Integer(channel.readInt());
  }

  public void write ( final CommunicationChannel channel ) throws IOException
  {
    channel.writeData(supertypeId);
    channel.writeData(subtypeIndex);
    channel.writeData(subtypeId);
  }

}
