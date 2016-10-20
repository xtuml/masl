//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;

import org.xtuml.masl.javagen.ast.expr.Expression;


public interface While
    extends Statement
{

  Statement getStatement ();

  Expression getCondition ();

  void setStatement ( Statement statement );

  void setCondition ( Expression condition );

}
