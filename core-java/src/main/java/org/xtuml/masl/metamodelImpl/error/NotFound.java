//
// File: AlreadyDefined.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.error;

import org.xtuml.masl.metamodelImpl.common.Position;


public abstract class NotFound extends SemanticError
{

  static private Object[] composeArgs ( final String name, final Object... args )
  {
    final Object[] result = new Object[args.length + 1];
    result[0] = name;
    System.arraycopy(args, 0, result, 1, args.length);
    return result;
  }

  public NotFound ( final SemanticErrorCode code, final Position position, final String name, final Object... args )
  {
    super(code, position, composeArgs(name, args));
  }

}
