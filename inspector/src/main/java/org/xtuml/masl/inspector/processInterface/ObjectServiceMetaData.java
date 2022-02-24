//
// Filename : ObjectServiceMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.io.File;

public abstract class ObjectServiceMetaData extends ServiceMetaData {

    public abstract ObjectMetaData getObject();

    @Override
    public String getFullyQualifiedName() {
        return getObject().getFullyQualifiedName() + "." + getName();
    }

    @Override
    public File getDirectory() {
        return getObject().getDomain().getDirectory();
    }

}
