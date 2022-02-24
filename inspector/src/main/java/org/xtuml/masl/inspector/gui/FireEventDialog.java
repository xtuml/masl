//
// Filename : FireEventDialog.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.rmi.RemoteException;

import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.processInterface.EventMetaData;
import org.xtuml.masl.inspector.processInterface.ObjectMetaData;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;

class FireEventDialog extends InvokeDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final EventMetaData event;
    private final ObjectMetaData object;
    private final Integer pk;

    public FireEventDialog(final EventMetaData event) {
        this(event, null, null);
    }

    public FireEventDialog(final EventMetaData event, final ObjectMetaData object, final Integer pk) {
        super(event.getFullyQualifiedName(), "Run", object, pk, event.getParameters());
        this.event = event;
        this.object = object;
        this.pk = pk;
        display();
    }

    @Override
    protected void invoke(final DataValue<?>[] parameters) {
        try {
            ProcessConnection.getConnection().fireEvent(event, object, pk, parameters);
        } catch (final RemoteException e) {
            e.printStackTrace();
        }
    }

}
