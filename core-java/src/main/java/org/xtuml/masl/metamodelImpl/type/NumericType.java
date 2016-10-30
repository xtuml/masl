//
// File: NumericType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import java.math.BigInteger;

import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.expression.IntegerLiteral;


public abstract class NumericType extends BuiltinType
{

  static final BigInteger ZERO = BigInteger.ZERO;
  static final BigInteger ONE  = BigInteger.ONE;
  static final BigInteger TWO  = BigInteger.valueOf(2);


  private NumericType ( final Position position,
                        final String name,
                        final Expression min,
                        final Expression max,
                        final boolean anonymous )
  {
    super(position, name, anonymous);
    this.min = min;
    this.max = max;
  }

  protected NumericType ( final Position position, final String name, final boolean anonymous )
  {
    this(position, name, null, null, anonymous);
  }


  protected NumericType ( final Position position, final String name, final boolean signed, final int bits, final boolean anonymous )
  {

    super(position, name, anonymous);
    if ( signed )
    {
      final BigInteger max = TWO.pow(bits - 1);
      final BigInteger min = max.negate().subtract(ONE);
      this.max = new IntegerLiteral(max.longValue());
      this.min = new IntegerLiteral(min.longValue());
    }
    else
    {
      final BigInteger max = TWO.pow(bits);
      final BigInteger min = ZERO;
      this.max = new IntegerLiteral(max.longValue());
      this.min = new IntegerLiteral(min.longValue());
    }
  }

  private Expression min;
  private Expression max;

  @Override
  public Expression getMinValue ()
  {
    return min;
  }

  @Override
  public Expression getMaxValue ()
  {
    return max;
  }

  @Override
  abstract public NumericType getPrimitiveType ();

  @Override
  abstract public NumericType getBasicType ();

  @Override
  public boolean isNumeric ()
  {
    return true;
  }

}
