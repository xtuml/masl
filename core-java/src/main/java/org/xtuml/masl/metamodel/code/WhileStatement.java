//
// File: WhileStatement.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.code;

import java.util.List;

import org.xtuml.masl.metamodel.expression.Expression;


public interface WhileStatement
    extends Statement
{

  Expression getCondition ();

  List<? extends Statement> getStatements ();
}
