/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.statemodel;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.CheckedLookup;
import org.xtuml.masl.metamodelImpl.common.ParameterDefinition;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.error.AlreadyDefined;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.EventExpression;
import org.xtuml.masl.metamodelImpl.name.Name;
import org.xtuml.masl.metamodelImpl.name.Named;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventDeclaration extends Name implements org.xtuml.masl.metamodel.statemodel.EventDeclaration, Named {

    public static void create(final Position position,
                              final ObjectDeclaration object,
                              final String name,
                              final EventType type,
                              final List<ParameterDefinition> parameters,
                              final PragmaList pragmas) {
        if (object == null || name == null || type == null) {
            return;
        }

        try {
            object.addEvent(new EventDeclaration(position, object, name, type, parameters, pragmas));
        } catch (final SemanticError e) {
            e.report();
        }

    }

    private EventDeclaration(final Position position,
                             final ObjectDeclaration object,
                             final String name,
                             final EventType type,
                             final List<ParameterDefinition> parameters,
                             final PragmaList pragmas) {
        super(position, name);
        this.parentObject = object;
        this.type = type;
        this.params =
                new CheckedLookup<>(SemanticErrorCode.ParameterAlreadyDefinedOnEvent,
                                    SemanticErrorCode.ParameterNotFoundOnEvent,
                                    this);
        for (final ParameterDefinition param : parameters) {
            try {
                if (param != null) {
                    addParameter(param);
                }
            } catch (final AlreadyDefined e) {
                // Report error, and ignore parameter
                e.report();
            }
        }
        this.pragmas = pragmas;
    }

    public void addParameter(final ParameterDefinition param) throws AlreadyDefined {
        params.put(param.getName(), param);
        signature.add(param.getType());
    }

    public List<BasicType> getSignature() {
        return signature;
    }

    private final List<BasicType> signature = new ArrayList<>();

    @Override
    public List<ParameterDefinition> getParameters() {
        return Collections.unmodifiableList(params.asList());
    }

    @Override
    public ObjectDeclaration getParentObject() {
        return parentObject;
    }

    @Override
    public PragmaList getPragmas() {
        return pragmas;
    }

    @Override
    public Type getType() {
        return type.getType();
    }

    @Override
    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled() {
        isScheduled = true;
    }

    @Override
    public String toString() {
        final String title = type + (type == EventType.NORMAL ? "" : " ") + "event\t" + getName() + "\t(\t";
        return title + TextUtils.formatList(params.asList(), "", ",\n\t\t\t", "") + " );\n" + pragmas;
    }

    private boolean isScheduled = false;

    private final EventType type;

    private final CheckedLookup<ParameterDefinition> params;

    private final PragmaList pragmas;

    private final ObjectDeclaration parentObject;

    @Override
    public EventExpression getReference(final Position position) {
        return new EventExpression(position, this);
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitEventDeclaration(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(params.asList(), pragmas);
    }

}
