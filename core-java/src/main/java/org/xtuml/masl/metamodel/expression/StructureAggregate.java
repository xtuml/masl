//
// File: StructureAggregate.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import java.util.List;


public interface StructureAggregate
    extends Expression
{

  List<? extends Expression> getElements ();
}
