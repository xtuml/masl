//
// File: EventExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import org.xtuml.masl.metamodel.statemodel.EventDeclaration;


public interface EventExpression
    extends Expression
{

  EventDeclaration getEvent ();
}
