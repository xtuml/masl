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
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.exception.ExceptionReference;
import org.xtuml.masl.metamodelImpl.name.NameLookup;
import org.xtuml.masl.metamodelImpl.type.StringType;
import org.xtuml.masl.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ExceptionHandler extends Positioned implements org.xtuml.masl.metamodel.code.ExceptionHandler {

    public static ExceptionHandler create(final ExceptionReference ref, final String messageVariable) {
        if (ref == null) {
            return null;
        }

        try {
            return new ExceptionHandler(ref.getPosition(), ref, messageVariable);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public static ExceptionHandler create(final Position pos, final String messageVariable) {
        if (pos == null) {
            return null;
        }

        try {
            return new ExceptionHandler(pos, null, messageVariable);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    private final List<Statement> code;
    private final ExceptionReference exception;
    private final String messageVariable;
    private final VariableDefinition messageVarDef;
    private final NameLookup nameLookup = new NameLookup();

    public ExceptionHandler(final Position position,
                            final ExceptionReference exception,
                            final String messageVariable) throws SemanticError {
        super(position);
        this.exception = exception;
        this.code = new ArrayList<Statement>();
        this.messageVariable = messageVariable;
        if (this.messageVariable != null) {
            this.messageVarDef =
                    new VariableDefinition(this.messageVariable,
                                           StringType.createAnonymous(),
                                           true,
                                           null,
                                           new PragmaList());
            nameLookup.addName(this.messageVarDef);
        } else {
            this.messageVarDef = null;
        }
    }

    @Override
    public int getLineNumber() {
        return getPosition().getLineNumber();
    }

    public NameLookup getNameLookup() {
        return nameLookup;
    }

    public void addStatement(final Statement statement) {
        if (statement != null) {
            code.add(statement);
        }
    }

    @Override
    public List<Statement> getCode() {
        return Collections.unmodifiableList(code);
    }

    @Override
    public ExceptionReference getException() {
        return this.exception;
    }

    @Override
    public String getMessageVariable() {
        return this.messageVariable;
    }

    @Override
    public VariableDefinition getMessageVarDef() {
        return this.messageVarDef;
    }

    @Override
    public String toString() {
        return "when " +
               (exception == null ? "others" : exception.toString()) +
               (messageVariable != null ? "with " + messageVariable : "") +
               " =>\n" +
               TextUtils.indentText("  ", TextUtils.formatList(code, "", "\n", ""));
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitExceptionHandler(this, p);
    }

}
