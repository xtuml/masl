//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;

import org.xtuml.masl.javagen.ast.code.ExpressionStatement;


public interface StatementExpression
    extends Expression
{

  ExpressionStatement asStatement ();
}
