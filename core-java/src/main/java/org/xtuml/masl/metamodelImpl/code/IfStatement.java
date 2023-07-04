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

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.type.BooleanType;
import org.xtuml.masl.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IfStatement extends Statement implements org.xtuml.masl.metamodel.code.IfStatement {

    public static Branch createIfBranch(final Position position,
                                        final Expression condition,
                                        final List<Statement> statements) {
        if (condition == null || statements == null) {
            return null;
        }

        try {
            return new Branch(position, condition, statements);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public static Branch createElseBranch(final Position position, final List<Statement> statements) {
        if (statements == null) {
            return null;
        }

        try {
            return new Branch(position, null, statements);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    static public class Branch extends Positioned implements org.xtuml.masl.metamodel.code.IfStatement.Branch {

        private final Expression condition;
        private final List<Statement> statements;

        public Branch(final Position position, final Expression condition, final List<Statement> statements) throws
                                                                                                             SemanticError {
            super(position);
            this.condition = condition;
            this.statements = statements;

            if (condition != null && !BooleanType.createAnonymous().isAssignableFrom(condition)) {
                throw new SemanticError(SemanticErrorCode.ExpectedBooleanCondition,
                                        condition.getPosition(),
                                        condition.getType());
            }
        }

        @Override
        public Expression getCondition() {
            return this.condition;
        }

        @Override
        public List<Statement> getStatements() {
            return Collections.unmodifiableList(statements);
        }

        @Override
        public String toString() {
            final String condCode = (condition == null ? "e " : "if " + condition + " then");

            return condCode + "\n" + TextUtils.indentText("  ", TextUtils.formatList(statements, "", "", "\n", "", ""));
        }

        public String toAbbreviatedString() {
            if (condition != null) {
                return "if " + condition + " then ...";
            } else {
                return "e ...";
            }
        }

        @Override
        public void accept(final ASTNodeVisitor v) {
            v.visitIfBranch(this);
        }

        @Override
        public List<ASTNode> children() {
            return ASTNode.makeChildren(condition, statements);
        }

    }

    private final List<Branch> branches;

    public static IfStatement create(final Position position, final List<Branch> branches) {
        return new IfStatement(position, branches);
    }

    private IfStatement(final Position position, final List<Branch> branches) {
        super(position);
        this.branches = branches;
        branches.stream().flatMap(b -> b.getStatements().stream()).forEach(s -> s.setParentStatement(this));
    }

    @Override
    public List<Branch> getBranches() {
        return Collections.unmodifiableList(branches);
    }

    @Override
    public List<Statement> getChildStatements() {
        final List<Statement> result = new ArrayList<>();
        for (final Branch branch : branches) {
            result.addAll(branch.getStatements());
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public String toString() {
        return TextUtils.formatList(branches, "", "els", "end if;");
    }

    @Override
    public String toAbbreviatedString() {
        final List<String> branchAbbrev = new ArrayList<>();
        for (final Branch branch : branches) {
            branchAbbrev.add(branch.toAbbreviatedString());
        }
        return TextUtils.formatList(branchAbbrev, "", "\nels", "");
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitIfStatement(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(branches);
    }

}
