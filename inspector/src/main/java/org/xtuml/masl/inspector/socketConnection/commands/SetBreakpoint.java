// 
// Filename : SetBreakpoint.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.processInterface.SourcePosition;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;


public class SetBreakpoint extends CommandStub<VoidType>
{

  private final SourcePosition position;
  private final boolean        set;

  public SetBreakpoint ( final SourcePosition position, final boolean set )
  {
    super(ServerCommandId.SET_BREAKPOINT);
    this.position = position;
    this.set = set;
  }

  @Override
  public VoidType execute ( final CommunicationChannel channel ) throws IOException
  {
    channel.writeData(position);
    channel.writeData(set);
    return null;
  }

}
