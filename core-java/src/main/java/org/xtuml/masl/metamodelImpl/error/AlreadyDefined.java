//
// File: AlreadyDefined.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.error;

import org.xtuml.masl.metamodelImpl.common.Position;


public class AlreadyDefined extends SemanticError
{

  public AlreadyDefined ( final SemanticErrorCode code, final Position position, final String name, final Position previousDef )
  {
    super(code, position, name, previousDef.getText(), name, previousDef);
  }
}
