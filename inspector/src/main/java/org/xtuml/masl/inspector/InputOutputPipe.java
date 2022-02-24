// 
// Filename : InputOutputPipe.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector;

import java.io.BufferedOutputStream;
import java.io.InputStream;

public class InputOutputPipe extends Thread {

    private final InputStream in;
    private final BufferedOutputStream out;

    private static final int bufferSize = 8192;

    public InputOutputPipe(final InputStream in, final BufferedOutputStream out) {
        this.in = in;
        this.out = out;
        setDaemon(true);
        start();
    }

    @Override
    public void run() {
        try {
            final byte[] data = new byte[bufferSize];
            int bytesRead = 0;
            while (!isInterrupted() && bytesRead >= 0) {
                bytesRead = in.read(data);
                out.write(data, 0, bytesRead);
                // If read would block, then flush the output first
                if (in.available() == 0) {
                    out.flush();
                }
            }
        } catch (final Exception e) {
        }
    }

}
