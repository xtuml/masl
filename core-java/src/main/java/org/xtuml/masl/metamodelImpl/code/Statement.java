//
// File: Statement.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import java.util.Collections;
import java.util.List;

import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.PragmaList;



public abstract class Statement extends Positioned
    implements org.xtuml.masl.metamodel.code.Statement
{

  public Statement ( final Position position )
  {
    super(position);
  }

  public void setPragmas ( final PragmaList pragmas )
  {
    this.pragmas = pragmas;
  }

  @Override
  public PragmaList getPragmas ()
  {
    return pragmas;
  }

  private PragmaList pragmas;

  @Override
  public int getLineNumber ()
  {
    return getPosition() == null ? 0 : getPosition().getLineNumber();
  }

  @Override
  public String toAbbreviatedString ()
  {
    return toString();
  }

  @Override
  public List<Statement> getChildStatements ()
  {
    return Collections.<Statement>emptyList();
  }
}
