//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.expression.EnumerateLiteral;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.name.Name;


public class EnumerateItem extends Name
    implements org.xtuml.masl.metamodel.type.EnumerateItem
{

  public static class AmbiguousEnumItem extends Name
  {

    public AmbiguousEnumItem ( final String name, final List<EnumerateItem> items )
    {
      super(name);
      this.items = items;
    }

    private final List<EnumerateItem> items;

    @Override
    public EnumerateLiteral.AmbiguousEnumerateLiteral getReference ( final Position position )
    {
      return new EnumerateLiteral.AmbiguousEnumerateLiteral(position, items);
    }
  }


  private final Expression value;
  private EnumerateType    enumerate;
  private int              index;
  private String           comment;

  public EnumerateItem ( final String name, final Expression value )
  {
    super(name);
    this.value = value;
  }

  public EnumerateItem ( final String name )
  {
    this(name, null);
  }

  @Override
  public EnumerateType getEnumerate ()
  {
    return enumerate;
  }


  @Override
  public Expression getValue ()
  {
    return value;
  }

  @Override
  public EnumerateLiteral getReference ( final Position position )
  {
    return new EnumerateLiteral(position, this);
  }


  public int getIndex ()
  {
    return index;
  }

  @Override
  public String toString ()
  {
    return getName() + (value == null ? "" : " = " + value);
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof EnumerateItem) )
    {
      return false;
    }
    else
    {
      final EnumerateItem rhs = (EnumerateItem)obj;

      return getName().equals(rhs.getName()) && ((value == null && rhs.value == null) || (value != null && value.equals(rhs.value)));
    }
  }

  @Override
  public int hashCode ()
  {

    return getName().hashCode() * 31 + (value == null ? 0 : value.hashCode());
  }

  public void setEnumerate ( final EnumerateType enumerate )
  {
    this.enumerate = enumerate;
  }

  public void setIndex ( final int i )
  {
    index = i;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitEnumerateItem(this, p);
  }

  @Override
  public String getComment ()
  {
    return comment;
  }

  public void setComment ( final String comment )
  {
    this.comment = comment;
  }

}
