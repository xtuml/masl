//
// File: BooleanLiteral.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import org.xtuml.masl.metamodel.type.EnumerateItem;


public interface EnumerateLiteral
    extends LiteralExpression
{

  EnumerateItem getValue ();

  int getIndex ();
}
