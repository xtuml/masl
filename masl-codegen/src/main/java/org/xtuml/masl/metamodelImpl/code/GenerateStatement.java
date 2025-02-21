/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.ParameterDefinition;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.EventExpression;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.statemodel.EventDeclaration;
import org.xtuml.masl.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GenerateStatement extends Statement implements org.xtuml.masl.metamodel.code.GenerateStatement {

    public static GenerateStatement create(final Position position,
                                           final EventExpression event,
                                           final List<Expression> arguments,
                                           final Expression toInstance) {
        if (event == null || arguments == null) {
            return null;
        }

        try {
            return new GenerateStatement(position, event, arguments, toInstance);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    private GenerateStatement(final Position position,
                              final EventExpression eventRef,
                              final List<Expression> arguments,
                              final Expression toInstance) throws SemanticError {
        super(position);
        this.event = eventRef.getEvent();

        if (event.getType() == EventDeclaration.Type.NORMAL && toInstance == null) {
            throw new SemanticError(SemanticErrorCode.NoDestinationInstance, position, event.getName());
        } else if (toInstance != null) {

            switch (event.getType()) {
                case NORMAL:
                    final ObjectDeclaration destObject = ObjectDeclaration.getObject(toInstance, false);

                    if (!(destObject.canReceive(event))) {
                        throw new SemanticError(SemanticErrorCode.InvalidEventDestination,
                                                toInstance.getPosition(),
                                                destObject.getName());
                    }

                    break;
                case CREATION:
                    throw new SemanticError(SemanticErrorCode.IgnoringCreationDestinationInstance, event.getPosition());
                case ASSIGNER:
                    throw new SemanticError(SemanticErrorCode.IgnoringAssignerDestinationInstance, event.getPosition());
                default:
                    assert false : "Unrecognised event type : " + event.getType();
            }
        }

        this.arguments = new ArrayList<>();

        if (arguments.size() != event.getParameters().size()) {
            throw new SemanticError(SemanticErrorCode.NumberEventArgsIncorrect,
                                    eventRef.getPosition(),
                                    event.getParameters(),
                                    arguments.size());
        }

        final Iterator<Expression> argIt = arguments.iterator();
        for (final ParameterDefinition param : event.getParameters()) {
            final Expression arg = argIt.next().resolve(param.getType());

            param.getType().checkAssignable(arg);

            this.arguments.add(arg);
        }

        this.toInstance = toInstance;
    }

    @Override
    public List<Expression> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public EventDeclaration getEvent() {
        return event;
    }

    @Override
    public Expression getToInstance() {
        return this.toInstance;
    }

    private final EventDeclaration event;
    private final List<Expression> arguments;
    private final Expression toInstance;

    @Override
    public String toString() {
        return "generate " +
               event.getParentObject().getName() +
               "." +
               event.getName() +
               " (" +
               TextUtils.formatList(arguments, "", ", ", "") +
               ")" +
               (toInstance == null ? "" : " to " + toInstance) +
               ";";

    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitGenerateStatement(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(toInstance, arguments);
    }

}
