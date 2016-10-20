//
// File: SliceExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

public interface DictionaryAccessExpression
    extends Expression
{

  Expression getPrefix ();

  Expression getKey ();
}
