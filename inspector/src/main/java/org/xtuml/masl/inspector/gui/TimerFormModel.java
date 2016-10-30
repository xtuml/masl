// 
// Filename : ParameterBox.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import org.xtuml.masl.inspector.gui.form.AbstractFormModel;
import org.xtuml.masl.inspector.processInterface.EventData;
import org.xtuml.masl.inspector.processInterface.TimerData;
import org.xtuml.masl.inspector.processInterface.TimestampData;
import org.xtuml.masl.inspector.socketConnection.DurationData;


class TimerFormModel extends AbstractFormModel
{

  TimerFormModel ( final TimerData timer )
  {
    this.timer = timer;
  }

  @Override
  public Class<?> getFieldClass ( final int fieldIndex )
  {
    if ( !timer.isScheduled() )
    {
      return Void.class;
    }
    else if ( fieldIndex == 0 )
    {
      return TimestampData.class;
    }
    else if ( fieldIndex == 1 && timer.getPeriod() != null )
    {
      return DurationData.class;
    }
    else
    {
      return EventData.class;
    }
  }

  public int getFieldCount ()
  {
    return timer.isScheduled() ? (timer.getPeriod() != null ? 3 : 2) : 1;
  }

  @Override
  public String getFieldName ( final int fieldIndex )
  {
    if ( !timer.isScheduled() )
    {
      return timer.toString();
    }
    else if ( fieldIndex == 0 )
    {
      return "expiry time";
    }
    else if ( fieldIndex == 1 && timer.getPeriod() != null )
    {
      return "repeat period";
    }
    else
    {
      return "event";
    }
  }

  public Object getValueAt ( final int fieldIndex )
  {
    if ( !timer.isScheduled() )
    {
      return null;
    }
    else if ( fieldIndex == 0 )
    {
      return timer.getExpiryTime();
    }
    else if ( fieldIndex == 1 && timer.getPeriod() != null )
    {
      return timer.getPeriod();
    }
    else
    {
      return timer.getEventData();
    }
  }

  @Override
  public boolean isValueEditable ( final int fieldIndex )
  {
    return false;
  }

  @Override
  public void setValueAt ( final Object aValue, final int fieldIndex )
  {
  }


  private final TimerData timer;


}
