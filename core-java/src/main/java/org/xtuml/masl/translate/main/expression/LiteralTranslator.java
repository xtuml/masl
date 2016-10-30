//
// File: LiteralExpressionTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.expression;

import java.math.BigInteger;

import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.metamodel.expression.BooleanLiteral;
import org.xtuml.masl.metamodel.expression.CharacterLiteral;
import org.xtuml.masl.metamodel.expression.ConsoleLiteral;
import org.xtuml.masl.metamodel.expression.DurationLiteral;
import org.xtuml.masl.metamodel.expression.EndlLiteral;
import org.xtuml.masl.metamodel.expression.EnumerateLiteral;
import org.xtuml.masl.metamodel.expression.FlushLiteral;
import org.xtuml.masl.metamodel.expression.IntegerLiteral;
import org.xtuml.masl.metamodel.expression.LiteralExpression;
import org.xtuml.masl.metamodel.expression.NullLiteral;
import org.xtuml.masl.metamodel.expression.RealLiteral;
import org.xtuml.masl.metamodel.expression.StringLiteral;
import org.xtuml.masl.metamodel.expression.ThisLiteral;
import org.xtuml.masl.metamodel.expression.TimestampLiteral;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.EnumerationTranslator;
import org.xtuml.masl.translate.main.Types;
import org.xtuml.masl.translate.main.object.ObjectTranslator;



public class LiteralTranslator extends ExpressionTranslator
{

  // Assume 32 bit long and 64 bit long long
  static final int        LONG_BITS     = 32;
  static final int        LONGLONG_BITS = 64;

  static final BigInteger longMax       = BigInteger.valueOf(2).pow(LONG_BITS - 1);
  static final BigInteger longMin       = longMax.negate().subtract(BigInteger.ONE);

  static final BigInteger ulongMax      = BigInteger.valueOf(2).pow(LONG_BITS);
  static final BigInteger ulongMin      = BigInteger.ZERO;

  static final BigInteger longlongMax   = BigInteger.valueOf(2).pow(LONGLONG_BITS - 1);
  static final BigInteger longlongMin   = longlongMax.negate().subtract(BigInteger.ONE);

  static final BigInteger ulonglongMax  = BigInteger.valueOf(2).pow(LONGLONG_BITS);
  static final BigInteger ulonglongMin  = BigInteger.ZERO;


  static final String     escapedChars  = new String("\'\\");

  LiteralTranslator ( final LiteralExpression literal )
  {
    this.literal = literal;
    if ( literal instanceof BooleanLiteral )
    {
      setReadExpression(((BooleanLiteral)literal).getValue() ? org.xtuml.masl.cppgen.Literal.TRUE
                                                            : org.xtuml.masl.cppgen.Literal.FALSE);
    }
    else if ( literal instanceof StringLiteral )
    {
      setReadExpression(Architecture.stringClass.callConstructor(org.xtuml.masl.cppgen.Literal.createStringLiteral(((StringLiteral)literal).getValue())));
    }
    else if ( literal instanceof IntegerLiteral )
    {
      setReadExpression(new org.xtuml.masl.cppgen.Literal(((IntegerLiteral)literal).getValue() + "ll"));
    }
    else if ( literal instanceof RealLiteral )
    {
      setReadExpression(new org.xtuml.masl.cppgen.Literal("" + ((RealLiteral)literal).getValue()));
    }

    else if ( literal instanceof CharacterLiteral )
    {
      setReadExpression(org.xtuml.masl.cppgen.Literal.createCharLiteral(((CharacterLiteral)literal).getValue()));
    }
    else if ( literal instanceof DurationLiteral )
    {
      setReadExpression(Architecture.Duration.fromNanos(
                                             new org.xtuml.masl.cppgen.Literal(((DurationLiteral)literal).getNanos() + "ll")));
    }
    else if ( literal instanceof TimestampLiteral )
    {
      final long nanosSinceEpoch = ((TimestampLiteral)literal).getNanos() + ((TimestampLiteral)literal).getValue().getTime()
                                   * 1000000;
      setReadExpression(Architecture.Timestamp.createFromNanosSinceEpoch(
                                              new org.xtuml.masl.cppgen.Literal(nanosSinceEpoch + "ll")));
    }
    else if ( literal instanceof EnumerateLiteral )
    {
      final EnumerateLiteral enumerator = (EnumerateLiteral)literal;

      // Force creation of the enumerate type, as it might not have been used
      // yet, eg if the literal is just used in a function/service call.
      Types.getInstance().getType(enumerator.getType());

      final EnumerationTranslator enumeration = Types.getInstance().getEnumerateTranslator(enumerator.getType()
                                                                                                     .getTypeDeclaration());
      setReadExpression(enumeration.getEnumerator(enumerator.getValue()));
    }
    else if ( literal instanceof EndlLiteral )
    {
      setReadExpression(org.xtuml.masl.cppgen.Literal.NEWLINE);
    }
    else if ( literal instanceof FlushLiteral )
    {
      setReadExpression(Std.flush);
    }
    else if ( literal instanceof NullLiteral )
    {
      setReadExpression(Architecture.nullPointer);
    }
    else if ( literal instanceof ConsoleLiteral )
    {
      setReadExpression(Architecture.console.asFunctionCall());
    }
    else if ( literal instanceof ThisLiteral )
    {
      final ObjectTranslator translator = ObjectTranslator.getInstance(((ThisLiteral)literal).getObject());
      setReadExpression(translator.createPointer(translator.getMainClass().getThis().asExpression()));
    }
    else
    {
      throw new IllegalArgumentException("Unrecognised Literal '" + literal.getClass() + " " + literal + "'");
    }
  }

  private final LiteralExpression literal;

  public LiteralExpression getLiteral ()
  {
    return literal;
  }

}
