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
package org.xtuml.masl.metamodelImpl.statemodel;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;

import java.util.Collections;
import java.util.List;

public class TransitionTable extends Positioned implements org.xtuml.masl.metamodel.statemodel.TransitionTable {

    private final boolean isAssigner;
    private final List<TransitionRow> rows;
    private final PragmaList pragmas;

    public static void create(final Position position,
                              final ObjectDeclaration object,
                              final boolean isAssigner,
                              final List<TransitionRow> rows,
                              final PragmaList pragmas) {
        if (object == null) {
            return;
        }

        try {
            object.addTransitionTable(new TransitionTable(position, object, isAssigner, rows, pragmas));
        } catch (final SemanticError e) {
            e.report();
        }

    }

    private TransitionTable(final Position position,
                            final ObjectDeclaration object,
                            final boolean isAssigner,
                            final List<TransitionRow> rows,
                            final PragmaList pragmas) throws SemanticError {
        super(position);
        this.pragmas = pragmas;
        this.isAssigner = isAssigner;
        this.rows = rows;
        this.parentObject = object;

        for (final State state : object.getStates()) {
            boolean found = false;
            for (final TransitionRow row : rows) {
                if (row.getInitialState() == state) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new SemanticError(SemanticErrorCode.NoRowForState, position, state.getName());
            }
        }
    }

    @Override
    public PragmaList getPragmas() {
        return pragmas;
    }

    @Override
    public boolean isAssigner() {
        return isAssigner;
    }

    @Override
    public List<TransitionRow> getRows() {
        return Collections.unmodifiableList(rows);
    }

    private final ObjectDeclaration parentObject;

    public ObjectDeclaration getParentObject() {
        return parentObject;
    }

    @Override
    public String toString() {
        return (isAssigner ? "assigner " : "") +
               "transition is\n" +
               org.xtuml.masl.utils.TextUtils.indentText("  ",
                                                         org.xtuml.masl.utils.TextUtils.formatList(rows,
                                                                                                   "",
                                                                                                   "\n",
                                                                                                   "")) +
               "end transition;\n" +
               pragmas;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitTransitionTable(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(rows, pragmas);
    }

}
