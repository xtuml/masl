// 
// Filename : CreateRelationships.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.processInterface.RelationshipData;
import org.xtuml.masl.inspector.socketConnection.RelationshipMetaData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;


public class CreateRelationships extends CommandStub<VoidType>
{

  private final RelationshipMetaData                                        meta;
  private final org.xtuml.masl.inspector.processInterface.RelationshipData[] data;

  public CreateRelationships ( final RelationshipMetaData meta,
                               final org.xtuml.masl.inspector.processInterface.RelationshipData[] data )
  {
    super(ServerCommandId.CREATE_RELATIONSHIPS);
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
    for ( final RelationshipData element : data )
    {
      channel.writeData(element);
    }
    channel.flush();
    return null;
  }
}
