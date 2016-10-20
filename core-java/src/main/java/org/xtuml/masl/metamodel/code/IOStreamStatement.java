//
// File: IOStreamStatement.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.code;

import java.util.List;

import org.xtuml.masl.metamodel.expression.Expression;


public interface IOStreamStatement
    extends Statement
{

  enum Type
  {
    IN, OUT, LINE_IN, LINE_OUT
  }

  interface IOExpression
  {

    Type getType ();

    Expression getExpression ();
  }

  Expression getStreamName ();

  List<? extends IOExpression> getArguments ();
}
