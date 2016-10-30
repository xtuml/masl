//
// File: SubtypeRelationshipDeclaration.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.relationship;

import java.util.List;

import org.xtuml.masl.metamodel.object.ObjectDeclaration;


public interface SubtypeRelationshipDeclaration
    extends RelationshipDeclaration
{

  RelationshipSpecification getSubToSuperSpec ( ObjectDeclaration subtype );

  RelationshipSpecification getSuperToSubSpec ( ObjectDeclaration subtype );

  ObjectDeclaration getSupertype ();

  List<? extends ObjectDeclaration> getSubtypes ();

}
