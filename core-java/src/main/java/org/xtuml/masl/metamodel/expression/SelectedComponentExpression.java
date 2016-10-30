//
// File: SelectedComponentExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import org.xtuml.masl.metamodel.type.StructureElement;


public interface SelectedComponentExpression
    extends Expression
{

  Expression getPrefix ();

  StructureElement getComponent ();

}
