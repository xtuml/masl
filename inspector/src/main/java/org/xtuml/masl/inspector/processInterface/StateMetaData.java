// 
// Filename : StateMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.io.File;

public abstract class StateMetaData extends ExecutableSource implements Comparable<StateMetaData> {

    public enum StateType {
        Assigner, Start, Normal, Creation, Terminal
    }

    public abstract int getId();

    public abstract String getName();

    public abstract StateType getType();

    @Override
    public File getDirectory() {
        return getObject().getDomain().getDirectory();
    }

    public abstract ObjectMetaData getObject();

    @Override
    public String getFullyQualifiedName() {
        return getObject().getDomain().getName() + "::" + getObject().getName() + "." + toString();
    }

    @Override
    public String toString() {
        final StringBuffer res = new StringBuffer(getName());

        if (getParameters() != null) {
            res.append(" ( ");
            for (int i = 0; i < getParameters().length; i++) {
                if (i != 0) {
                    res.append(", ");
                }
                res.append(getParameters()[i].toString());
            }
            res.append(" )");
        }
        return res.toString();
    }

    @Override
    public int compareTo(final StateMetaData lhs) {
        // Compare names
        int res = getName().compareTo(lhs.getName());
        if (res != 0) {
            return res;
        }

        // Names are the same so compare parameter names
        for (int i = 0; i < getParameters().length; i++) {
            // Check for less parameters on lhs, in which case we are bigger
            if (i >= lhs.getParameters().length) {
                return 1;
            }

            // Compare the next parameter position
            res = getParameters()[i].getName().compareTo(lhs.getParameters()[i].getName());
            if (res != 0) {
                return res;
            }
        }

        // Check for more parameters on lhs, in which case we are smaller
        if (getParameters().length < lhs.getParameters().length) {
            return -1;
        } else {
            return 0;
        }
    }

}
