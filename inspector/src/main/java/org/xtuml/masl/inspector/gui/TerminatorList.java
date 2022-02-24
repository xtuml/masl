//
// Filename : ObjectList.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.Dimension;
import java.util.Arrays;

import javax.swing.ListSelectionModel;

import org.xtuml.masl.inspector.processInterface.DomainMetaData;
import org.xtuml.masl.inspector.processInterface.TerminatorMetaData;

public class TerminatorList extends DependentObjectList<TerminatorMetaData, DomainMetaData> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public TerminatorList(final DomainPicker domainPicker) {
        super(new DependentObjectListModel<TerminatorMetaData, DomainMetaData>(domainPicker) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected TerminatorMetaData[] getDependentValues(final DomainMetaData domain) {
                final TerminatorMetaData[] terms = domain.getTerminators();
                Arrays.sort(terms);
                return terms;
            }
        });
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setVisibleRowCount(10);

    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        final Dimension d = super.getPreferredScrollableViewportSize();

        if (getModel().getSize() == 0) {
            d.width = 0;
        }
        d.width = Math.max(d.width, 100);
        return d;
    }

}
