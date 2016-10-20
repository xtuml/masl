//
// File: StructureElement.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.type;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.expression.Expression;


public interface StructureElement
    extends ASTNode
{

  PragmaList getPragmas ();

  String getName ();

  BasicType getType ();

  Expression getDefault ();

  String getComment ();
}
