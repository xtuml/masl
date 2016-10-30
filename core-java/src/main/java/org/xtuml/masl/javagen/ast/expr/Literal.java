//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;


public interface Literal
    extends Expression
{

  interface BooleanLiteral
      extends Literal
  {

    boolean getValue ();

    void setValue ( boolean value );
  }

  interface CharacterLiteral
      extends Literal
  {

    char getValue ();

    void setValue ( char value );
  }

  interface DoubleLiteral
      extends Literal
  {

    double getValue ();

    void setValue ( double value );
  }

  interface FloatLiteral
      extends Literal
  {

    float getValue ();

    void setValue ( float value );
  }

  interface IntegerLiteral
      extends Literal
  {

    int getValue ();

    void setValue ( int value );
  }

  interface LongLiteral
      extends Literal
  {

    long getValue ();

    void setValue ( long value );
  }

  interface NullLiteral
      extends Literal
  {
  }

  interface StringLiteral
      extends Literal
  {

    String getValue ();

    void setValue ( String value );
  }

}
