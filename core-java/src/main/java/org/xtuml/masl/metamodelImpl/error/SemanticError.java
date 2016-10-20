//
// File: SemanticError.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.error;

import java.text.MessageFormat;

import org.xtuml.masl.error.MaslError;
import org.xtuml.masl.metamodelImpl.common.Position;


public class SemanticError extends MaslError
{

  public SemanticError ( final SemanticErrorCode code, final Position position, final Object... args )
  {
    super(code);
    format = new MessageFormat(code.getMessageFormat());
    this.position = position;
    this.args = args;
  }

  @Override
  public String getMessage ()
  {

    final String posString = position == null ? "<unknown position>" : position.getText();
    final String message = format.format(args);
    final String context = position != null ? "\n" + position.getContext() : "";

    return posString + ": " + getErrorCode().getErrorType() + ": " + message + context;

  }

  private final Position      position;

  private final MessageFormat format;

  private final Object[]      args;

}
