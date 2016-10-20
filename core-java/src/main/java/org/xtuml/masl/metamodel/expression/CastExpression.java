//
// File: CastExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import org.xtuml.masl.metamodel.TypeNameExpression;


public interface CastExpression
    extends Expression
{

  Expression getRhs ();

  TypeNameExpression getTypeName ();

}
