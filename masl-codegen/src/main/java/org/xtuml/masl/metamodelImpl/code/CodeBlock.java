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
        this.variables = new ArrayList<>();
        this.statements = new ArrayList<>();
        this.exceptionHandlers = new ArrayList<>();
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
        final List<Statement> result = new ArrayList<>();
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
    public void accept(final ASTNodeVisitor v) {
        v.visitCodeBlock(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(variables, statements, exceptionHandlers);
    }

    private final NameLookup nameLookup = new NameLookup();

    private final boolean topLevel;

    private final List<ExceptionHandler> exceptionHandlers;

    private final List<Statement> statements;

    private final List<VariableDefinition> variables;

}
