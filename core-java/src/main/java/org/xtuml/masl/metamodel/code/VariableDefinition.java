//
// File: VariableDefinition.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.code;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.expression.Expression;
import org.xtuml.masl.metamodel.type.BasicType;


public interface VariableDefinition
    extends ASTNode
{

  String getName ();

  BasicType getType ();

  boolean isReadonly ();

  int getLineNumber ();

  PragmaList getPragmas ();

  Expression getInitialValue ();
}
