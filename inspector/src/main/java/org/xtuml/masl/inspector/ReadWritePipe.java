// 
// Filename : ReadWritePipe.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Reader;
import java.io.Writer;


public class ReadWritePipe extends Thread
{

  private final Reader     reader;
  private final Writer     writer;

  private static final int bufferSize = 8192;

  public ReadWritePipe ( final Reader in, final Writer out )
  {
    reader = in;
    writer = out;
    setDaemon(true);
    start();
  }

  @Override
  public void run ()
  {
    try
    {
      final char[] data = new char[bufferSize];
      int charsRead;
      while ( !isInterrupted() )
      {
        charsRead = reader.read(data);
        if ( charsRead > 0 )
        {
          writer.write(data, 0, charsRead);
          // If read would block, then flush the output first
          if ( !reader.ready() )
          {
            writer.flush();
          }
        }
        else
        {
          writer.flush();
        }
      }
    }
    catch ( final InterruptedIOException e )
    {
    }
    catch ( final IOException e )
    {
      e.printStackTrace();
    }
  }


}
