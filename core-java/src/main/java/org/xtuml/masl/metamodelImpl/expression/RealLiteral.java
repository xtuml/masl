//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.DurationType;
import org.xtuml.masl.metamodelImpl.type.RealType;


public class RealLiteral extends NumericLiteral
    implements org.xtuml.masl.metamodel.expression.RealLiteral
{

  final private String text;
  final private Double value;

  public RealLiteral ( final Position position, final String text )
  {
    super(position);
    this.text = text;
    final String[] components = text.split("#");

    if ( components.length == 1 )
    {
      // Normal numeric literal
      this.value = Double.parseDouble(text);
    }
    else
    {
      // Based literal
      final int radix = Integer.parseInt(components[0]);

      String mantissaStr = components[1];
      final int pointPos = mantissaStr.indexOf('.', 0);
      int exponent = 0;

      if ( pointPos != -1 )
      {
        exponent = pointPos - mantissaStr.length() + 1;
        mantissaStr = mantissaStr.substring(0, pointPos) + mantissaStr.substring(pointPos + 1);
      }

      if ( components.length == 3 )
      {
        exponent += Integer.parseInt(components[2]);
      }

      final BigDecimal mantissa = new BigDecimal(new BigInteger(mantissaStr, radix));

      final double multiplicand = Math.pow(radix, exponent);

      this.value = mantissa.doubleValue() * multiplicand;
    }

  }


  public RealLiteral ( final double value )
  {
    super(null);
    this.text = "" + value;
    this.value = value;
  }

  @Override
  public Double getValue ()
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
    return RealType.createAnonymous();
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof RealLiteral) )
    {
      return false;
    }
    else
    {
      final RealLiteral obj2 = (RealLiteral)obj;

      return new Double(value).equals(new Double(obj2.value));
    }
  }

  @Override
  public int hashCode ()
  {

    return new Double(value).hashCode();
  }

  @Override
  protected Expression resolveInner ( final BasicType requiredType )
  {
    if ( requiredType.getPrimitiveType() instanceof DurationType && value == 0.0 )
    {
      return new DurationLiteral(getPosition(), this);
    }
    return this;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitRealLiteral(this, p);
  }


}
