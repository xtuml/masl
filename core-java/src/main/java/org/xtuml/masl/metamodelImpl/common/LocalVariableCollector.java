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
package org.xtuml.masl.metamodelImpl.common;

import org.xtuml.masl.metamodel.PreOrderASTNodeVisitor;
import org.xtuml.masl.metamodel.code.CodeBlock;
import org.xtuml.masl.metamodel.code.ForStatement;
import org.xtuml.masl.metamodel.code.VariableDefinition;

import java.util.ArrayList;
import java.util.List;

public class LocalVariableCollector extends PreOrderASTNodeVisitor {

    public LocalVariableCollector(final CodeBlock block) {
        visit(block);
    }

    @Override
    public final void visitCodeBlock(final CodeBlock statement) {
        variables.addAll(statement.getVariables());
    }

    @Override
    public void visitForStatement(final ForStatement statement) {
        variables.add(statement.getLoopSpec().getLoopVariableDef());
    }

    public List<VariableDefinition> getLocalVariables() {
        return variables;
    }

    private final List<VariableDefinition> variables = new ArrayList<>();

}
