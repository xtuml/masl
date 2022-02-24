//
// Filename : WriteableObject.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.ipc;

public interface WriteableObject {

    public void write(CommunicationChannel channel) throws java.io.IOException;
}
