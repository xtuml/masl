// 
// Filename : SocketForwarder.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;


public class SocketForwarder extends Thread
{

  private InputOutputPipe inspectorToProcessPipe;
  private InputOutputPipe processToInspectorPipe;

  private final String    host;
  private final int       port;
  private final int       listenPort;
  private Socket          inspectorSocket = null;
  private Socket          processSocket   = null;


  public SocketForwarder ( final String host, final int port, final int listenPort )
  {
    this.host = host;
    this.port = port;
    this.listenPort = listenPort;
    setName("Console Redirection Connector");
  }


  @Override
  public void run ()
  {
    try
    {
      // Keep forwarding connections forever
      while ( true )
      {

        // Listen for incoming inspector
        while ( inspectorSocket == null )
        {
          try
          {
            final ServerSocket server = new ServerSocket(listenPort);
            inspectorSocket = server.accept();
            server.close();
          }
          catch ( final ConnectException e )
          {
            try
            {
              Thread.sleep(1000);
            }
            catch ( final InterruptedException ie )
            {
            }
          }
        }
        inspectorSocket.setTcpNoDelay(true);

        // Connect to process
        while ( processSocket == null )
        {
          try
          {
            processSocket = new Socket(host, port);
          }
          catch ( final ConnectException e )
          {
            try
            {
              Thread.sleep(1000);
            }
            catch ( final InterruptedException ie )
            {
            }
          }
        }
        processSocket.setTcpNoDelay(true);


        inspectorToProcessPipe = new InputOutputPipe(inspectorSocket.getInputStream(),
                                                     new BufferedOutputStream(processSocket.getOutputStream()));
        inspectorToProcessPipe.setName("Inspector -> Process");


        processToInspectorPipe = new InputOutputPipe(processSocket.getInputStream(),
                                                     new BufferedOutputStream(inspectorSocket.getOutputStream()));
        processToInspectorPipe.setName("Process -> Inspector");

        Runtime.getRuntime().addShutdownHook(new Thread("Socket Forwarder Shutdown")
        {

          @Override
          public void run ()
          {
            interrupt();
          }
        });

        try
        {
          // When inspector to Process Pipe terminates, the
          // inspector has shut down, so terminate the other
          // end and start all over again.
          inspectorToProcessPipe.join();
          interrupt();
        }
        catch ( final InterruptedException ie )
        {
        }
      }
    }
    catch ( final IOException e )
    {
      e.printStackTrace();
    }
  }

  @Override
  public void interrupt ()
  {
    try
    {
      if ( inspectorToProcessPipe != null )
      {
        inspectorToProcessPipe.interrupt();
      }
      if ( processToInspectorPipe != null )
      {
        processToInspectorPipe.interrupt();
      }
      if ( inspectorSocket != null )
      {
        inspectorSocket.close();
      }
      if ( processSocket != null )
      {
        processSocket.close();
      }
      inspectorSocket = null;
      processSocket = null;
    }
    catch ( final Exception e )
    {
      e.printStackTrace();
    }
  }

  static private final int MAIN_PORT_OFFSET    = 20000;
  static private final int INFO_PORT_OFFSET    = 30000;
  static private final int CONSOLE_PORT_OFFSET = 40000;

  public static void main ( final String[] args )
  {
    System.setProperty("java.awt.headless", "true");

    final String host = System.getProperty("host", "localhost");
    final int port = Integer.parseInt(System.getProperty("port", "0"));
    final int listenPort = Integer.parseInt(System.getProperty("listenPort", "" + port));

    System.out.println("Forwarding " + host + ":" + port + " to " + listenPort);

    final SocketForwarder mainRedirector = new SocketForwarder(host, port + MAIN_PORT_OFFSET, listenPort + MAIN_PORT_OFFSET);
    final SocketForwarder infoRedirector = new SocketForwarder(host, port + INFO_PORT_OFFSET, listenPort + INFO_PORT_OFFSET);
    final SocketForwarder consoleRedirector = new SocketForwarder(host,
                                                                  port + CONSOLE_PORT_OFFSET,
                                                                  listenPort + CONSOLE_PORT_OFFSET);
    mainRedirector.start();
    infoRedirector.start();
    consoleRedirector.start();

  }
}
