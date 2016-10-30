// 
// Filename : GetInstanceCount.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.TimerData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;


public class CancelTimer extends CommandStub<VoidType>
{

  private final TimerData timer;

  public CancelTimer ( final TimerData timer )
  {
    super(ServerCommandId.CANCEL_TIMER);
    this.timer = timer;
  }

  @Override
  public VoidType execute ( final CommunicationChannel channel ) throws IOException
  {
    channel.writeData(timer.getId());
    channel.flush();

    return null;
  }
}
