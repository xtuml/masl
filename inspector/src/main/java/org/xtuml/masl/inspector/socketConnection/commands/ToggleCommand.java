// 
// Filename : ToggleCommand.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;


public class ToggleCommand extends CommandStub<VoidType>
{

  private final boolean toggle;

  public ToggleCommand ( final ServerCommandId commandId, final boolean toggle )
  {
    super(commandId);
    this.toggle = toggle;
  }

  @Override
  public VoidType execute ( final CommunicationChannel channel ) throws IOException
  {
    channel.writeData(toggle);
    return null;
  }

}
