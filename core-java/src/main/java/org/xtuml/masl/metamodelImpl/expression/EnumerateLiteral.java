//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.EnumerateItem;
import org.xtuml.masl.metamodelImpl.type.EnumerateType;
import org.xtuml.masl.metamodelImpl.type.InternalType;
import org.xtuml.masl.metamodelImpl.type.UserDefinedType;


public class EnumerateLiteral extends LiteralExpression
    implements org.xtuml.masl.metamodel.expression.EnumerateLiteral
{

  public static class AmbiguousEnumerateLiteral extends LiteralExpression
  {

    public AmbiguousEnumerateLiteral ( final Position position, final List<EnumerateItem> values )
    {
      super(position);
      for ( final EnumerateItem value : values )
      {
        lookup.put(value.getEnumerate(), value.getReference(position));
      }
    }

    @Override
    public boolean equals ( final Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( !(obj instanceof AmbiguousEnumerateLiteral) )
      {
        return false;
      }
      else
      {
        final AmbiguousEnumerateLiteral obj2 = (AmbiguousEnumerateLiteral)obj;

        return lookup.equals(obj2.lookup);
      }
    }

    @Override
    public LiteralExpression resolveInner ( final BasicType type )
    {
      LiteralExpression result = this;

      if ( type.getPrimitiveType() instanceof UserDefinedType )
      {
        final UserDefinedType udt = (UserDefinedType)type.getPrimitiveType();
        result = lookup.get(udt.getDefinedType());
      }
      return result;
    }


    @Override
    public BasicType getType ()
    {
      return InternalType.AMBIGUOUS_ENUM;
    }

    private final Map<EnumerateType, EnumerateLiteral> lookup = new HashMap<EnumerateType, EnumerateLiteral>();

    @Override
    public int hashCode ()
    {

      return lookup.hashCode();
    }

    @Override
    public String toString ()
    {
      return lookup.values().toArray(new EnumerateLiteral[0])[0].getValue().getName();
    }

    @Override
    public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
    {
      throw new IllegalStateException("Cannot visit AmbiguousEnumerateLiteral");
    }
  }


  private final EnumerateItem value;

  public EnumerateLiteral ( final Position position, final EnumerateItem value )
  {
    super(position);
    this.value = value;
  }

  @Override
  public EnumerateItem getValue ()
  {
    return value;
  }

  @Override
  public String toString ()
  {
    return value.getEnumerate().getTypeDeclaration().getDomain().getName()
           + "::"
           + value.getEnumerate().getTypeDeclaration().getName()
           + "."
           + value.getName();
  }

  @Override
  public BasicType getType ()
  {
    return value.getEnumerate().getTypeDeclaration().getDeclaredType();
  }

  @Override
  public int getIndex ()
  {
    return value.getIndex();
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof EnumerateLiteral) )
    {
      return false;
    }
    else
    {
      final EnumerateLiteral obj2 = (EnumerateLiteral)obj;

      return value.equals(obj2.value);
    }
  }

  @Override
  public int hashCode ()
  {

    return value.hashCode();
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitEnumerateLiteral(this, p);
  }


}
