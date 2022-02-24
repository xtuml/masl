//
// Filename : DomainPicker.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import javax.swing.JComboBox;

import org.xtuml.masl.inspector.processInterface.DomainMetaData;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;

public class DomainPicker extends JComboBox {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public DomainPicker() {
        super();
        try {
            final java.util.Set<String> ignoredDomains = org.xtuml.masl.inspector.Preferences.getIgnoredDomains();

            final DomainMetaData[] domains = ProcessConnection.getConnection().getMetaData().getDomains();
            java.util.Arrays.sort(domains);

            for (DomainMetaData domain : domains) {
                if (!domain.isInterface() && !ignoredDomains.contains(domain.getName())) {
                    addItem(domain);
                }
            }
        } catch (final java.rmi.RemoteException e) {
            e.printStackTrace();
        }

        setRequestFocusEnabled(false);
    }

}
