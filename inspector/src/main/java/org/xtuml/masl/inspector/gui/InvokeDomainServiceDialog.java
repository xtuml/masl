// 
// Filename : InvokeDomainServiceDialog.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.rmi.RemoteException;

import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.processInterface.DomainServiceMetaData;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;

class InvokeDomainServiceDialog extends InvokeDialog {

    private final DomainServiceMetaData service;

    public InvokeDomainServiceDialog(final DomainServiceMetaData service) {
        super(service.getFullyQualifiedName(), "Run", service.getParameters());
        this.service = service;
        display();
    }

    @Override
    protected void invoke(final DataValue<?>[] parameters) {
        try {
            ProcessConnection.getConnection().invokeDomainService(service, parameters);
        } catch (final RemoteException e) {
            e.printStackTrace();
        }
    }

}
