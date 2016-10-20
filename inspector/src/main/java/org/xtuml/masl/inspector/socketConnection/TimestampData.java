// 
// Filename : LongData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;
import java.text.ParseException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;
import org.xtuml.masl.inspector.socketConnection.ipc.WriteableObject;


public class TimestampData extends org.xtuml.masl.inspector.processInterface.TimestampData
    implements ReadableObject, WriteableObject
{

  public TimestampData ( final String formatted ) throws ParseException
  {
    super(formatted);
  }

  public TimestampData ()
  {
    super();
  }


  public void read ( final CommunicationChannel channel ) throws IOException
  {
    nanosSinceEpoch = channel.readLong();
  }

  public void write ( final CommunicationChannel channel ) throws IOException
  {
    channel.writeData(nanosSinceEpoch);
  }

}
