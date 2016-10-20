// 
// Filename : InvokeObjectServiceDialog.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.rmi.RemoteException;

import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.processInterface.ObjectServiceMetaData;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;


class InvokeObjectServiceDialog extends InvokeDialog
{

  private final ObjectServiceMetaData service;
  private final Integer               pk;

  public InvokeObjectServiceDialog ( final ObjectServiceMetaData service )
  {
    this(service, null);
  }

  public InvokeObjectServiceDialog ( final ObjectServiceMetaData service, final Integer pk )
  {
    super(service.getFullyQualifiedName(), "Run", service.getObject(), pk, service.getParameters());
    this.service = service;
    this.pk = pk;
    display();
  }

  @Override
  protected void invoke ( final DataValue<?>[] parameters )
  {
    try
    {
      ProcessConnection.getConnection().invokeObjectService(service, pk, parameters);
    }
    catch ( final RemoteException e )
    {
      e.printStackTrace();
    }
  }

}
