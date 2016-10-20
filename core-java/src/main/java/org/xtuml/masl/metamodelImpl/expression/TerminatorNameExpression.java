//
// File: ObjectNameExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.domain.DomainTerminator;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.InternalType;


public class TerminatorNameExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.TerminatorNameExpression
{

  public static TerminatorNameExpression create ( final Domain.Reference domainRef, final String termName )
  {
    if ( domainRef == null || termName == null )
    {
      return null;
    }

    try
    {
      final Position position = domainRef.getPosition() == null ? Position.getPosition(termName) : domainRef.getPosition();
      return domainRef.getDomain().getTerminator(termName).getReference(position);

    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }


  public TerminatorNameExpression ( final Position position, final DomainTerminator terminator )
  {
    super(position);
    this.terminator = terminator;
  }

  @Override
  public DomainTerminator getTerminator ()
  {
    return terminator;
  }

  private final DomainTerminator terminator;

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof TerminatorNameExpression) )
    {
      return false;
    }
    else
    {
      final TerminatorNameExpression term = (TerminatorNameExpression)obj;

      return terminator.equals(term.terminator);
    }
  }

  @Override
  public int hashCode ()
  {
    return terminator.hashCode();
  }

  @Override
  public String toString ()
  {
    return terminator.getName();
  }


  @Override
  public BasicType getType ()
  {
    return InternalType.TERMINATOR;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitTerminatorNameExpression(this, p);
  }

}
