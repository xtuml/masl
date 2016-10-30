// 
// Filename : CreateSuperSubtypes.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.processInterface.SuperSubtypeData;
import org.xtuml.masl.inspector.socketConnection.SuperSubtypeMetaData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;


public class CreateSuperSubtypes extends CommandStub<VoidType>
{

  private final SuperSubtypeMetaData                                        meta;
  private final org.xtuml.masl.inspector.processInterface.SuperSubtypeData[] data;

  public CreateSuperSubtypes ( final SuperSubtypeMetaData meta,
                               final org.xtuml.masl.inspector.processInterface.SuperSubtypeData[] data )
  {
    super(ServerCommandId.CREATE_SUPERSUBTYPES);
    this.meta = meta;
    this.data = data;
  }

  @Override
  public VoidType execute ( final CommunicationChannel channel ) throws IOException
  {
    channel.writeData(meta.getDomain().getId());
    channel.writeData(meta.getArchId());
    channel.writeData(data.length);
    channel.flush();
    for ( final SuperSubtypeData element : data )
    {
      channel.writeData(element);
    }
    channel.flush();
    return null;
  }
}
