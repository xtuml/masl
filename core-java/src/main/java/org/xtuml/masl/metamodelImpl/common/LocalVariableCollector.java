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

import org.xtuml.masl.metamodel.DoNothingASTNodeVisitor;
import org.xtuml.masl.metamodel.code.*;

import java.util.ArrayList;
import java.util.List;

public class LocalVariableCollector extends StatementTraverser<List<VariableDefinition>> {

    public LocalVariableCollector(final Statement statement) {
        super(new DoNothingASTNodeVisitor<Void, List<VariableDefinition>>() {

            @Override
            public Void visitCodeBlock(final CodeBlock statement, final List<VariableDefinition> variables) throws
                                                                                                                  Exception {
                variables.addAll(statement.getVariables());
                return null;
            }

            @Override
            public Void visitForStatement(final ForStatement statement, final List<VariableDefinition> variables) throws
                                                                                                                  Exception {
                variables.add(statement.getLoopSpec().getLoopVariableDef());
                return null;
            }
        });

        try {
            traverse(statement, variables);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public List<VariableDefinition> getLocalVariables() {
        return variables;
    }

    private final List<VariableDefinition> variables = new ArrayList<VariableDefinition>();

}
