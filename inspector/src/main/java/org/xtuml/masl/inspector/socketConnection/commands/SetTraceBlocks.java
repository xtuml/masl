// 
// Filename : SetTraceBlocks.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

public class SetTraceBlocks extends ToggleCommand
{

  public SetTraceBlocks ( final boolean enabled )
  {
    super(ServerCommandId.TRACE_BLOCKS, enabled);
  }
}
