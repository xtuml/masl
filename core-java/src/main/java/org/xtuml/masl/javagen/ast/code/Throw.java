//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;

import org.xtuml.masl.javagen.ast.expr.Expression;


public interface Throw
    extends Statement
{

  Expression getThrownExpression ();

  void setThrownExpression ( Expression expression );
}
