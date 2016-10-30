// 
// Filename : GetInstanceCount.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.socketConnection.DomainServiceMetaData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;


public class InvokeDomainService extends CommandStub<VoidType>
{

  private final DomainServiceMetaData meta;
  private final DataValue<?>[]        parameters;


  public InvokeDomainService ( final DomainServiceMetaData meta, final DataValue<?>[] parameters )
  {
    super(ServerCommandId.RUN_DOMAIN_SERVICE);
    this.meta = meta;
    this.parameters = parameters;
  }

  @Override
  public VoidType execute ( final CommunicationChannel channel ) throws IOException
  {
    channel.writeData(meta.getDomain().getId());
    channel.writeData(meta.getArchId());
    for ( final DataValue<?> param : parameters )
    {
      channel.writeData(param);
    }
    channel.flush();
    return null;
  }
}
