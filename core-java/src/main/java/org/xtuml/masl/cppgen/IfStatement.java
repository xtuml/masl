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
import java.io.Writer;
import java.util.Set;

/**
 * A C++ <ocde>if</code> statement.
 */
public class IfStatement extends Statement {

    /**
     * The condition that decides whether the 'if' or 'else' statements should be
     * executed.
     */

    private final Expression condition;
    /**
     * Statement to execute if the condition expression is true.
     */

    private final Statement ifStatement;

    /**
     * Statement to execute if the condition expression is false.
     */
    private Statement elseStatement;

    /**
     * Creates a C++ <code>if</code>-<code>else</code> statement.
     * <p>
     * <p>
     * The condition that decides whether the 'if' or 'else' statements should be
     * executed.
     * <p>
     * The statement to execute if the condition is true.
     * <p>
     * The statement to execute if the condition is false.
     */
    public IfStatement(final Expression condition, Statement ifStatement, Statement elseStatement) {
        ifStatement.setParent(this);
        if (elseStatement != null) {
            elseStatement.setParent(this);
        }

        if (ifStatement instanceof StatementGroup) {
            final CodeBlock tmp = new CodeBlock();
            tmp.appendStatement(ifStatement);
            ifStatement = tmp;
        }

        if (elseStatement instanceof StatementGroup) {
            final CodeBlock tmp = new CodeBlock();
            tmp.appendStatement(elseStatement);
            elseStatement = tmp;
        }

        this.condition = condition;
        this.ifStatement = ifStatement;
        this.elseStatement = elseStatement;
    }

    /**
     * Creates a C++ <code>if</code> statement. An else clause may be added later by
     * means of the {@link IfStatement#setElse(Statement)}function.
     * <p>
     * <p>
     * The condition that decides whether the 'if' statement should be executed.
     * <p>
     * The statement to execute if the condition is true.
     */
    public IfStatement(final Expression condition, final Statement ifStatement) {
        this(condition, ifStatement, null);
    }

    /**
     * Converts an <code>if</code> statement into an <code>if</code>-
     * <code>else</code> statement.
     * <p>
     * <p>
     * The statement to execute if the condition is false.
     */
    public void setElse(final Statement elseStatement) {
        elseStatement.setParent(this);
        this.elseStatement = elseStatement;
    }

    @Override
    void write(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
        write(writer, indent, indent, currentNamespace);
    }

    /**
     * Helper function for {@link IfStatement#write(Writer, String, Namespace)}, which
     * takes an extra argument for the indent for the initial if statement. This is
     * so that cascading <code>else if</code> statements can be formatted nicely.
     * <p>
     * <p>
     * The writer to write code to
     * <p>
     * Indent for the initial if statement.
     * <p>
     * Indent for subsequent lines.
     * <p>
     * The namespace the code is being written into.
     *
     * @throws IOException
     */
    private void write(final Writer writer,
                       final String firstIndent,
                       final String indent,
                       final Namespace currentNamespace) throws IOException {
        writer.write(TextUtils.alignTabs(firstIndent + "if ( " + condition.getCode(currentNamespace) + " )"));
        if (ifStatement instanceof CodeBlock) {
            writer.write("\n");
            ifStatement.write(writer, indent, currentNamespace);
        } else {
            ifStatement.write(writer, " ", currentNamespace);
        }
        if (elseStatement != null) {
            writer.write("\n" + indent + "else");
            if (elseStatement instanceof CodeBlock) {
                writer.write("\n");
                elseStatement.write(writer, indent, currentNamespace);
            } else if (elseStatement instanceof IfStatement) {
                // Cascading 'else if', so no new line and indent by just a single
                // space.

                ((IfStatement) elseStatement).write(writer, " ", indent, currentNamespace);
            } else {
                // Must be single statement, so write onto same line.
                elseStatement.write(writer, " ", currentNamespace);
            }
        }
    }

    @Override
    Set<Declaration> getForwardDeclarations() {
        final Set<Declaration> result = super.getForwardDeclarations();
        result.addAll(condition.getForwardDeclarations());
        result.addAll(ifStatement.getForwardDeclarations());
        if (elseStatement != null) {
            result.addAll(elseStatement.getForwardDeclarations());
        }
        return result;
    }

    @Override
    Set<CodeFile> getIncludes() {
        final Set<CodeFile> result = super.getIncludes();
        result.addAll(condition.getIncludes());
        result.addAll(ifStatement.getIncludes());
        if (elseStatement != null) {
            result.addAll(elseStatement.getIncludes());
        }
        return result;
    }

}
