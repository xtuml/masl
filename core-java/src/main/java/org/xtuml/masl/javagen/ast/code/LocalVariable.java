//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;

import org.xtuml.masl.javagen.ast.expr.Expression;


public interface LocalVariable
    extends Variable
{

  void setInitialValue ( Expression initialValue );

  Expression getInitialValue ();

  LocalVariableDeclaration asStatement ();

}
