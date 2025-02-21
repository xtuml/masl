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

import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.PragmaList;

import java.util.Collections;
import java.util.List;

public abstract class Statement extends Positioned implements org.xtuml.masl.metamodel.code.Statement {

    private Statement parent;
    private PragmaList pragmas;

    public Statement(final Position position) {
        super(position);
        parent = null;
    }

    public void setParentStatement(final Statement parent) {
        this.parent = parent;
    }

    @Override
    public Statement getParentStatement() {
        return parent;
    }

    public void setPragmas(final PragmaList pragmas) {
        this.pragmas = pragmas;
    }

    @Override
    public PragmaList getPragmas() {
        return pragmas;
    }

    @Override
    public int getLineNumber() {
        return getPosition() == null ? 0 : getPosition().getLineNumber();
    }

    @Override
    public String toAbbreviatedString() {
        return toString();
    }

    @Override
    public List<Statement> getChildStatements() {
        return Collections.emptyList();
    }

    @Override
    public boolean inExceptionHandler() {
        final Statement parent = getParentStatement();
        if (parent != null) {
            if (parent instanceof CodeBlock block) {
                // check if this statement is contained in any of the codeblock handlers
                return block.getExceptionHandlers().stream().flatMap(h -> h.getCode().stream()).anyMatch(this::equals);
            } else {
                return parent.inExceptionHandler();
            }
        } else {
            return false;
        }
    }

}
