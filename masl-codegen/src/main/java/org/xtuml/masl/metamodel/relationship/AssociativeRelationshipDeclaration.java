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

import org.xtuml.masl.metamodel.object.ObjectDeclaration;

public interface AssociativeRelationshipDeclaration extends RelationshipDeclaration {

    ObjectDeclaration getAssocObject();

    RelationshipSpecification getAssocToLeftSpec();

    RelationshipSpecification getAssocToRightSpec();

    boolean getLeftConditional();

    MultiplicityType getLeftMult();

    ObjectDeclaration getLeftObject();

    String getLeftRole();

    RelationshipSpecification getLeftToAssocSpec();

    RelationshipSpecification getLeftToRightSpec();

    boolean getRightConditional();

    MultiplicityType getRightMult();

    ObjectDeclaration getRightObject();

    String getRightRole();

    RelationshipSpecification getRightToAssocSpec();

    RelationshipSpecification getRightToLeftSpec();

}
