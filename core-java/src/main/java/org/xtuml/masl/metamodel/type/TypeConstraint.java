//
// File: TypeConstraint.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.type;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.expression.RangeExpression;


public interface TypeConstraint
    extends ASTNode
{

  RangeExpression getRange ();
}
