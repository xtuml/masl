//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.exception;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.common.Visibility;
import org.xtuml.masl.metamodelImpl.domain.Domain;


public final class ExceptionDeclaration extends Positioned
    implements org.xtuml.masl.metamodel.exception.ExceptionDeclaration
{

  public class Reference extends ExceptionReference
      implements org.xtuml.masl.metamodel.exception.UserDefinedException
  {

    private Reference ( final Position position )
    {
      super(position);
    }

    @Override
    public String getName ()
    {
      return ExceptionDeclaration.this.getName();
    }

    @Override
    public ExceptionDeclaration getException ()
    {
      return ExceptionDeclaration.this;
    }

    @Override
    public String toString ()
    {
      return ExceptionDeclaration.this.getDomain().getName() + "::" + ExceptionDeclaration.this.getName();
    }

  }

  public Reference getReference ( final Position position )
  {
    return new Reference(position);
  }


  private final String     name;
  private final Visibility visibility;
  private final PragmaList pragmas;

  public ExceptionDeclaration ( final Position position,
                                final Domain domain,
                                final String name,
                                final Visibility visibility,
                                final PragmaList pragmas )
  {
    super(position);
    this.domain = domain;
    this.name = name;
    this.visibility = visibility;
    this.pragmas = pragmas;
  }


  @Override
  public String getName ()
  {
    return name;
  }

  @Override
  public PragmaList getPragmas ()
  {
    return pragmas;
  }

  @Override
  public org.xtuml.masl.metamodel.common.Visibility getVisibility ()
  {
    return visibility.getVisibility();
  }

  private final Domain domain;

  @Override
  public Domain getDomain ()
  {
    return domain;
  }

  @Override
  public String toString ()
  {
    return visibility + (visibility.toString().equals("") ? "" : " ") + "exception\t" + name + ";\n" + pragmas;
  }


  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitExceptionDeclaration(this, p);
  }

  private String comment;

  public String getComment ()
  {
    return comment;
  }

  public void setComment ( final String comment )
  {
    this.comment = comment;
  }
}
