//
// Filename : BreakpointListener.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector;

public interface BreakpointListener extends java.util.EventListener {

    public void breakpointChanged(BreakpointEvent e);
}
