//
// File: Plugin.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.inspector.processInterface;

public interface Plugin {

    interface Flag {

        String getName();

        boolean isReadable();

        boolean isWriteable();
    }

    interface Property {

        String getName();

        boolean isReadable();

        boolean isWriteable();
    }

    String getName();

    Flag[] getFlags();

    Property[] getProperties();

    String[] getActions();
}
