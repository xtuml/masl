// 
// Filename : CommandServer.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;


public class CommandServer extends Thread
{

  private final CommunicationChannel                                         channel;

  private final java.util.Map<ClientCommandId, Class<? extends CommandImpl>> commands = new java.util.Hashtable<ClientCommandId, Class<? extends CommandImpl>>(); // NB

  // Hashtable
  // is
  // synchronised

  public void addCommand ( final Class<? extends CommandImpl> impl, final ClientCommandId commandId )
  {
    if ( CommandImpl.class.isAssignableFrom(impl) )
    {
      commands.put(commandId, impl);
    }
    else
    {
      throw new IllegalArgumentException("Command must be of class CommandImpl");
    }
  }

  private void processCommand ( final int commandId, final CommunicationChannel channel ) throws java.io.IOException
  {
    try
    {
      final Class<? extends CommandImpl> impl = commands.get(ClientCommandId.values()[commandId]);
      if ( impl != null )
      {
        final CommandImpl command = impl.newInstance();
        command.perform(channel);
      }
      else
      {
        System.err.println("Command not found: " + commandId + " (" + Integer.toHexString(commandId) + ")");
      }
    }
    catch ( final ClassCastException e )
    {
      e.printStackTrace();
    }
    catch ( final IllegalAccessException e )
    {
      e.printStackTrace();
    }
    catch ( final InstantiationException e )
    {
      e.printStackTrace();
    }
  }

  public CommandServer ( final CommunicationChannel channel )
  {
    super("Command Server");
    this.channel = channel;
    setDaemon(true);
  }

  @Override
  public void run ()
  {
    try
    {
      int commandId = channel.readInt();
      while ( !isInterrupted() )
      {
        processCommand(commandId, channel);
        commandId = channel.readInt();
      }
    }
    catch ( final java.io.EOFException e )
    {
    }
    catch ( final java.io.IOException e )
    {
    }
  }

}
