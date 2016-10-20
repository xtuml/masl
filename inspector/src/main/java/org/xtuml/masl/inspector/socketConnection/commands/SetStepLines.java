// 
// Filename : SetStepLines.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

public class SetStepLines extends ToggleCommand
{

  public SetStepLines ( final boolean enabled )
  {
    super(ServerCommandId.STEP_LINES, enabled);
  }
}
