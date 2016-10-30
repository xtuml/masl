// 
// Filename : IdleCommand.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import org.xtuml.masl.inspector.socketConnection.ProcessConnection;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;


public class IdleCommand extends CommandImpl
{

  public IdleCommand ()
  {
  }

  @Override
  protected void execute ( final CommunicationChannel channel ) throws java.io.IOException
  {
    ProcessConnection.getConnection().setIdle();
  }
}
