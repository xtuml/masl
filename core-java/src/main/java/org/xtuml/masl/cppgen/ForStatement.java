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
import java.util.Set;

/**
 * Represents a C++ <code>for</code> statement.
 */
public class ForStatement extends Statement {

    /**
     * Creates a C++ <code>for</code> statement using an expression as the start
     * condition.
     * <p>
     * Creates code of the form:
     * <code>for ( startExpression; endCondition; increment ) statement;</code>
     * <p>
     * eg <code>
     * for ( i = 0; i < 10; ++i ) f(i);
     * </code>
     * <p>
     * <p>
     * expression to initialise the loop
     * <p>
     * condition that determines termination of the loop
     * <p>
     * expression to evaluate each time round the loop
     * <p>
     * statements to execute each time around the loop
     */
    public ForStatement(final Expression startExpression,
                        final Expression endCondition,
                        final Expression increment,
                        final Statement statement) {
        this.startVariable = null;
        this.startExpression = startExpression;
        this.endCondition = endCondition;
        this.increment = increment;
        statement.setParent(this);
        this.statement = statement;

    }

    /**
     * Creates a C++ <code>for</code> statement using a variable declaration as
     * the start condition.
     * <p>
     * Creates code of the form: <code>
     * for ( startVariable; endCondition; increment ) statement;</code>
     * <p>
     * eg <code>
     * for ( int i = 0; i < 10; ++i ) f(i);
     * </code>
     * <p>
     * <p>
     * variable to declare in the loop initialisation
     * <p>
     * condition that determines termination of the loop
     * <p>
     * expression to evaluate each time round the loop
     * <p>
     * statements to execute each time around the loop
     */
    public ForStatement(final VariableDefinitionStatement startVariable,
                        final Expression endCondition,
                        final Expression increment,
                        Statement statement) {
        startVariable.setParent(this);
        statement.setParent(this);

        this.startVariable = startVariable;
        this.startExpression = null;
        this.endCondition = endCondition;
        this.increment = increment;
        if (statement instanceof StatementGroup) {
            final CodeBlock tmp = new CodeBlock();
            tmp.appendStatement(statement);
            statement = tmp;
        }
        this.statement = statement;

    }

    @Override
    Set<Declaration> getForwardDeclarations() {
        final Set<Declaration> result = super.getForwardDeclarations();
        if (startExpression != null) {
            result.addAll(startExpression.getForwardDeclarations());
        }
        if (startVariable != null) {
            result.addAll(startVariable.getForwardDeclarations());
        }
        if (endCondition != null) {
            result.addAll(endCondition.getForwardDeclarations());
        }
        if (increment != null) {
            result.addAll(increment.getForwardDeclarations());
        }
        result.addAll(statement.getForwardDeclarations());
        return result;
    }

    @Override
    Set<CodeFile> getIncludes() {
        final Set<CodeFile> result = super.getIncludes();
        if (startExpression != null) {
            result.addAll(startExpression.getIncludes());
        }
        if (startVariable != null) {
            result.addAll(startVariable.getIncludes());
        }
        if (endCondition != null) {
            result.addAll(endCondition.getIncludes());
        }
        if (increment != null) {
            result.addAll(increment.getIncludes());
        }
        result.addAll(statement.getIncludes());
        return result;
    }

    @Override
    void write(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
        String startCode;
        if (startVariable == null) {
            startCode = startExpression.getCode(currentNamespace) + ";";
        } else {
            final StringWriter strWriter = new StringWriter();
            startVariable.write(strWriter, "", currentNamespace);
            startCode = strWriter.toString();
        }

        writer.write(TextUtils.alignTabs(indent +
                                         "for ( " +
                                         startCode +
                                         " " +
                                         (endCondition == null ? "" : endCondition.getCode(currentNamespace)) +
                                         "; " +
                                         (increment == null ? "" : increment.getCode(currentNamespace)) +
                                         " )"));
        if (statement instanceof CodeBlock) {
            writer.write("\n");
            statement.write(writer, indent, currentNamespace);
        } else {
            statement.write(writer, " ", currentNamespace);
        }
    }

    private final Expression endCondition;
    private final Expression increment;
    private final Expression startExpression;
    private final VariableDefinitionStatement startVariable;
    private final Statement statement;

}
