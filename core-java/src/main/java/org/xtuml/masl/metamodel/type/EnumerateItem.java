//
// File: EnumerateItem.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.type;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodelImpl.expression.Expression;


public interface EnumerateItem
    extends ASTNode
{

  EnumerateType getEnumerate ();

  String getName ();

  Expression getValue ();

  String getComment ();
}
