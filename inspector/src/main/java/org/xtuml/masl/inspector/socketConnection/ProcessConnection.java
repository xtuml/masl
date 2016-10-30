// 
// Filename : ProcessConnection.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.RemoteException;

import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.socketConnection.commands.BacklogCommand;
import org.xtuml.masl.inspector.socketConnection.commands.CancelTimer;
import org.xtuml.masl.inspector.socketConnection.commands.ClientCommandId;
import org.xtuml.masl.inspector.socketConnection.commands.CommandServer;
import org.xtuml.masl.inspector.socketConnection.commands.ContinueExecution;
import org.xtuml.masl.inspector.socketConnection.commands.CreateInstancePopulation;
import org.xtuml.masl.inspector.socketConnection.commands.CreateRelationships;
import org.xtuml.masl.inspector.socketConnection.commands.CreateSingleInstance;
import org.xtuml.masl.inspector.socketConnection.commands.CreateSuperSubtypes;
import org.xtuml.masl.inspector.socketConnection.commands.CurrentPositionCommand;
import org.xtuml.masl.inspector.socketConnection.commands.DeleteSingleInstance;
import org.xtuml.masl.inspector.socketConnection.commands.FireEvent;
import org.xtuml.masl.inspector.socketConnection.commands.GetAssignerState;
import org.xtuml.masl.inspector.socketConnection.commands.GetEventQueue;
import org.xtuml.masl.inspector.socketConnection.commands.GetInstanceCount;
import org.xtuml.masl.inspector.socketConnection.commands.GetInstanceData;
import org.xtuml.masl.inspector.socketConnection.commands.GetLocalVariables;
import org.xtuml.masl.inspector.socketConnection.commands.GetPluginFlag;
import org.xtuml.masl.inspector.socketConnection.commands.GetPluginProperty;
import org.xtuml.masl.inspector.socketConnection.commands.GetProcessMetaData;
import org.xtuml.masl.inspector.socketConnection.commands.GetRelatedInstanceData;
import org.xtuml.masl.inspector.socketConnection.commands.GetSelectedInstanceData;
import org.xtuml.masl.inspector.socketConnection.commands.GetSingleInstanceData;
import org.xtuml.masl.inspector.socketConnection.commands.GetStack;
import org.xtuml.masl.inspector.socketConnection.commands.GetTimerQueue;
import org.xtuml.masl.inspector.socketConnection.commands.IdleCommand;
import org.xtuml.masl.inspector.socketConnection.commands.InvokeDomainService;
import org.xtuml.masl.inspector.socketConnection.commands.InvokeObjectService;
import org.xtuml.masl.inspector.socketConnection.commands.InvokePluginAction;
import org.xtuml.masl.inspector.socketConnection.commands.InvokeTerminatorService;
import org.xtuml.masl.inspector.socketConnection.commands.PauseExecution;
import org.xtuml.masl.inspector.socketConnection.commands.RunTestSchedule;
import org.xtuml.masl.inspector.socketConnection.commands.RunningCommand;
import org.xtuml.masl.inspector.socketConnection.commands.ScheduleEvent;
import org.xtuml.masl.inspector.socketConnection.commands.SetBreakpoint;
import org.xtuml.masl.inspector.socketConnection.commands.SetCatchConsole;
import org.xtuml.masl.inspector.socketConnection.commands.SetEnableTimers;
import org.xtuml.masl.inspector.socketConnection.commands.SetPluginFlag;
import org.xtuml.masl.inspector.socketConnection.commands.SetPluginProperty;
import org.xtuml.masl.inspector.socketConnection.commands.SetStepBlocks;
import org.xtuml.masl.inspector.socketConnection.commands.SetStepEvents;
import org.xtuml.masl.inspector.socketConnection.commands.SetStepExceptions;
import org.xtuml.masl.inspector.socketConnection.commands.SetStepLines;
import org.xtuml.masl.inspector.socketConnection.commands.SetTraceBlocks;
import org.xtuml.masl.inspector.socketConnection.commands.SetTraceEvents;
import org.xtuml.masl.inspector.socketConnection.commands.SetTraceExceptions;
import org.xtuml.masl.inspector.socketConnection.commands.SetTraceLines;
import org.xtuml.masl.inspector.socketConnection.commands.StepExecution;
import org.xtuml.masl.inspector.socketConnection.commands.UpdateSingleInstance;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;


public class ProcessConnection extends org.xtuml.masl.inspector.processInterface.ProcessConnection
{

