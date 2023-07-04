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
package org.xtuml.masl.metamodelImpl.statemodel;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.expression.EventExpression;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;

public class TransitionOption extends Positioned implements org.xtuml.masl.metamodel.statemodel.TransitionOption {

    private final EventDeclaration event;
    private final TransitionType type;
    private final State destinationState;

    public static TransitionOption create(final EventExpression event,
                                          final ObjectDeclaration object,
                                          final TransitionType type,
                                          final String destinationState) {
        if (event == null || object == null || type == null) {
            return null;
        }

        try {
            return new TransitionOption(event,
                                        type,
                                        destinationState == null ? null : object.getState(destinationState));
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    private TransitionOption(final EventExpression event, final TransitionType type, final State destinationState) {
        super(event.getPosition());
        this.event = event.getEvent();
        this.type = type;
        this.destinationState = destinationState;
    }

    @Override
    public EventDeclaration getEvent() {
        return event;
    }

    @Override
    public org.xtuml.masl.metamodel.statemodel.TransitionType getType() {
        return type.getType();
    }

    @Override
    public State getDestinationState() {
        return destinationState;
    }

    @Override
    public String toString() {
        return event.getParentObject().getName() +
               "." +
               event.getName() +
               "\t=> " +
               (destinationState != null ? destinationState.getName() : type.toString());
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitTransitionOption(this, p);
    }

}
