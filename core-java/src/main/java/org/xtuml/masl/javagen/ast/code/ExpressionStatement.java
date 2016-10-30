//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;

import org.xtuml.masl.javagen.ast.expr.StatementExpression;


public interface ExpressionStatement
    extends Statement
{

  void setExpression ( StatementExpression expression );

  StatementExpression getExpression ();

}
