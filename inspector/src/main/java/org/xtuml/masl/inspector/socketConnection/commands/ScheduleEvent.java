// 
// Filename : GetInstanceCount.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.socketConnection.DurationData;
import org.xtuml.masl.inspector.socketConnection.EventMetaData;
import org.xtuml.masl.inspector.socketConnection.ObjectMetaData;
import org.xtuml.masl.inspector.socketConnection.TimerData;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;


public class ScheduleEvent extends CommandStub<VoidType>
{

  private final TimerData timer;

  public ScheduleEvent ( final TimerData timer )
  {
    super(ServerCommandId.SCHEDULE_EVENT);
    this.timer = timer;
  }

  @Override
  public VoidType execute ( final CommunicationChannel channel ) throws IOException
  {
    channel.writeData(timer.getId());
    channel.writeData(timer.getExpiryTime());
    if ( timer.getPeriod() == null )
    {
      channel.writeData(new DurationData());
    }
    else
    {
      channel.writeData(timer.getPeriod());
    }

    final EventMetaData meta = (EventMetaData)timer.getEventData().getEvent();
    channel.writeData(meta.getObject().getDomain().getId());
    channel.writeData(meta.getObject().getArchId());
    channel.writeData(meta.getArchId());

    if ( timer.getEventData().getSourceInstanceId() != null )
    {
      channel.writeData(true);
      channel.writeData(((ObjectMetaData)timer.getEventData().getSourceObject()).getArchId());
      channel.writeData(timer.getEventData().getSourceInstanceId().getId());
    }
    else
    {
      channel.writeData(false);
    }

    if ( timer.getEventData().getDestInstanceId() != null )
    {
      channel.writeData(timer.getEventData().getDestInstanceId());
    }
    for ( final DataValue<?> param : timer.getEventData().getParameters() )
    {
      channel.writeData(param);
    }
    channel.flush();

    return null;
  }
}
