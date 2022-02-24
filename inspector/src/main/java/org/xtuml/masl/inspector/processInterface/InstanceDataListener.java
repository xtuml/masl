//
// Filename : InstanceDataListener.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public interface InstanceDataListener {

    public void setInstanceCount(int count);

    public boolean addInstanceData(InstanceData instance);

    public void finished();
}
