/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
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
