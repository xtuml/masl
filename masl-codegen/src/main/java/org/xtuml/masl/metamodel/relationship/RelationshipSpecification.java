/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.relationship;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;

public interface RelationshipSpecification extends ASTNode {

    MultiplicityType getCardinality();

    boolean getConditional();

    ObjectDeclaration getDestinationObject();

    ObjectDeclaration getFromObject();

    RelationshipDeclaration getRelationship();

    String getRole();

    boolean isToAssociative();

    boolean isFromAssociative();

    boolean isFormalisingEnd();

    RelationshipSpecification getReverseSpec();

    RelationshipSpecification getAssocSpec();

    RelationshipSpecification getNonAssocSpec();

}
