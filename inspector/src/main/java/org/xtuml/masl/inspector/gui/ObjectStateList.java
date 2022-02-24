// 
// Filename : ObjectStateList.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.util.Arrays;

import org.xtuml.masl.inspector.processInterface.ObjectMetaData;
import org.xtuml.masl.inspector.processInterface.StateMetaData;

public class ObjectStateList extends ExecutableSourceList<StateMetaData, ObjectMetaData> {

    public ObjectStateList(final ObjectList objectList) {
        super(new DependentObjectListModel<StateMetaData, ObjectMetaData>(objectList) {

            @Override
            protected StateMetaData[] getDependentValues(final ObjectMetaData object) {
                final StateMetaData[] states = object.getInstanceStates();
                Arrays.sort(states);
                return states;
            }
        });
    }

}
