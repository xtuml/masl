//
// File: DomainNotFound.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.error;

import org.xtuml.masl.metamodelImpl.common.Position;


public class NotFoundGlobal extends NotFound
{

  public NotFoundGlobal ( final SemanticErrorCode code, final Position position, final String name )
  {
    super(code, position, name);
  }
}
