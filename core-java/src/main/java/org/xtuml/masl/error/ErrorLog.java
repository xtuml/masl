//
// File: ErrorLog.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.error;

import java.util.ArrayList;
import java.util.List;


public class ErrorLog
{

  private static ErrorLog instance = new ErrorLog();

  private ErrorLog ()
  {
  }

  public static ErrorLog getInstance ()
  {
    return instance;
  }

  private final List<ErrorListener> errorListeners = new ArrayList<ErrorListener>();


  public void addErrorListener ( final ErrorListener listener )
  {
    errorListeners.add(listener);
  }


  public void report ( final MaslError error )
  {
    for ( final ErrorListener listener : errorListeners )
    {
      listener.errorReported(error);
    }

  }

}
