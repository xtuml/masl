//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.exception;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.error.SemanticError;


public abstract class ExceptionReference extends Positioned
    implements org.xtuml.masl.metamodel.exception.ExceptionReference
{

  public static ExceptionReference create ( final Domain.Reference domainRef, final boolean allowBuiltin, final String name )
  {
    if ( domainRef == null || name == null )
    {
      return null;
    }

    try
    {
      final Position position = domainRef.getPosition() == null ? Position.getPosition(name) : domainRef.getPosition();

      if ( allowBuiltin )
      {
        final ExceptionDeclaration exception = domainRef.getDomain().findException(name);
        if ( exception == null )
        {
          return BuiltinException.create(position, name);
        }
        else
        {
          return exception.getReference(position);
        }
      }
      else
      {
        return domainRef.getDomain().getException(name).getReference(position);
      }
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }

  }


  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitExceptionReference(this, p);
  }


  public ExceptionReference ( final Position position )
  {
    super(position);
  }

}
