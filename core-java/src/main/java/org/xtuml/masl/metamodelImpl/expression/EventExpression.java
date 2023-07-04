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
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.statemodel.EventDeclaration;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.EventType;

import java.util.List;

public class EventExpression extends Expression implements org.xtuml.masl.metamodel.expression.EventExpression {

    public static EventExpression create(final ObjectNameExpression objectName, final String eventName) {
        if (eventName == null) {
            return null;
        }

        try {
            if (objectName == null) {
                throw new SemanticError(SemanticErrorCode.NoObjectForEvent, Position.getPosition(eventName), eventName);
            }
            final Position
                    position =
                    objectName.getPosition() == null ? Position.getPosition(eventName) : objectName.getPosition();
            return objectName.getObject().getEvent(eventName).getReference(position);

        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public EventExpression(final Position position, final EventDeclaration event) {
        super(position);
        this.event = event;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EventExpression obj2)) {
            return false;
        } else {

            return event.equals(obj2.event);
        }
    }

    @Override
    public EventDeclaration getEvent() {
        return event;
    }

    @Override
    public BasicType getType() {
        return EventType.createAnonymous();
    }

    @Override
    public int hashCode() {
        return event.hashCode();
    }

    @Override
    public String toString() {
        return event.getParentObject().getName() + "." + event.getName();
    }

    private final EventDeclaration event;

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitEventExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren();
    }

}
