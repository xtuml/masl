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

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

public class VariableDefinitionStatement extends Statement {

    private final Variable variable;

    public VariableDefinitionStatement(final Variable variable) {
        this.variable = variable;
    }

    @Override
    /**
     *
     * @throws IOException
     * @see org.xtuml.masl.cppgen.Statement#write(java.io.Writer, java.lang.String)
     */
    void write(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
        ((Variable.VariableDefinition) variable.getDefinition()).writeCodeDefinition(writer, indent, currentNamespace);
    }

    @Override
    Set<Declaration> getForwardDeclarations() {
        final Set<Declaration> result = super.getForwardDeclarations();
        result.addAll(variable.getDefinition().getForwardDeclarations());
        return result;
    }

    @Override
    Set<CodeFile> getIncludes() {
        final Set<CodeFile> result = super.getIncludes();
        result.addAll(variable.getDefinition().getIncludes());
        return result;
    }

}
