//
// Filename : ObjectServiceList.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JMenuItem;

import org.xtuml.masl.inspector.processInterface.Capability;
import org.xtuml.masl.inspector.processInterface.TerminatorMetaData;
import org.xtuml.masl.inspector.processInterface.TerminatorServiceMetaData;

public class TerminatorServiceList extends ExecutableSourceList<TerminatorServiceMetaData, TerminatorMetaData> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public TerminatorServiceList(final TerminatorList terminatorList) {
        super(new DependentObjectListModel<TerminatorServiceMetaData, TerminatorMetaData>(terminatorList) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected TerminatorServiceMetaData[] getDependentValues(final TerminatorMetaData terminator) {
                final TerminatorServiceMetaData[] services = terminator.getServices();
                Arrays.sort(services);
                return services;
            }
        });
    }

    @Override
    protected void initPopup() {
        if (Capability.RUN_TERMINATOR_SERVICE.isAvailable()) {
            final JMenuItem runServiceItem = new JMenuItem("Run service...");
            runServiceItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent action) {
                    new InvokeTerminatorServiceDialog((TerminatorServiceMetaData) getSelectedValue());
                }
            });
            popup.add(runServiceItem);
        }

        super.initPopup();
    }

}
