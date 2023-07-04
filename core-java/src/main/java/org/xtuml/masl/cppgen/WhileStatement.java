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

public class WhileStatement extends Statement {

    private final Expression expression;
    private final Statement statement;

    public WhileStatement(final Expression expression, Statement statement) {
        if (statement instanceof StatementGroup) {
            final CodeBlock tmp = new CodeBlock();
            tmp.appendStatement(statement);
            statement = tmp;
        }

        statement.setParent(this);

        this.expression = expression;
        this.statement = statement;

    }

    @Override
    /**
     *
     * @throws IOException
     * @see org.xtuml.masl.cppgen.Statement#write(java.io.Writer, java.lang.String)
     */
    void write(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
        writer.write(TextUtils.alignTabs(indent + "while ( " + expression.getCode(currentNamespace) + " )"));
        if (statement instanceof CodeBlock) {
            writer.write("\n");
            statement.write(writer, indent, currentNamespace);
        } else {
            statement.write(writer, " ", currentNamespace);
        }
    }

    @Override
    Set<Declaration> getForwardDeclarations() {
        final Set<Declaration> result = super.getForwardDeclarations();
        result.addAll(expression.getForwardDeclarations());
        result.addAll(statement.getForwardDeclarations());
        return result;
    }

    @Override
    Set<CodeFile> getIncludes() {
        final Set<CodeFile> result = super.getIncludes();
        result.addAll(expression.getIncludes());
        result.addAll(statement.getIncludes());
        return result;
    }

}