  public static ProcessConnection getConnection ()
  {
    return (ProcessConnection)org.xtuml.masl.inspector.processInterface.ProcessConnection.getConnection();
  }

  public ProcessConnection ()
  {
    host = System.getProperty("host", "localhost");
    port = Integer.parseInt(System.getProperty("port", "0"));

    System.out.print("Connecting to " + host + ":" + port + "...");

    try
    {
      while ( processConnectionSocketChannel == null )
      {
        try
        {
          processConnectionSocketChannel = java.nio.channels.SocketChannel
                                                                          .open(new InetSocketAddress(host, port + MAIN_PORT_OFFSET));
        }
        catch ( final ConnectException e )
        {
          System.out.print(".");
          try
          {
            Thread.sleep(1000);
          }
          catch ( final InterruptedException ie )
          {
          }
        }
      }

      processConnectionChannel = new CommunicationChannel(processConnectionSocketChannel);

      while ( processInfoSocket == null )
      {
        try
        {
          processInfoSocket = new Socket(host, port + INFO_PORT_OFFSET);
        }
        catch ( final ConnectException e )
        {
          try
          {
            Thread.sleep(10);
          }
          catch ( final InterruptedException ie )
          {
          }
        }
      }

      processInfoSocket.setTcpNoDelay(true);

      processInfoChannel = new CommunicationChannel(processInfoSocket);

      commandServer = new CommandServer(processInfoChannel);
      commandServer.addCommand(CurrentPositionCommand.class, ClientCommandId.CURRENT_POSITION_INFO);
      commandServer.addCommand(RunningCommand.class, ClientCommandId.RUNNING_INFO);
      commandServer.addCommand(IdleCommand.class, ClientCommandId.IDLE_INFO);
      commandServer.addCommand(BacklogCommand.class, ClientCommandId.BACKLOG_INFO);
      commandServer.start();

      Runtime.getRuntime().addShutdownHook(new Thread("Process Connection Shutdown")
      {

        @Override
        public void run ()
        {
          try
          {
            closeConnection();
          }
          catch ( final Exception e )
          {
            e.printStackTrace();
          }
        }
      });

      System.out.println("Connected");
      connected = true;

      consoleRedirector = new org.xtuml.masl.inspector.ConsoleRedirect(new InetSocketAddress(host, port + CONSOLE_PORT_OFFSET));
      consoleRedirector.start();

      processMetaData = new GetProcessMetaData().perform(processConnectionChannel);

      setTraceLines(org.xtuml.masl.inspector.Preferences.getTraceLines());
      setTraceBlocks(org.xtuml.masl.inspector.Preferences.getTraceBlocks());
      setTraceEvents(org.xtuml.masl.inspector.Preferences.getTraceEvents());
      setTraceExceptions(org.xtuml.masl.inspector.Preferences.getTraceExceptions());
      setStepLines(org.xtuml.masl.inspector.Preferences.getStepLines());
      setStepBlocks(org.xtuml.masl.inspector.Preferences.getStepBlocks());
      setStepExceptions(org.xtuml.masl.inspector.Preferences.getStepExceptions());
      setStepEvents(org.xtuml.masl.inspector.Preferences.getStepEvents());
      setEnableTimers(org.xtuml.masl.inspector.Preferences.getEnableTimers());
      setCatchConsole(org.xtuml.masl.inspector.Preferences.getCatchConsole());

    }
    catch ( final Exception e )
    {
      e.printStackTrace();
      processMetaData = new ProcessMetaData()
      {

        @Override
        public DomainMetaData getDomain ( final String name )
        {
          return null;
        }

        @Override
        public DomainMetaData[] getDomains ()
        {
          return new DomainMetaData[0];
        }

      };
    }
  }

  @Override
  public synchronized void closeConnection () throws java.rmi.RemoteException
  {
    if ( connected )
    {
      System.out.println("Closing connection to process");
      try
      {
        connected = false;
        commandServer.interrupt();
        processInfoSocket.close();
        processConnectionSocketChannel.close();
        consoleRedirector.interrupt();
      }
      catch ( final Exception e )
      {
      }
    }
  }

