//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;

import java.util.Map;

import org.xtuml.masl.javagen.ast.expr.Expression;


public interface If
    extends Statement
{

  void setCondition ( Expression condition );

  void setThen ( Statement thenStatement );

  void setElse ( Statement elseStatement );

  Expression getCondition ();

  Statement getThen ();

  Statement getElse ();

  Map<? extends Expression, ? extends Statement> getIfElseChainStatements ();

}
