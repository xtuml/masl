//
// Filename : DomainExternalList.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JMenuItem;

import org.xtuml.masl.inspector.processInterface.Capability;
import org.xtuml.masl.inspector.processInterface.DomainMetaData;
import org.xtuml.masl.inspector.processInterface.DomainServiceMetaData;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;

public class DomainExternalList extends ExecutableSourceList<DomainServiceMetaData, DomainMetaData> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public DomainExternalList(final DomainPicker domainPicker) {
        super(new DependentObjectListModel<DomainServiceMetaData, DomainMetaData>(domainPicker) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected DomainServiceMetaData[] getDependentValues(final DomainMetaData domain) {
                final DomainServiceMetaData[] externals = domain.getExternals();
                Arrays.sort(externals);
                return externals;
            }
        });
    }

    @Override
    protected void initPopup() {
        if (Capability.RUN_EXTERNAL.isAvailable()) {
            final JMenuItem runExternalItem = new JMenuItem("Run External...");
            runExternalItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent action) {
                    try {
                        ProcessConnection.getConnection().runExternal((DomainServiceMetaData) getSelectedValue());
                    } catch (final java.rmi.RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });

            popup.add(runExternalItem);
        }

        super.initPopup();
    }

}
