//
// File: RaiseStatement.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.code;

import org.xtuml.masl.metamodel.exception.ExceptionReference;
import org.xtuml.masl.metamodel.expression.Expression;


public interface RaiseStatement
    extends Statement
{

  ExceptionReference getException ();

  Expression getMessage ();
}
