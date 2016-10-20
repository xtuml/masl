// 
// Filename : GetInstanceCount.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.socketConnection.TerminatorServiceMetaData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;


public class InvokeTerminatorService extends CommandStub<VoidType>
{

  private final TerminatorServiceMetaData meta;
  private final DataValue<?>[]            parameters;

  public InvokeTerminatorService ( final TerminatorServiceMetaData meta, final DataValue<?>[] parameters )
  {
    super(ServerCommandId.RUN_TERMINATOR_SERVICE);
    this.meta = meta;
    this.parameters = parameters;
  }

  @Override
  public VoidType execute ( final CommunicationChannel channel ) throws IOException
  {
    channel.writeData(meta.getTerminator().getDomain().getId());
    channel.writeData(meta.getTerminator().getArchId());
    channel.writeData(meta.getArchId());
    for ( final DataValue<?> param : parameters )
    {
      channel.writeData(param);
    }
    channel.flush();
    return null;

  }
}
