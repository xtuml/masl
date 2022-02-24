//
// Filename : InstanceServiceList.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.util.Arrays;

import org.xtuml.masl.inspector.processInterface.ObjectMetaData;
import org.xtuml.masl.inspector.processInterface.ObjectServiceMetaData;

public class InstanceServiceList extends ExecutableSourceList<ObjectServiceMetaData, ObjectMetaData> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InstanceServiceList(final ObjectList objectList) {
        super(new DependentObjectListModel<ObjectServiceMetaData, ObjectMetaData>(objectList) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected ObjectServiceMetaData[] getDependentValues(final ObjectMetaData object) {
                final ObjectServiceMetaData[] services = object.getInstanceServices();
                Arrays.sort(services);
                return services;
            }
        });
    }

}
