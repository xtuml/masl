/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.object;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.metamodel.statemodel.TransitionTable;

import java.util.List;

public interface ObjectDeclaration extends ASTNode {

    List<? extends AttributeDeclaration> getAttributes();

    PragmaList getDeclarationPragmas();

    PragmaList getDefinitionPragmas();

    Domain getDomain();

    List<? extends EventDeclaration> getEvents();

    List<? extends EventDeclaration> getAllEvents();

    IdentifierDeclaration getPreferredIdentifier();

    List<? extends IdentifierDeclaration> getIdentifiers();

    String getName();

    List<? extends RelationshipSpecification> getRelationships();

    List<? extends ObjectService> getServices();

    List<? extends State> getStates();

    boolean hasCurrentState();

    boolean hasAssignerState();

    TransitionTable getStateMachine();

    TransitionTable getAssignerStateMachine();

    List<? extends ObjectDeclaration> getSupertypes();

}
