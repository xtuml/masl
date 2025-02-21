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

public interface NormalRelationshipDeclaration extends RelationshipDeclaration {

    RelationshipSpecification getLeftToRightSpec();

    RelationshipSpecification getRightToLeftSpec();

    ObjectDeclaration getLeftObject();

    String getRightRole();

    MultiplicityType getRightMult();

    boolean getRightConditional();

    ObjectDeclaration getRightObject();

    String getLeftRole();

    MultiplicityType getLeftMult();

    boolean getLeftConditional();

}
