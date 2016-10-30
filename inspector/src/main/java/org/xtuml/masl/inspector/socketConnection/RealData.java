// 
// Filename : DoubleData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;
import org.xtuml.masl.inspector.socketConnection.ipc.WriteableObject;


public class RealData extends org.xtuml.masl.inspector.processInterface.RealData
    implements ReadableObject, WriteableObject
{

  public void read ( final CommunicationChannel channel ) throws IOException
  {
    setValue(new Double(channel.readDouble()));
  }

  public void write ( final CommunicationChannel channel ) throws IOException
  {
    channel.writeData(getValue());
  }

}
