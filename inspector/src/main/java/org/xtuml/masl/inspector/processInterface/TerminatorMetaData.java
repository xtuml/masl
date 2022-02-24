// 
// Filename : ObjectMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public abstract class TerminatorMetaData implements Comparable<TerminatorMetaData> {

    @Override
    public int compareTo(final TerminatorMetaData rhs) {
        final int res = getDomain().compareTo(rhs.getDomain());
        if (res != 0) {
            return res;
        }

        return name.compareTo(rhs.name);
    }

    public abstract DomainMetaData getDomain();

    public String getKeyLetters() {
        return keyLetters;
    }

    public String getName() {
        return name;
    }

    public abstract TerminatorServiceMetaData[] getServices();

    @Override
    public String toString() {
        return name;
    }

    protected String name;
    protected String keyLetters;

    public String getFullyQualifiedName() {
        return getDomain().getName() + "::" + getName();
    }

}
