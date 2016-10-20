// 
// Filename : SetCatchConsole.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

public class SetCatchConsole extends ToggleCommand
{

  public SetCatchConsole ( final boolean enabled )
  {
    super(ServerCommandId.CATCH_CONSOLE, enabled);
  }
}
