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
 * A C++ throw statement
 */
public class ThrowStatement extends Statement {

    /**
     * Creates a throw statement that will rethrow the exception currently being
     * handled.
     */
    public ThrowStatement() {
        this.exception = null;
    }

    /**
     * Creates a throw statement that will throw the specified expression as an
     * exception. If the exception is null, a throw statement to rethrow the current
     * exception will be created.
     * <p>
     * <p>
     * the exception to throw
     */
    public ThrowStatement(final Expression exception) {
        this.exception = exception;
    }

    @Override
    Set<Declaration> getForwardDeclarations() {
        final Set<Declaration> result = super.getForwardDeclarations();
        if (exception != null) {
            result.addAll(exception.getForwardDeclarations());
        }
        return result;
    }

    @Override
    Set<CodeFile> getIncludes() {
        final Set<CodeFile> result = super.getIncludes();
        if (exception != null) {
            result.addAll(exception.getIncludes());
        }
        return result;
    }

    @Override
    void write(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
        writer.write(indent +
                     TextUtils.alignTabs("throw" +
                                         (exception == null ? "" : " " + exception.getCode(currentNamespace)) +
                                         ";"));

    }

    private final Expression exception;

}
