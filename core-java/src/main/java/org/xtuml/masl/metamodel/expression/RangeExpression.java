//
// File: RangeExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

public interface RangeExpression
    extends Expression
{

  Expression getMin ();

  Expression getMax ();
}
