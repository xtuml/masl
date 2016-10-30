//
// File: RelationshipSpecification.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.relationship;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;


public interface RelationshipSpecification
    extends ASTNode
{

  MultiplicityType getCardinality ();

  boolean getConditional ();

  ObjectDeclaration getDestinationObject ();

  ObjectDeclaration getFromObject ();

  RelationshipDeclaration getRelationship ();

  String getRole ();

  boolean isToAssociative ();

  boolean isFromAssociative ();

  boolean isFormalisingEnd ();

  RelationshipSpecification getReverseSpec ();

  RelationshipSpecification getAssocSpec ();

  RelationshipSpecification getNonAssocSpec ();

}
