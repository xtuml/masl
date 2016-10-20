// 
// Filename : LongData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;
import org.xtuml.masl.inspector.socketConnection.ipc.WriteableObject;


public class LongData extends org.xtuml.masl.inspector.processInterface.LongData
    implements ReadableObject, WriteableObject
{

  public void read ( final CommunicationChannel channel ) throws IOException
  {
    setValue(new Long(channel.readLong()));
  }

  public void write ( final CommunicationChannel channel ) throws IOException
  {
    channel.writeData(getValue());
  }

}
