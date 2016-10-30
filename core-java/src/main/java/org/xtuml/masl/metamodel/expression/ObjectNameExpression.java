//
// File: ParameterNameExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import org.xtuml.masl.metamodel.object.ObjectDeclaration;


public interface ObjectNameExpression
    extends Expression
{

  ObjectDeclaration getObject ();
}
