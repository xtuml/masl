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

  private Statement parent;
  private PragmaList pragmas;

  public Statement ( final Position position )
  {
    super(position);
    parent = null;
  }

  public void setParentStatement ( final Statement parent )
  {
    this.parent = parent;
  }

  @Override
  public Statement getParentStatement ()
  {
    return parent;
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

  @Override
  public boolean inExceptionHandler ()
  {
    final Statement parent = getParentStatement();
    if ( parent != null )
    {
      if ( parent instanceof CodeBlock ) {
        // check if this statement is contained in any of the codeblock handlers
        final CodeBlock block = (CodeBlock) parent;
        return block.getExceptionHandlers().stream().flatMap(h -> h.getCode().stream()).anyMatch(this::equals);
      }
      else
      {
        return parent.inExceptionHandler();
      }
    }
    else
    {
      return false;
    }
  }

}
