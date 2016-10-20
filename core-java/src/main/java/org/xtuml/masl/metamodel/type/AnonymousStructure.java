//
// File: AnonymousStructure.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.metamodel.type;

import java.util.List;


public interface AnonymousStructure
    extends BasicType
{

  List<? extends BasicType> getElements ();

}
