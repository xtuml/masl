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
