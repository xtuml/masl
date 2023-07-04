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
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.expression.VariableNameExpression;
import org.xtuml.masl.metamodelImpl.name.Name;
import org.xtuml.masl.metamodelImpl.type.BasicType;

public class VariableDefinition extends Name implements org.xtuml.masl.metamodel.code.VariableDefinition {

    public static VariableDefinition create(final String name,
                                            final BasicType type,
                                            final boolean readonly,
                                            final Expression initialValue,
                                            final PragmaList pragmas) {
        if (name == null || type == null) {
            return null;
        }
        return new VariableDefinition(name, type, readonly, initialValue, pragmas);
    }

    private final Expression initialValue;
    private final boolean readonly;
    private final BasicType type;
    private final PragmaList pragmas;

    public VariableDefinition(final String name,
                              final BasicType type,
                              final boolean readonly,
                              Expression initialValue,
                              final PragmaList pragmas) {
        super(name);
        this.pragmas = pragmas;
        this.type = type;
        this.readonly = readonly;

        if (initialValue != null) {
            try {
                type.checkAssignable(initialValue);
            } catch (final SemanticError e) {
                e.report();
                initialValue = null;
            }
        }

        this.initialValue = initialValue;
    }

    @Override
    public PragmaList getPragmas() {
        return pragmas;
    }

    @Override
    public Expression getInitialValue() {
        return this.initialValue;
    }

    @Override
    public BasicType getType() {
        return this.type;
    }

    @Override
    public boolean isReadonly() {
        return this.readonly;
    }

    @Override
    public String toString() {
        return getName() +
               " : " +
               (readonly ? "readonly " : "") +
               type +
               (initialValue == null ? "" : " := " + initialValue) +
               ";";
    }

    @Override
    public int getLineNumber() {
        return getPosition().getLineNumber();
    }

    @Override
    public VariableNameExpression getReference(final Position position) {
        return new VariableNameExpression(position, this);
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitVariableDefinition(this, p);
    }

}
