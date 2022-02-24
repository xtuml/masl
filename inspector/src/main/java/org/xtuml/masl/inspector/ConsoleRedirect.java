//
// Filename : ConsoleRedirect.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConsoleRedirect extends Thread {

    private InputOutputPipe outputPipe;
    private InputOutputPipe inputPipe;

    private final InetSocketAddress address;
    private Socket consoleSocket = null;

    public static InputStream inputSource = System.in;
    public static OutputStream outputSink = System.out;

    public ConsoleRedirect(final InetSocketAddress address) {
        this.address = address;
        setName("Console Redirection Connector");
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (consoleSocket == null) {
                try {
                    consoleSocket = new Socket(address.getAddress(), address.getPort());
                } catch (final ConnectException e) {
                    System.out.print(",");
                    try {
                        Thread.sleep(1000);
                    } catch (final InterruptedException ie) {
                    }
                }
            }
            consoleSocket.setTcpNoDelay(true);

            if (outputSink != null) {
                // Pipe ooa process output to inspector screen output
                outputPipe = new InputOutputPipe(consoleSocket.getInputStream(), new BufferedOutputStream(outputSink));
                outputPipe.setName("Ooa->Console Pipe");
            }

            if (inputSource != null) {
                // Pipe inspector input to ooa process input
                inputPipe = new InputOutputPipe(inputSource, new BufferedOutputStream(consoleSocket.getOutputStream()));
                inputPipe.setName("Console->Ooa Pipe");
            }

            Runtime.getRuntime().addShutdownHook(new Thread("Console Redirection Shutdown") {

                @Override
                public void run() {
                    try {
                        if (outputPipe != null) {
                            outputPipe.interrupt();
                        }
                        if (inputPipe != null) {
                            inputPipe.interrupt();
                        }
                        if (consoleSocket != null) {
                            consoleSocket.close();
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void interrupt() {
        try {
            if (outputPipe != null) {
                outputPipe.interrupt();
            }
            if (inputPipe != null) {
                inputPipe.interrupt();
            }
            if (consoleSocket != null) {
                consoleSocket.close();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
