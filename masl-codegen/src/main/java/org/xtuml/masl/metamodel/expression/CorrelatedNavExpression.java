/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.expression;

import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;

public interface CorrelatedNavExpression extends Expression {

    Expression getLhs();

    Expression getRhs();

    RelationshipSpecification getRelationship();

}
