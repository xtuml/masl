// 
// Filename : GetProcessMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ProcessMetaData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;


public class GetProcessMetaData extends CommandStub<ProcessMetaData>
{

  public GetProcessMetaData ()
  {
    super(ServerCommandId.GET_PROCESS_DATA);
  }

  @Override
  public ProcessMetaData execute ( final CommunicationChannel channel ) throws IOException
  {
    channel.flush();
    return channel.readObject(ProcessMetaData.class);
  }

}
