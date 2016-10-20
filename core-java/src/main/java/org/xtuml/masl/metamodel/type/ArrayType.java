//
// File: ArrayType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.type;

import org.xtuml.masl.metamodel.expression.RangeExpression;


public interface ArrayType
    extends CollectionType
{

  RangeExpression getRange ();
}
