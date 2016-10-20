//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.DurationType;


public class DurationLiteral extends LiteralExpression
    implements org.xtuml.masl.metamodel.expression.DurationLiteral
{

  static final String       number      = "(\\d+(?:\\.\\d+)?(?!..)|\\d+)";
  static final String       weeks       = "(?:" + number + "W)?";
  static final String       years       = "(?:" + number + "Y)?";
  static final String       months      = "(?:" + number + "M)?";
  static final String       days        = "(?:" + number + "D)?";
  static final String       hours       = "(?:" + number + "H)?";
  static final String       minutes     = "(?:" + number + "M)?";
  static final String       seconds     = "(?:" + number + "S)?";
  static final String       time        = "(?:T(?=.)" + hours + minutes + seconds + ")?";
  static final Pattern      pattern     = Pattern.compile("P(?=.)" + "(?:" + weeks + "|" + years + months + days + time + ")");

  static final long         S           = 1000000000;
  static final long         M           = 60 * S;
  static final long         H           = 60 * M;
  static final long         D           = 24 * H;
  static final long         W           = 7 * D;

  static final BigDecimal[] multipliers =
                                          { BigDecimal.ZERO,
                                          BigDecimal.ZERO,
                                          new BigDecimal(W),
                                          new BigDecimal(D),
                                          new BigDecimal(H),
                                          new BigDecimal(M),
                                          new BigDecimal(S) };
  static final int[]        resultPos   =
                                          { 2, 3, 1, 4, 5, 6, 7 };

  public static DurationLiteral create ( final Position position, final String literal )
  {
    try
    {
      return new DurationLiteral(position, literal);
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }

  DurationLiteral ( final Position position, final NumericLiteral literal )
  {
    // Should only be called for a value of zero when resoving from a numeric
    // literal
    super(position);
    original = literal.toString();
    nanos = literal.getValue().longValue();
  }

  private DurationLiteral ( final Position position, final String literal ) throws SemanticError
  {
    super(position);
    original = literal;

    final String theDuration = literal.substring(1, literal.length() - 1);
    final Matcher matcher = pattern.matcher(theDuration);

    if ( matcher.matches() )
    {
      BigDecimal result = new BigDecimal(0);

      for ( int i = 0; i < multipliers.length; ++i )
      {
        final String str = matcher.group(resultPos[i]);
        if ( str != null )
        {
          final BigDecimal val = new BigDecimal(str);
          result = result.add(val.multiply(multipliers[i]));
          if ( multipliers[i].compareTo(BigDecimal.ZERO) == 0 && val.compareTo(BigDecimal.ZERO) != 0 )
          {
            throw new SemanticError(SemanticErrorCode.IndeterminateDuration, position);
          }
        }

      }
      nanos = result.setScale(0, RoundingMode.HALF_UP).longValue();
    }
    else
    {
      throw new SemanticError(SemanticErrorCode.DurationFormatNotRecognised, position);
    }
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof DurationLiteral) )
    {
      return false;
    }
    else
    {
      final DurationLiteral obj2 = (DurationLiteral)obj;

      return nanos == obj2.nanos;
    }
  }

  @Override
  public BasicType getType ()
  {
    return DurationType.createAnonymous();
  }

  @Override
  public long getNanos ()
  {
    return nanos;
  }

  @Override
  public int hashCode ()
  {
    return (int)(nanos ^ nanos >>> 32);
  }

  @Override
  public String toString ()
  {
    return original;
  }

  final private long   nanos;
  final private String original;

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitDurationLiteral(this, p);
  }


}
