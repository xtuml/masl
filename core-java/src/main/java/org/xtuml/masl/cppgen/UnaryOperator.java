//
// File: Operator.java
//
// UK Crown Copyright (c) 2007. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

public enum UnaryOperator
{
  PLUS("+", true, 3),
  MINUS("-", true, 3),
  NOT("!", true, 3),
  BITNOT("~", true, 3),
  ADDRESS_OF("&", true, 3),
  DEREFERENCE("*", true, 3),
  POSTINCREMENT("++", false, 2),
  PREINCREMENT("++", true, 3),
  POSTDECREMENT("--", false, 2),
  PREDECREMENT("--", true, 3);

  private String operator;
  boolean        prefix;
  int            precedence;

  private UnaryOperator ( final String operator, final boolean prefix, final int precedence )
  {
    this.operator = operator;
    this.prefix = prefix;
    this.precedence = precedence;
  }

  String getCode ()
  {
    return operator;
  }

  boolean isPrefix ()
  {
    return prefix;
  }

  /**
   * Determines the precedence for the operator. Precedence is determined
   * according to the table in C++ Programming Language (Third Edition)
   * Stroustrup, Section 6.2. See {@link Expression#getPrecedence()} for
   * details.
   * 
   * @return the precedence of the operator
   */
  int getPrecedence ()
  {
    return precedence;
  }

}
