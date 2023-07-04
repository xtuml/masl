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
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class CaseStatement extends Statement implements org.xtuml.masl.metamodel.code.CaseStatement {

    public static Alternative createAlternative(final Position position,
                                                final List<Expression> conditions,
                                                final List<Statement> statements) {
        return new Alternative(position, conditions, statements);
    }

    public static Alternative createOther(final Position position, final List<Statement> statements) {
        return new Alternative(position, null, statements);
    }

    public static class Alternative extends Positioned
            implements org.xtuml.masl.metamodel.code.CaseStatement.Alternative {

        private final List<Expression> conditions;
        private final List<Statement> statements;

        private Alternative(final Position position,
                            final List<Expression> conditions,
                            final List<Statement> statements) {
            super(position);
            this.conditions = conditions;
            this.statements = statements;
        }

        @Override
        public List<Expression> getConditions() {
            return conditions == null ? null : Collections.unmodifiableList(conditions);
        }

        @Override
        public List<Statement> getStatements() {
            return Collections.unmodifiableList(statements);
        }

        public String toAbbreviatedString() {
            if (conditions == null) {
                return "\n  when others => ...";
            } else {
                return TextUtils.formatList(conditions, "\n  when ", "", "", " |\n       ", " => ...");
            }
        }

        private void checkConditions(final Expression discriminator) throws SemanticError {
            if (conditions != null) {
                for (final ListIterator<Expression> it = conditions.listIterator(); it.hasNext(); ) {
                    final Expression cond = it.next();
                    discriminator.getType().checkAssignable(cond);
                    it.set(cond.resolve(discriminator.getType()));
                }
            }
        }

        @Override
        public String toString() {
            return (conditions == null ?
                    "\n  when others =>\n" :
                    TextUtils.formatList(conditions, "\n  when ", " |\n       ", " =>\n")) +
                   TextUtils.indentText("    ", TextUtils.formatList(statements, "", "\n", ""));
        }

        @Override
        public void accept(final ASTNodeVisitor v) {
            v.visitCaseAlternative(this);
        }

        @Override
        public List<ASTNode> children() {
            final List<ASTNode> result = new ArrayList<>();
            if (conditions != null) {
                result.addAll(conditions);
            }
            result.addAll(statements);
            return result;
        }

    }

    private final List<Alternative> alternatives;
    private final Expression discriminator;

    public static CaseStatement create(final Position position,
                                       final Expression discriminator,
                                       final List<Alternative> alternatives) {
        if (discriminator == null || alternatives == null) {
            return null;
        }

        try {
            return new CaseStatement(position, discriminator, alternatives);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    private CaseStatement(final Position position,
                          final Expression discriminator,
                          final List<Alternative> alternatives) throws SemanticError {
        super(position);

        for (final Alternative alt : alternatives) {
            alt.checkConditions(discriminator);
        }

        this.discriminator = discriminator;
        this.alternatives = alternatives;
        alternatives.stream().flatMap(a -> a.getStatements().stream()).forEach(s -> s.setParentStatement(this));
    }

    @Override
    public List<Alternative> getAlternatives() {
        return Collections.unmodifiableList(alternatives);
    }

    @Override
    public List<Statement> getChildStatements() {
        final List<Statement> result = new ArrayList<>();

        for (final Alternative alt : alternatives) {
            result.addAll(alt.getStatements());
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    public Expression getDiscriminator() {
        return this.discriminator;
    }

    @Override
    public String toAbbreviatedString() {
        final List<String> alts = new ArrayList<>();
        for (final Alternative alternative : alternatives) {
            alts.add(alternative.toAbbreviatedString());
        }
        return "case " + discriminator + " is" + TextUtils.formatList(alts, "", "", "");
    }

    @Override
    public String toString() {
        return "case " + discriminator + " is" + TextUtils.formatList(alternatives, "", "", "\nend case;");
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitCaseStatement(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(discriminator, alternatives);
    }

}
