// 
// Filename : GetTraceIPC.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;


public class InvokePluginAction extends CommandStub<VoidType>
{

  private final String pluginName;
  private final String actionName;

  public InvokePluginAction ( final String pluginName, final String actionName )
  {
    super(ServerCommandId.INVOKE_PLUGIN_ACTION);
    this.pluginName = pluginName;
    this.actionName = actionName;
  }

  @Override
  public VoidType execute ( final CommunicationChannel channel ) throws IOException
  {
    channel.writeData(pluginName);
    channel.writeData(actionName);
    channel.flush();
    return null;
  }

}
