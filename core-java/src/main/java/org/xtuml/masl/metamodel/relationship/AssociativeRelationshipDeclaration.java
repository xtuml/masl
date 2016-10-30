//
// File: NormalRelationshipDeclaration.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.relationship;

import org.xtuml.masl.metamodel.object.ObjectDeclaration;


public interface AssociativeRelationshipDeclaration
    extends RelationshipDeclaration
{

  ObjectDeclaration getAssocObject ();

  RelationshipSpecification getAssocToLeftSpec ();

  RelationshipSpecification getAssocToRightSpec ();

  boolean getLeftConditional ();

  MultiplicityType getLeftMult ();

  ObjectDeclaration getLeftObject ();

  String getLeftRole ();

  RelationshipSpecification getLeftToAssocSpec ();

  RelationshipSpecification getLeftToRightSpec ();

  boolean getRightConditional ();

  MultiplicityType getRightMult ();

  ObjectDeclaration getRightObject ();

  String getRightRole ();

  RelationshipSpecification getRightToAssocSpec ();

  RelationshipSpecification getRightToLeftSpec ();

}
