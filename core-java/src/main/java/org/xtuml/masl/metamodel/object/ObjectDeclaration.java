/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
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
