//
// File: ArrayAccess.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.cppgen;


/**
 * Represents a C++ array access. An array access is an expression that returns
 * the value of an element of an array at a particular index. For example
 * {@code a[4]}will return the fourth element of array {@code a}.
 */
public class ArrayAccess extends Expression
{

  /**
   * Creates an array access object.
   * 

   *          the array to access

   *          the index of the required element
   */
  public ArrayAccess ( final Expression name, final Expression index )
  {
    this.nameExpression = name;
    this.indexExpression = index;
  }

  @Override
  String getCode ( final Namespace currentNamespace, final String alignment )
  {
    String name = nameExpression.getCode(currentNamespace, alignment);
    final String index = indexExpression.getCode(currentNamespace, alignment);

    if ( getPrecedence() < nameExpression.getPrecedence() )
    {
      name = "(" + name + ")";
    }

    return name + "[" + index + "]";
  }

  @Override
  int getPrecedence ()
  {
    return 2;
  }

  @Override
  boolean isTemplateType ()
  {
    return nameExpression.isTemplateType();
  }

  /**
   * The index or the required element
   */
  private final Expression indexExpression;

  /**
   * The array to index into
   */
  private final Expression nameExpression;

}
