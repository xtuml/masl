//
// File: CollectionType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.type;


public interface CollectionType
    extends BasicType
{

  BasicType getContainedType ();
}
