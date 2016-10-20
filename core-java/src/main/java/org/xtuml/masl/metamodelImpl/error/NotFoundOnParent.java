//
// File: ParameterNotFoundOnService.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.error;

import org.xtuml.masl.metamodelImpl.common.Position;


public class NotFoundOnParent extends NotFound
{

  public NotFoundOnParent ( final SemanticErrorCode code, final Position position, final String name, final String parentName )
  {
    super(code, position, name, parentName);
  }
}
