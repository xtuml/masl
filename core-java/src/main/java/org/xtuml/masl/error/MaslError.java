//
// File: Error.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.error;

public abstract class MaslError extends Exception
{

  @Override
  public abstract String getMessage ();

  public ErrorCode getErrorCode ()
  {
    return errorCode;
  }

  protected MaslError ( final ErrorCode errorCode )
  {
    this.errorCode = errorCode;
  }

  public void report ()
  {
    ErrorLog.getInstance().report(this);
  }

  private final ErrorCode errorCode;

}
