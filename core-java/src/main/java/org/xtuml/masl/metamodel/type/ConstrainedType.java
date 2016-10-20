//
// File: ConstrainedType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.type;

public interface ConstrainedType
    extends TypeDefinition
{

  BasicType getFullType ();

  TypeConstraint getConstraint ();
}
