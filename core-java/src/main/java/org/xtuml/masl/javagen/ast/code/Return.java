//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;

import org.xtuml.masl.javagen.ast.expr.Expression;


public interface Return
    extends Statement
{

  Expression getReturnValue ();

  void setReturnValue ( Expression returnValue );

}
