//
// File: ElementsExpression.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

public interface AnyExpression
    extends Expression
{

  Expression getCollection ();

  Expression getCount ();
}
