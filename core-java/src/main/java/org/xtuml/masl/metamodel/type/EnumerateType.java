//
// File: EnumerateType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.type;

import java.util.List;


public interface EnumerateType
    extends TypeDefinition
{

  List<? extends EnumerateItem> getItems ();

  UserDefinedType getUserDefinedType ();
}
