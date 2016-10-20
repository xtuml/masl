//
// File: NormalRelationshipDeclaration.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.relationship;

import org.xtuml.masl.metamodel.object.ObjectDeclaration;


public interface NormalRelationshipDeclaration
    extends RelationshipDeclaration
{

  RelationshipSpecification getLeftToRightSpec ();

  RelationshipSpecification getRightToLeftSpec ();

  ObjectDeclaration getLeftObject ();

  String getRightRole ();

  MultiplicityType getRightMult ();

  boolean getRightConditional ();

  ObjectDeclaration getRightObject ();

  String getLeftRole ();

  MultiplicityType getLeftMult ();

  boolean getLeftConditional ();

}
