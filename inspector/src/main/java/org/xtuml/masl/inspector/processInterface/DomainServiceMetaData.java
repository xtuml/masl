// 
// Filename : DomainServiceMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.io.File;

public abstract class DomainServiceMetaData extends ServiceMetaData {

    public abstract DomainMetaData getDomain();

    @Override
    public String getFullyQualifiedName() {
        return getDomain().getName() + "::" + getName();
    }

    @Override
    public File getDirectory() {
        return getDomain().getDirectory();
    }

}
