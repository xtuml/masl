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
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.name.NameLookup;
import org.xtuml.masl.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CodeBlock extends Statement implements org.xtuml.masl.metamodel.code.CodeBlock {

    public CodeBlock(final Position position, final boolean topLevel) {
        super(position);
        this.topLevel = topLevel;
        this.variables = new ArrayList<VariableDefinition>();
        this.statements = new ArrayList<Statement>();
        this.exceptionHandlers = new ArrayList<ExceptionHandler>();
    }

    public void addExceptionHandler(final ExceptionHandler handler) {
        if (handler != null) {
            exceptionHandlers.add(handler);
            handler.getCode().forEach(s -> s.setParentStatement(this));
        }
    }

    public void addStatement(final Statement statement) {
        if (statement != null) {
            statements.add(statement);
            statement.setParentStatement(this);
        }
    }

    public void addVariableDefinition(final VariableDefinition variable) {
        try {
            if (variable != null) {
                nameLookup.addName(variable);
                variables.add(variable);
            }
        } catch (final SemanticError e) {
            e.report();
        }
    }

    @Override
    public List<ExceptionHandler> getExceptionHandlers() {
        return Collections.unmodifiableList(exceptionHandlers);
    }

    @Override
    public List<Statement> getStatements() {
        return Collections.unmodifiableList(statements);
    }

    @Override
    public List<Statement> getChildStatements() {
        final List<Statement> result = new ArrayList<Statement>();
        result.addAll(statements);
        for (final ExceptionHandler handler : exceptionHandlers) {
            result.addAll(handler.getCode());
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public List<VariableDefinition> getVariables() {
        return Collections.unmodifiableList(variables);
    }

    @Override
    public String toAbbreviatedString() {
        return (variables.size() > 0 && !topLevel ? "declare ...\n" : "") +
               "begin ...\n" +
               (exceptionHandlers.size() > 0 ? "exception ...\n" : "") +
               "end;";
    }

    @Override
    public String toString() {
        return (variables.size() > 0 && !topLevel ? "declare\n" : "") +
               TextUtils.indentText("  ", TextUtils.formatList(variables, "", "", "\n", "", "")) +
               "begin\n" +
               TextUtils.indentText("  ", TextUtils.formatList(statements, "", "", "\n", "", "")) +
               (exceptionHandlers.size() > 0 ? "exception\n" : "") +
               TextUtils.indentText("  ", TextUtils.formatList(exceptionHandlers, "", "", "\n", "", "")) +
               "end;";
    }

    public NameLookup getNameLookup() {
        return nameLookup;
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitCodeBlock(this, p);
    }

    private final NameLookup nameLookup = new NameLookup();

    private final boolean topLevel;

    private final List<ExceptionHandler> exceptionHandlers;

    private final List<Statement> statements;

    private final List<VariableDefinition> variables;

}
