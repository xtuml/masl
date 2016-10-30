//
// File: NullLiteral.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import org.xtuml.masl.metamodel.object.ObjectDeclaration;


public interface ThisLiteral
    extends LiteralExpression
{

  ObjectDeclaration getObject ();
}
