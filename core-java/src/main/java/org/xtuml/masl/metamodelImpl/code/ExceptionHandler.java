//
// File: ExceptionHandler.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.exception.ExceptionReference;
import org.xtuml.masl.utils.TextUtils;


public final class ExceptionHandler extends Positioned
    implements org.xtuml.masl.metamodel.code.ExceptionHandler
{

  public static ExceptionHandler create ( final ExceptionReference ref )
  {
    if ( ref == null )
    {
      return null;
    }

    return new ExceptionHandler(ref);
  }

  public static ExceptionHandler create ( final Position pos )
  {
    if ( pos == null )
    {
      return null;
    }

    return new ExceptionHandler(pos);
  }

  private final List<Statement>    code;
  private final ExceptionReference exception;

  public ExceptionHandler ( final Position position )
  {
    super(position);
    this.exception = null;
    this.code = new ArrayList<Statement>();
  }

  public ExceptionHandler ( final ExceptionReference exception )
  {
    super(exception.getPosition());
    this.exception = exception;
    this.code = new ArrayList<Statement>();
  }

  @Override
  public int getLineNumber ()
  {
    return getPosition().getLineNumber();
  }


  public void addStatement ( final Statement statement )
  {
    if ( statement != null )
    {
      code.add(statement);
    }
  }

  @Override
  public List<Statement> getCode ()
  {
    return Collections.unmodifiableList(code);
  }

  @Override
  public ExceptionReference getException ()
  {
    return this.exception;
  }

  @Override
  public String toString ()
  {
    return "when "
           + (exception == null ? "others" : exception.toString())
           + " =>\n"
           + TextUtils.indentText("  ", TextUtils.formatList(code, "", "\n", ""));
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitExceptionHandler(this, p);
  }

}
