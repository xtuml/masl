//
// File: GenerateStatement.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.code;

import java.util.List;

import org.xtuml.masl.metamodel.expression.Expression;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;


public interface GenerateStatement
    extends Statement
{

  List<? extends Expression> getArguments ();

  EventDeclaration getEvent ();

  Expression getToInstance ();
}
