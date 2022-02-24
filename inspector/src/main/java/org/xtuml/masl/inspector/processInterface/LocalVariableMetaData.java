// 
// Filename : ParameterMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public abstract class LocalVariableMetaData {

    public abstract String getName();

    public abstract String getTypeName();

    public abstract TypeMetaData getType();

    @Override
    public String toString() {
        return getName() + " : " + getTypeName();
    }

}
