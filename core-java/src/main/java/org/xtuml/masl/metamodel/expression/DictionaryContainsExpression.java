//
// File: SliceExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

public interface DictionaryContainsExpression
    extends Expression
{

  Expression getPrefix ();

  Expression getKey ();
}
