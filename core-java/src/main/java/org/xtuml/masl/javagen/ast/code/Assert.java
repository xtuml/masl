//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;

import org.xtuml.masl.javagen.ast.expr.Expression;


public interface Assert
    extends Statement
{

  void setCondition ( final Expression condition );

  void setMessage ( final Expression message );

  Expression getCondition ();

  Expression getMessage ();
}
