//
// Filename : AssignerStateList.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.util.Arrays;

import org.xtuml.masl.inspector.processInterface.ObjectMetaData;
import org.xtuml.masl.inspector.processInterface.StateMetaData;

public class AssignerStateList extends ExecutableSourceList<StateMetaData, ObjectMetaData> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public AssignerStateList(final ObjectList objectList) {
        super(new DependentObjectListModel<StateMetaData, ObjectMetaData>(objectList) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected StateMetaData[] getDependentValues(final ObjectMetaData object) {
                final StateMetaData[] states = object.getAssignerStates();
                Arrays.sort(states);
                return states;
            }
        });
    }

}
