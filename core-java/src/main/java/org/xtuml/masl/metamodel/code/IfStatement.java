//
// File: IfStatement.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.code;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.expression.Expression;


public interface IfStatement
    extends Statement
{

  public interface Branch
      extends ASTNode
  {

    Expression getCondition ();

    List<? extends Statement> getStatements ();
  }

  List<? extends Branch> getBranches ();
}
