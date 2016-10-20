//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.cppgen;


/**
 * Encapsulates the operator for the enclosing expression.
 */
public enum BinaryOperator
{
  /**
   * Enumeration class for the associativity of the operator
   */

  /**
   * logical and operator: {@code a && b}
   */
  AND(" && ", 13),


  /**
   * simple assignment operator: {@code a = b}
   */
  ASSIGN(" = ", 16, Associativity.RIGHT),


  /**
   * bitwise and operator: {@code a & b}
   */
  BITAND(" & ", 10),


  /**
   * bitwise and and assign operator: {@code a &= b}
   */
  BITAND_ASSIGN(" &= ", 16, Associativity.RIGHT),

  /**
   * bitwise or operator: {@code a|b}
   */
  BITOR(" | ", 12),

  /**
   * bitwise or and assign operator: {@code a |= b}
   */
  BITOR_ASSIGN(" |= ", 16, Associativity.RIGHT),

  /**
   * bitwise xor operator: {@code a ^ b}
   */
  BITXOR(" ^ ", 11),

  /**
   * bitwise xor and assign operator: {@code a ^= b}
   */
  BITXOR_ASSIGN(" ^= ", 16, Associativity.RIGHT),


  /**
   * comma operator: {@code a, b}
   */
  COMMA(", ", 18),

  /**
   * divide operator: {@code a / b}
   */
  DIVIDE(" / ", 5),

  /**
   * divide and assign operator: {@code a /= b}
   */
  DIVIDE_ASSIGN(" /= ", 16, Associativity.RIGHT),

  /**
   * equal operator: {@code a == b}
   */
  EQUAL(" == ", 9),

  /**
   * greater than operator: {@code a > b}
   */
  GREATER_THAN(" > ", 8),

  /**
   * greater than or equal operator: {@code a >= b}
   */
  GREATER_THAN_OR_EQUAL(" >= ", 8),

  /**
   * left shift operator: {@code a << b}
   */
  LEFT_SHIFT(" << ", 7),

  /**
   * left shift and assign operator: {@code a <<= b}
   */
  LEFT_SHIFT_ASSIGN(" <<= ", 16, Associativity.RIGHT),


  /**
   * less than operator: {@code a < b}
   */
  LESS_THAN(" < ", 8),

  /**
   * less than or equal operator: {@code a <= b}
   */
  LESS_THAN_OR_EQUAL(" <= ", 8),

  /**
   * pointer to member selection operator: <code>code a.*b</code>
   */
  MEMBER_OBJ_REF(".*", 4),

  /**
   * pointer to member selection operator for pointer: {@code a->*b}
   */
  MEMBER_PTR_REF("->*", 4),

  /**
   * subtract operator: {@code a - b}
   */
  MINUS(" - ", 6),

  /**
   * subtract and assign operator: {@code a -= b}
   */
  MINUS_ASSIGN(" -= ", 16, Associativity.RIGHT),


  /**
   * not equal operator: <code>a != b</code>
   */
  NOT_EQUAL(" != ", 9),


  /**
   * member selection operator: {@code a.b}
   */
  OBJ_REF(".", 2),

  /**
   * member selection operator: {@code a.template b}
   */
  OBJ_REF_TEMPLATE(".template ", 2),


  /**
   * logical or operator: {@code a || b}
   */
  OR(" || ", 14),

  /**
   * add operator: {@code a + b}
   */
  PLUS(" + ", 6),

  /**
   * add and assign operator: {@code a += b}
   */
  PLUS_ASSIGN(" += ", 16, Associativity.RIGHT),

  /**
   * member selection operator for pointer: {@code a->b}
   */
  PTR_REF("->", 2),

  /**
   * member selection operator for pointer: {@code a->template b}
   */
  PTR_REF_TEMPLATE("->template ", 2),

  /**
   * modulo operator: {@code a % b}
   */
  REMAINDER(" % ", 5),

  /**
   * modulo and assign operator: {@code a %= b}
   */
  REMAINDER_ASSIGN(" %= ", 16, Associativity.RIGHT),

  /**
   * right shift operator: {@code a >> b}
   */
  RIGHT_SHIFT(" >> ", 7),

  /**
   * right shift and assign operator: {@code a >>= b}
   */
  RIGHT_SHIFT_ASSIGN(" >>= ", 16, Associativity.RIGHT),

  /**
   * multiply operator: {@code a * b}
   */

  TIMES(" * ", 5),

  /**
   * multiply and assign operator: {@code a *= b}
   */
  TIMES_ASSIGN(" *= ", 15, Associativity.RIGHT);

  /**
   * Constructs a left-associative operator
   * 

   *          The code for the operator

   *          The precedence for the operator when compared to other expressions
   */
  private BinaryOperator ( final String operator, final int precedence )
  {
    this(operator, precedence, Associativity.LEFT);
  }

  /**
   * Constructs an operator
   * 

   *          The code for the operator

   *          The precedence for the operator when compared to other expressions

   *          Whether the operator is left- or right-associative.
   */
  private BinaryOperator ( final String operator, final int precedence, final Associativity associativity )
  {
    this.operator = operator;
    this.precedence = precedence;
    this.associativity = associativity;
  }

  /**
   * Determines whether this operator is left or right-associative. Assignment
   * expressions are right associative, that is {@code a=b=c}is equivalent to
   * {@code a=(b=c)}. All other binary expressions are left-associative, that is
   * {@code a+b+c}is equivalent to {@code (a+b)+c}.
   * 
   * @return the associativity
   */
  Associativity getAssociativity ()
  {
    return associativity;
  }

  /**
   * Generates code for the operator
   * 
   * @return the code for this operator
   */
  String getCode ()
  {
    return operator;
  }

  /**
   * Determines the precedence for the operator. Precedence is determined
   * according to the table in C++ Programming Language (Third Edition)
   * Stroustrup, Section 6.2. See {@link Expression#getPrecedence()}for details.
   * 
   * @return the precedence of the operator
   */
  int getPrecedence ()
  {
    return precedence;
  }

  /**
   * The associativity of this operator
   */
  private Associativity associativity;

  static enum Associativity
  {
    LEFT, RIGHT;
  }


  /**
   * The text to put in the generated code for this operator
   */
  private String operator;
  /**
   * The precedence of this operator
   */
  private int    precedence;
}
