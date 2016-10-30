//
// UK Crown Copyright (c) 2012. All Rights Reserved.
//
package org.xtuml.masl.metamodel;

import org.xtuml.masl.metamodel.expression.RangeExpression;
import org.xtuml.masl.metamodelImpl.expression.TypeNameExpression;


public interface CharacteristicRange
    extends RangeExpression
{

  TypeNameExpression getTypeName ();
}
