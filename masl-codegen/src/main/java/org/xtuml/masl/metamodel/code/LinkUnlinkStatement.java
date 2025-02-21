/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.code;

import org.xtuml.masl.metamodel.expression.Expression;
import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;

public interface LinkUnlinkStatement extends Statement {

    enum Type {
        LINK, UNLINK
    }

    RelationshipSpecification getRelationship();

    Expression getLhs();

    Expression getRhs();

    Expression getAssoc();

    Type getLinkType();

    ObjectDeclaration getAssocObject();

    ObjectDeclaration getLhsObject();

    ObjectDeclaration getRhsObject();

}
