// 
// Filename : InvokeDomainServiceDialog.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.rmi.RemoteException;

import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;
import org.xtuml.masl.inspector.processInterface.TerminatorServiceMetaData;


class InvokeTerminatorServiceDialog extends InvokeDialog
{

  private final TerminatorServiceMetaData service;

  public InvokeTerminatorServiceDialog ( final TerminatorServiceMetaData service )
  {
    super(service.getFullyQualifiedName(), "Run", service.getParameters());
    this.service = service;
    display();
  }

  @Override
  protected void invoke ( final DataValue<?>[] parameters )
  {
    try
    {
      ProcessConnection.getConnection().invokeTerminatorService(service, parameters);
    }
    catch ( final RemoteException e )
    {
      e.printStackTrace();
    }
  }


}
