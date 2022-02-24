//
// Filename : StackFrame.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public abstract class StackFrame {

    public abstract SourcePosition getPosition();

    public abstract LocalVarData[] getLocalVars();
}
