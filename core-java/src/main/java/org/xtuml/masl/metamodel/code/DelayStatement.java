//
// File: ExitStatement.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.code;

import org.xtuml.masl.metamodel.expression.Expression;


public interface DelayStatement
    extends Statement
{

  Expression getDuration ();

}
