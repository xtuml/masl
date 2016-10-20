//
// File: BooleanLiteral.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import java.util.Date;


public interface TimestampLiteral
    extends LiteralExpression
{

  Date getValue ();

  int getNanos ();
}
