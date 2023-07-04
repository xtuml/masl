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
package org.xtuml.masl.cppgen;

import org.xtuml.masl.utils.TextUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Set;

/**
 * A C++ switch statement
 */
public class SwitchStatement extends Statement {

    /**
     * A C++ case condition within a switch statement
     */
    public static class CaseCondition {

        /**
         * Creates a case condition
         * <p>
         * <p>
         * the discriminator for this condition. If the containing switch
         * statement's condition variable is equal to this value, execution
         * continues with the statements in this condition
         * <p>
         * the statement to execute if the discriminator matches. If more
         * than one statement is required, then either a {@link CodeBlock}
         * or {@link StatementGroup} may be used. Note that there is no
         * implicit <code>break</code> statement, and if one is required it
         * must be explicitly added to the group or block.
         */
        public CaseCondition(final Expression expression, final Statement statement) {
            this.expression = expression;
            this.statement = statement;
        }

        Set<Declaration> getForwardDeclarations() {
            final Set<Declaration> result = expression.getForwardDeclarations();
            if (statement != null) {
                result.addAll(statement.getForwardDeclarations());
            }
            return result;
        }

        Set<CodeFile> getIncludes() {
            final Set<CodeFile> result = expression.getIncludes();
            if (statement != null) {
                result.addAll(statement.getIncludes());
            }
            return result;
        }

        void setParent(final Statement parent) {
            if (statement != null) {
                statement.setParent(parent);
            }
        }

        void write(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
            writer.write(indent + TextUtils.alignTabs("case " + expression.getCode(currentNamespace) + ":"));

            if (statement != null) {
                if (statement instanceof CodeBlock) {
                    writer.write("\n");
                    statement.write(writer, indent, currentNamespace);
                    writer.write("\n");
                } else if (statement instanceof StatementGroup) {
                    writer.write("\n");
                    statement.write(writer, indent + TextUtils.getIndent(), currentNamespace);
                    writer.write("\n");
                } else {
                    statement.write(writer, "\t", currentNamespace);
                    writer.write("\n");
                }
            } else {
                writer.write("\n");
            }
        }

        private final Expression expression;

        private final Statement statement;

    }

    /**
     * Creates a C++ switch statement.
     * <p>
     * <p>
     * the expression that determines which of the case conditions should
     * be executed
     * <p>
     * a list of case conditions
     * <p>
     * the statement to execute if none of the case conditions match
     */
    public SwitchStatement(final Expression expression,
                           final List<CaseCondition> cases,
                           final Statement defaultStatement) {
        if (defaultStatement != null) {
            defaultStatement.setParent(this);
        }
        for (final CaseCondition caseCond : cases) {
            caseCond.setParent(this);
        }
        this.expression = expression;
        this.cases = cases;
        this.defaultStatement = defaultStatement;
    }

    /**
     * Creates a C++ switch statement.
     * <p>
     * <p>
     * the expression that determines which of the case conditions should
     * be executed
     * <p>
     * a list of case conditions
     */
    public SwitchStatement(final Expression expression, final List<CaseCondition> cases) {
        this(expression, cases, null);
    }

    @Override
    Set<Declaration> getForwardDeclarations() {
        final Set<Declaration> result = super.getForwardDeclarations();
        result.addAll(expression.getForwardDeclarations());
        if (defaultStatement != null) {
            result.addAll(defaultStatement.getForwardDeclarations());
        }
        for (final CaseCondition caseCond : cases) {
            result.addAll(caseCond.getForwardDeclarations());
        }
        return result;
    }

    @Override
    Set<CodeFile> getIncludes() {
        final Set<CodeFile> result = super.getIncludes();
        result.addAll(expression.getIncludes());
        if (defaultStatement != null) {
            result.addAll(defaultStatement.getIncludes());
        }
        for (final CaseCondition caseCond : cases) {
            result.addAll(caseCond.getIncludes());
        }
        return result;
    }

    @Override
    void write(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
        writer.write(indent + "switch ( " + expression.getCode(currentNamespace) + " )\n" + indent + "{\n");
        final StringWriter buf = new StringWriter();
        for (final CaseCondition caseCond : cases) {
            caseCond.write(buf, indent + TextUtils.getIndent(), currentNamespace);
        }
        if (defaultStatement != null) {
            buf.write(indent + TextUtils.getIndent() + "default:");

            if (defaultStatement instanceof CodeBlock) {
                buf.write("\n");
                defaultStatement.write(buf, indent + TextUtils.getIndent(), currentNamespace);
            } else {
                defaultStatement.write(buf, "\t", currentNamespace);
            }
            buf.write("\n");
        }
        TextUtils.alignTabs(writer, buf.toString());
        writer.write(indent + "}\n");

    }

    private final Expression expression;

    private final List<CaseCondition> cases;

    private final Statement defaultStatement;

}
