//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.DurationType;
import org.xtuml.masl.metamodelImpl.type.IntegerType;


public class IntegerLiteral extends NumericLiteral
    implements org.xtuml.masl.metamodel.expression.IntegerLiteral
{

  final private String text;

  final private Long   value;

  public IntegerLiteral ( final Position position, final String text )
  {
    super(position);
    this.text = text;
    final String[] components = text.split("#");

    if ( components.length == 1 )
    {
      // Normal numeric literal
      this.value = Long.parseLong(text);
    }
    else
    {
      // Based literal
      final int radix = Integer.parseInt(components[0]);
      value = Long.parseLong(components[1], radix);
    }
  }


  public IntegerLiteral ( final Long value )
  {
    super(null);
    this.text = "" + value;
    this.value = value;
  }

  @Override
  public Long getValue ()
  {
    return value;
  }

  public String getText ()
  {
    return text;
  }

  @Override
  public String toString ()
  {
    return text;
  }

  @Override
  public BasicType getType ()
  {
    return IntegerType.createAnonymous();
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof IntegerLiteral) )
    {
      return false;
    }
    else
    {
      final IntegerLiteral obj2 = (IntegerLiteral)obj;

      return value.equals(obj2.value);
    }
  }

  @Override
  public int hashCode ()
  {

    return value.hashCode();
  }

  @Override
  protected Expression resolveInner ( final BasicType requiredType )
  {
    if ( requiredType.getPrimitiveType() instanceof DurationType && value == 0 )
    {
      return new DurationLiteral(getPosition(), this);
    }
    return this;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitIntegerLiteral(this, p);
  }


}
