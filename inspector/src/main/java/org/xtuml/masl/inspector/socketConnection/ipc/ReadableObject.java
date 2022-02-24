//
// Filename : ReadableObject.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.ipc;

public interface ReadableObject {

    public void read(CommunicationChannel channel) throws java.io.IOException;
}
