//
// Filename : SourcePosition.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public abstract class SourcePosition implements Comparable<SourcePosition> {

    public final static int NO_LINE = -1;
    public final static int FIRST_LINE = 1;
    public final static int LAST_LINE = Integer.MAX_VALUE;

    public SourcePosition() {
    }

    public abstract int getLineNo();

    public abstract ExecutableSource getSource();

    @Override
    public boolean equals(final Object obj) {
        return (getSource() == ((SourcePosition) obj).getSource() && getLineNo() == ((SourcePosition) obj).getLineNo());
    }

    @Override
    public int compareTo(final SourcePosition bp) {
        if (getSource().getFullyQualifiedName().equals(bp.getSource().getFullyQualifiedName())) {
            return (getLineNo() < bp.getLineNo() ? -1 : (getLineNo() == bp.getLineNo() ? 0 : 1));
        } else {
            return getSource().getFullyQualifiedName().compareTo(bp.getSource().getFullyQualifiedName());
        }
    }

    @Override
    public int hashCode() {
        return (getSource() == null ? 0 : getSource().hashCode()) ^ getLineNo();
    }

}
