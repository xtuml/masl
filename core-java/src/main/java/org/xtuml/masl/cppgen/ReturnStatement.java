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
 * A C++ return statement
 */
public class ReturnStatement extends Statement {

    private final Expression expression;

    /**
     * Creates a return statement, returning the supplied expression. If the
     * expression is null, a bare <code>return;</code> is generated.
     * <p>
     * <p>
     * the value to return
     */
    public ReturnStatement(final Expression expression) {
        this.expression = expression;
    }

    @Override
    void write(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
        writer.write(TextUtils.indentText(indent,
                                          TextUtils.alignTabs("return" +
                                                              (expression == null ?
                                                               "" :
                                                               " " + expression.getCode(currentNamespace)) +
                                                              ";")));

    }

    @Override
    Set<Declaration> getForwardDeclarations() {
        final Set<Declaration> result = super.getForwardDeclarations();
        if (expression != null) {
            result.addAll(expression.getForwardDeclarations());
        }
        return result;
    }

    @Override
    Set<CodeFile> getIncludes() {
        final Set<CodeFile> result = super.getIncludes();
        if (expression != null) {
            result.addAll(expression.getIncludes());
        }
        return result;
    }

}
