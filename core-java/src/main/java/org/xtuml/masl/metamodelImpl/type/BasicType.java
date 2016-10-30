//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.error.NotFound;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;


public abstract class BasicType extends TypeDefinition
    implements org.xtuml.masl.metamodel.type.BasicType
{

  public static BasicType createNamedType ( final Domain.Reference domainRef,
                                            final boolean allowBuiltin,
                                            final String name,
                                            final boolean anonymous )
  {
    if ( domainRef == null || name == null )
    {
      return null;
    }

    try
    {
      if ( allowBuiltin )
      {
        final TypeDeclaration type = domainRef.getDomain().findType(name);
        if ( type == null )
        {
          return BuiltinType.create(Position.getPosition(name), name, anonymous);
        }
        else
        {
          if ( anonymous )
          {
            new SemanticError(SemanticErrorCode.AnonymousUserDefinedType, Position.getPosition(name)).report();
          }
          return UserDefinedType.create(domainRef, name);
        }
      }
      else
      {
        if ( anonymous )
        {
          new SemanticError(SemanticErrorCode.AnonymousUserDefinedType, Position.getPosition(name)).report();
        }
        return UserDefinedType.create(domainRef, name);
      }
    }
    catch ( final NotFound e )
    {
      e.report();
      return null;
    }

  }


  BasicType ( final Position position, final boolean anonymous )
  {
    super(position);
    this.anonymous = anonymous;
  }

  public final boolean isAnonymousType ()
  {
    return anonymous;
  }

  private final boolean anonymous;

  public BasicType getContainedType ()
  {
    return null;
  }

  public void checkAssignable ( final Expression rhs ) throws SemanticError
  {
    if ( !isAssignableFrom(rhs) )
    {
      throw new SemanticError(SemanticErrorCode.NotAssignable, rhs.getPosition(), rhs.getType().toString(), this.toString());
    }
  }

  public void checkAssignable ( final BasicType rhs ) throws SemanticError
  {
    if ( !isAssignableFrom(rhs) )
    {
      throw new SemanticError(SemanticErrorCode.NotAssignable, rhs.getPosition(), rhs.toString(), this.toString());
    }
  }


  public final boolean isAssignableFrom ( final Expression rhs )
  {
    return isAssignableFrom(rhs, false);
  }

  public final boolean isAssignableFrom ( final Expression rhs, final boolean allowSeqPromote )
  {
    return isAssignableFrom(rhs.resolve(this, allowSeqPromote).getType());
  }

  public final boolean isAssignableFrom ( final BasicType rhs )
  {
    if ( isAnonymousType() || rhs.isAnonymousType() )
    {
      return getPrimitiveType().isAssignableFromInner(rhs.getPrimitiveType());
    }
    else
    {
      return isAssignableFromInner(rhs);
    }
  }

  private final boolean isAssignableFromInner ( final BasicType rhs )
  {
    return this.equals(rhs) || isAssignableFromRelaxation(rhs);
  }

  protected boolean isAssignableFromRelaxation ( final BasicType rhs )
  {
    return false;
  }

  public final boolean isConvertibleFrom ( final BasicType rhs )
  {
    return isConvertibleFrom(rhs, true);
  }

  protected final boolean isConvertibleFrom ( final BasicType rhs, final boolean allowSeqPromote )
  {
    return getPrimitiveType().isConvertibleFromInner(rhs.getPrimitiveType(), allowSeqPromote);
  }

  private final boolean isConvertibleFromInner ( final BasicType rhs, final boolean allowSeqPromote )
  {
    return this.equals(rhs) || isConvertibleFromRelaxation(rhs)
           // Allow promotion to sequence
           || (allowSeqPromote && getContainedType() != null && getContainedType().isConvertibleFrom(rhs, allowSeqPromote))
           // Allow conversion of single value to single component structures
           || (getPrimitiveType() instanceof AnonymousStructure
               && ((AnonymousStructure)getPrimitiveType()).getElements().size() == 1 && ((AnonymousStructure)getPrimitiveType())
                                                                                                                                .getElements()
                                                                                                                                .get(0)
                                                                                                                                .isConvertibleFrom(rhs,
                                                                                                                                                   allowSeqPromote));
  }

  protected boolean isConvertibleFromRelaxation ( final BasicType rhs )
  {
    return false;
  }

  @Override
  abstract public BasicType getBasicType ();

  public BasicType getBaseType ()
  {
    return this;
  }

}
