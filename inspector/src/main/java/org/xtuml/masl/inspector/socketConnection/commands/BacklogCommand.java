// 
// Filename : IdleCommand.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import org.xtuml.masl.inspector.socketConnection.ProcessConnection;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;


public class BacklogCommand extends CommandImpl
{

  public BacklogCommand ()
  {
  }

  @Override
  protected void execute ( final CommunicationChannel channel ) throws java.io.IOException
  {
    final long backlogMillis = channel.readLong();
    ProcessConnection.getConnection().setBacklog(backlogMillis);
  }
}