  @Override
  public void continueExecution () throws java.rmi.RemoteException
  {
    try
    {
      new ContinueExecution().perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void createInstancePopulation ( final org.xtuml.masl.inspector.processInterface.ObjectMetaData object,
                                         final org.xtuml.masl.inspector.processInterface.InstanceData[] data ) throws java.rmi.RemoteException
  {
    try
    {
      new CreateInstancePopulation((ObjectMetaData)object, data).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void createRelationships ( final org.xtuml.masl.inspector.processInterface.RelationshipMetaData rel,
                                    final org.xtuml.masl.inspector.processInterface.RelationshipData[] data ) throws java.rmi.RemoteException
  {
    try
    {
      new CreateRelationships((RelationshipMetaData)rel, data).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void createSingleInstance ( final org.xtuml.masl.inspector.processInterface.ObjectMetaData object,
                                     final org.xtuml.masl.inspector.processInterface.InstanceData data ) throws java.rmi.RemoteException
  {
    try
    {
      new CreateSingleInstance((ObjectMetaData)object, (InstanceData)data).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void createSuperSubtypes ( final org.xtuml.masl.inspector.processInterface.SuperSubtypeMetaData ss,
                                    final org.xtuml.masl.inspector.processInterface.SuperSubtypeData[] data ) throws java.rmi.RemoteException
  {
    try
    {
      new CreateSuperSubtypes((SuperSubtypeMetaData)ss, data).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void fireEvent ( final org.xtuml.masl.inspector.processInterface.EventMetaData event,
                          final org.xtuml.masl.inspector.processInterface.ObjectMetaData object,
                          final Integer pk,
                          final DataValue<?>[] parameters ) throws RemoteException
  {
    try
    {
      new FireEvent((EventMetaData)event, (ObjectMetaData)object, pk, parameters).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void scheduleEvent ( final org.xtuml.masl.inspector.processInterface.TimerData timer ) throws RemoteException
  {
    try
    {
      new ScheduleEvent((TimerData)timer).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void cancelTimer ( final org.xtuml.masl.inspector.processInterface.TimerData timer ) throws RemoteException
  {
    try
    {
      new CancelTimer((TimerData)timer).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public int getAssignerState ( final org.xtuml.masl.inspector.processInterface.ObjectMetaData object ) throws java.rmi.RemoteException
  {
    try
    {
      return new GetAssignerState((ObjectMetaData)object).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
      return -1;
    }
  }

  @Override
  public boolean getCatchConsole () throws java.rmi.RemoteException
  {
    return catchConsole;
  }

  @Override
  public String getConnectionTitle ()
  {
    return processMetaData.getName() + " on " + host + ":" + port;
  }

  @Override
  public boolean getEnableTimers () throws java.rmi.RemoteException
  {
    return enableTimers;
  }

  @Override
  public org.xtuml.masl.inspector.processInterface.EventData[] getEventQueue () throws java.rmi.RemoteException
  {
    try
    {
      return new GetEventQueue().perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
      return new EventData[0];
    }
  }

  @Override
  public org.xtuml.masl.inspector.processInterface.TimerData[] getTimerQueue () throws java.rmi.RemoteException
  {
    try
    {
      return new GetTimerQueue().perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
      return new TimerData[0];
    }
  }

  @Override
  public int getInstanceCount ( final org.xtuml.masl.inspector.processInterface.ObjectMetaData object ) throws java.rmi.RemoteException
  {
    try
    {
      return new GetInstanceCount((ObjectMetaData)object).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
      return 0;
    }
  }

  @Override
  public void getInstanceData ( final org.xtuml.masl.inspector.processInterface.ObjectMetaData object,
                                final org.xtuml.masl.inspector.processInterface.InstanceDataListener listener ) throws java.rmi.RemoteException
  {
    try
    {
      new GetInstanceData((ObjectMetaData)object, listener).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
      listener.setInstanceCount(0);
    }
  }

  @Override
  public org.xtuml.masl.inspector.processInterface.InstanceData getInstanceData ( final org.xtuml.masl.inspector.processInterface.ObjectMetaData object,
                                                                                 final Object pk ) throws java.rmi.RemoteException
  {
    try
    {
      return new GetSingleInstanceData((ObjectMetaData)object, pk).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
      return null;
    }
  }

  @Override
  public void getInstanceData ( final org.xtuml.masl.inspector.processInterface.ObjectMetaData object,
                                final Object[] pks,
                                final org.xtuml.masl.inspector.processInterface.InstanceDataListener listener ) throws java.rmi.RemoteException
  {
    try
    {
      new GetSelectedInstanceData((ObjectMetaData)object, pks, listener).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
      listener.setInstanceCount(0);
    }
  }

  public LocalVarData[] getLocalVariables ( final int stackDepth, final SourcePosition position ) throws java.rmi.RemoteException
  {
    try
    {
      return new GetLocalVariables(stackDepth, position).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
      return new LocalVarData[0];
    }
  }

  @Override
  public ProcessMetaData getMetaData ()
  {
    return processMetaData;
  }

  @Override
  public void getRelatedInstanceData ( final org.xtuml.masl.inspector.processInterface.ObjectMetaData object,
                                       final Object pk,
                                       final int relId,
                                       final org.xtuml.masl.inspector.processInterface.InstanceDataListener listener ) throws java.rmi.RemoteException
  {
    try
    {
      new GetRelatedInstanceData((ObjectMetaData)object, pk, relId, listener).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
      listener.setInstanceCount(0);
    }
  }

  @Override
  public org.xtuml.masl.inspector.processInterface.StackFrame[] getStack () throws java.rmi.RemoteException
  {
    try
    {
      return new GetStack().perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
      return new StackFrame[0];
    }
  }

  @Override
  public boolean getStepBlocks () throws java.rmi.RemoteException
  {
    return stepBlocks;
  }

  @Override
  public boolean getStepEvents () throws java.rmi.RemoteException
  {
    return stepEvents;
  }

  @Override
  public boolean getStepExceptions () throws java.rmi.RemoteException
  {
    return stepExceptions;
  }

  @Override
  public boolean getStepLines () throws java.rmi.RemoteException
  {
    return stepLines;
  }

  @Override
  public boolean getTraceBlocks () throws java.rmi.RemoteException
  {
    return traceBlocks;
  }

  @Override
  public boolean getTraceEvents () throws java.rmi.RemoteException
  {
    return traceEvents;
  }

  @Override
  public boolean getTraceExceptions () throws java.rmi.RemoteException
  {
    return traceExceptions;
  }

  @Override
  public boolean getPluginFlag ( final String pluginName, final String flagName ) throws java.rmi.RemoteException
  {
    try
    {
      return new GetPluginFlag(pluginName, flagName).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
      return false;
    }
  }

  @Override
  public String getPluginProperty ( final String pluginName, final String propertyName ) throws java.rmi.RemoteException
  {
    try
    {
      return new GetPluginProperty(pluginName, propertyName).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
      return "";
    }
  }

  @Override
  public boolean getTraceLines () throws java.rmi.RemoteException
  {
    return traceLines;
  }

  @Override
  public void invokeDomainService ( final org.xtuml.masl.inspector.processInterface.DomainServiceMetaData service,
                                    final DataValue<?>[] parameters ) throws RemoteException
  {
    try
    {
      new InvokeDomainService((DomainServiceMetaData)service, parameters).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void invokeTerminatorService ( final org.xtuml.masl.inspector.processInterface.TerminatorServiceMetaData service,
                                        final DataValue<?>[] parameters ) throws RemoteException
  {
    try
    {
      new InvokeTerminatorService((TerminatorServiceMetaData)service, parameters).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void runExternal ( final org.xtuml.masl.inspector.processInterface.DomainServiceMetaData service ) throws RemoteException
  {
    try
    {
      new InvokeDomainService((DomainServiceMetaData)service, new DataValue[0]).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void runScenario ( final org.xtuml.masl.inspector.processInterface.DomainServiceMetaData service ) throws RemoteException
  {
    try
    {
      new InvokeDomainService((DomainServiceMetaData)service, new DataValue[0]).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void invokeObjectService ( final org.xtuml.masl.inspector.processInterface.ObjectServiceMetaData service,
                                    final Integer pk,
                                    final DataValue<?>[] parameters ) throws java.rmi.RemoteException
  {
    try
    {
      new InvokeObjectService((ObjectServiceMetaData)service, pk, parameters).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void lostConnection () throws java.rmi.RemoteException
  {
    System.out.println("Lost connection to process");
    closeConnection();
  }

  @Override
  public void pauseExecution () throws java.rmi.RemoteException
  {
    try
    {
      new PauseExecution().perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void runTestSchedule ( final File file ) throws RemoteException
  {
    try
    {
      new RunTestSchedule(file).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void setBreakpoint ( final org.xtuml.masl.inspector.processInterface.SourcePosition position, final Boolean enable ) throws java.rmi.RemoteException
  {
    try
    {
      new SetBreakpoint(position, Boolean.TRUE.equals(enable)).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void setCatchConsole ( final boolean enabled ) throws java.rmi.RemoteException
  {
    try
    {
      new SetCatchConsole(enabled).perform(processConnectionChannel);
      catchConsole = enabled;
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void setEnableTimers ( final boolean enabled ) throws RemoteException
  {
    try
    {
      new SetEnableTimers(enabled).perform(processConnectionChannel);
      enableTimers = enabled;
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void setStepBlocks ( final boolean enabled ) throws RemoteException
  {
    try
    {
      new SetStepBlocks(enabled).perform(processConnectionChannel);
      stepBlocks = enabled;
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void setStepEvents ( final boolean enabled ) throws RemoteException
  {
    try
    {
      new SetStepEvents(enabled).perform(processConnectionChannel);
      stepEvents = enabled;
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void setStepExceptions ( final boolean enabled ) throws RemoteException
  {
    try
    {
      new SetStepExceptions(enabled).perform(processConnectionChannel);
      stepExceptions = enabled;
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void setStepLines ( final boolean enabled ) throws RemoteException
  {
    try
    {
      new SetStepLines(enabled).perform(processConnectionChannel);
      stepLines = enabled;
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void setTraceBlocks ( final boolean enabled ) throws RemoteException
  {
    try
    {
      new SetTraceBlocks(enabled).perform(processConnectionChannel);
      traceBlocks = enabled;
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void setTraceEvents ( final boolean enabled ) throws RemoteException
  {
    try
    {
      new SetTraceEvents(enabled).perform(processConnectionChannel);
      traceEvents = enabled;
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void setTraceExceptions ( final boolean enabled ) throws RemoteException
  {
    try
    {
      new SetTraceExceptions(enabled).perform(processConnectionChannel);
      traceExceptions = enabled;
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void setPluginFlag ( final String pluginName, final String flagName, final boolean value ) throws RemoteException
  {
    try
    {
      new SetPluginFlag(pluginName, flagName, value).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void setPluginProperty ( final String pluginName, final String propertyName, final String value ) throws RemoteException
  {
    try
    {
      new SetPluginProperty(pluginName, propertyName, value).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void invokePluginAction ( final String pluginName, final String actionName ) throws RemoteException
  {
    try
    {
      new InvokePluginAction(pluginName, actionName).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void setTraceLines ( final boolean enabled ) throws RemoteException
  {
    try
    {
      new SetTraceLines(enabled).perform(processConnectionChannel);
      traceLines = enabled;
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void stepExecution () throws java.rmi.RemoteException
  {
    try
    {
      new StepExecution().perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  @Override
  public void updateSingleInstance ( final org.xtuml.masl.inspector.processInterface.ObjectMetaData object,
                                     final org.xtuml.masl.inspector.processInterface.InstanceData data ) throws java.rmi.RemoteException
  {
    try
    {
      new UpdateSingleInstance((ObjectMetaData)object, (InstanceData)data).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  // Doesn't work properly yet... dangling relationships cause strange results.
  public void deleteSingleInstance ( final boolean removeThisToActivate,
                                     final org.xtuml.masl.inspector.processInterface.ObjectMetaData object,
                                     final Object pk ) throws java.rmi.RemoteException
  {
    try
    {
      new DeleteSingleInstance((ObjectMetaData)object, pk).perform(processConnectionChannel);
    }
    catch ( final IOException e )
    {
      lostConnection();
    }
  }

  private final int                               MAIN_PORT_OFFSET               = 20000;
  private final int                               INFO_PORT_OFFSET               = 30000;
  private final int                               CONSOLE_PORT_OFFSET            = 40000;
  private final String                            host;
  private final int                               port;
  private CommunicationChannel                    processConnectionChannel;

  private CommunicationChannel                    processInfoChannel;

  private java.nio.channels.SocketChannel         processConnectionSocketChannel = null;

  private Socket                                  processInfoSocket              = null;


  private CommandServer                           commandServer;

  private ProcessMetaData                         processMetaData;

  private org.xtuml.masl.inspector.ConsoleRedirect consoleRedirector;

  private boolean                                 connected                      = false;

  private boolean                                 traceLines;

  private boolean                                 traceBlocks;

  private boolean                                 traceEvents;

  private boolean                                 traceExceptions;

  private boolean                                 stepLines;

  private boolean                                 stepBlocks;

  private boolean                                 stepExceptions;

  private boolean                                 stepEvents;

  private boolean                                 enableTimers;

  private boolean                                 catchConsole;

}
