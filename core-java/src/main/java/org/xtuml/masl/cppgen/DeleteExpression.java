//
// File: NewExpression.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.cppgen;


/**
 * Writes a C++ <code>delete</code> or <code>delete[]</code> expression
 */
public class DeleteExpression extends Expression
{

  /**
   * Creates a non-array delete expression
   * 

   *          an expression returning a pointer to the object to delete
   */
  public DeleteExpression ( final Expression expression )
  {
    this(expression, false);
  }

  /**
   * Creates a delete expression
   * 

   *          an expression returning a pointer to the object to delete

   *          should be true if the expression is a pointer to an array
   */
  public DeleteExpression ( final Expression expression, final boolean array )
  {
    this.expression = expression;
    this.array = array;
  }

  @Override
  int getPrecedence ()
  {
    return 3;
  }

  @Override
  String getCode ( final Namespace currentNamespace, final String alignment )
  {
    return "delete" + (array ? "[]" : "") + " " + expression.getCode(currentNamespace, alignment);
  }

  /**
   * Flag to indicate whether this is an array deletion or not.
   */
  private final boolean    array;

  /**
   * An expression returning a pointer to the object to delete
   */
  private final Expression expression;

}
