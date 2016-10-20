//
// File: ReferentialAttributeDefinition.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.object;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;


public interface ReferentialAttributeDefinition
    extends ASTNode
{

  RelationshipSpecification getRelationship ();

  AttributeDeclaration getDestinationAttribute ();

}
