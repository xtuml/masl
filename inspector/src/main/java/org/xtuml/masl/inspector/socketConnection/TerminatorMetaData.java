// 
// Filename : ObjectMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;


public class TerminatorMetaData extends org.xtuml.masl.inspector.processInterface.TerminatorMetaData
    implements ReadableObject
{

  public int getArchId ()
  {
    return archId;
  }

  @Override
  public DomainMetaData getDomain ()
  {
    return domain;
  }

  public TerminatorServiceMetaData getService ( final int number )
  {
    return serviceLookup.get(number);
  }

  @Override
  public TerminatorServiceMetaData[] getServices ()
  {
    return services;
  }

  public void read ( final CommunicationChannel channel ) throws IOException
  {
    archId = channel.readInt();
    name = channel.readString();
    keyLetters = channel.readString();

    services = channel.readData(TerminatorServiceMetaData[].class);

    for ( final TerminatorServiceMetaData service : services )
    {
      service.setTerminator(this);
      serviceLookup.put(service.getArchId(), service);
    }

  }

  public void setDomain ( final DomainMetaData domain )
  {
    this.domain = domain;
    for ( final TerminatorServiceMetaData service : services )
    {
      service.setDomain(domain);
    }
  }

  private DomainMetaData                                domain        = null;

  private final Map<Integer, TerminatorServiceMetaData> serviceLookup = new HashMap<Integer, TerminatorServiceMetaData>();

  private int                                           archId;

  private TerminatorServiceMetaData[]                   services;

}
