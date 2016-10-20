//
// UK Crown Copyright (c) 2012. All Rights Reserved.
//
package org.xtuml.masl.metamodel;

import org.xtuml.masl.metamodel.expression.Expression;
import org.xtuml.masl.metamodel.type.BasicType;


public interface TypeNameExpression
    extends Expression
{

  public BasicType getReferencedType ();

}
