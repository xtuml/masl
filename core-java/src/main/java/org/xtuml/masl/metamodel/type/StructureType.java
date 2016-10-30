//
// File: StructureType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.type;

import java.util.List;


public interface StructureType
    extends TypeDefinition
{

  List<? extends StructureElement> getElements ();

  UserDefinedType getUserDefinedType ();
}
