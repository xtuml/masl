// 
// Filename : DomainServiceList.java
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

public class DomainServiceList extends ExecutableSourceList<DomainServiceMetaData, DomainMetaData> {

    public DomainServiceList(final DomainPicker domainPicker) {
        super(new DependentObjectListModel<DomainServiceMetaData, DomainMetaData>(domainPicker) {

            @Override
            protected DomainServiceMetaData[] getDependentValues(final DomainMetaData domain) {
                final DomainServiceMetaData[] services = domain.getDomainServices();
                Arrays.sort(services);
                return services;
            }
        });

    }

    @Override
    protected void initPopup() {
        if (Capability.RUN_DOMAIN_SERVICE.isAvailable()) {
            final JMenuItem runServiceItem = new JMenuItem("Run service...");
            runServiceItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent action) {
                    new InvokeDomainServiceDialog((DomainServiceMetaData) getSelectedValue());
                }
            });
            popup.add(runServiceItem);
        }
        super.initPopup();
    }

}
