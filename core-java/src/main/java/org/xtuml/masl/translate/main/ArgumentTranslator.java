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
package org.xtuml.masl.translate.main;

import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.type.BasicType;
import org.xtuml.masl.translate.main.expression.EventTranslator;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;

import java.util.ArrayList;
import java.util.List;

public class ArgumentTranslator {

    public ArgumentTranslator(final List<? extends ParameterDefinition> parameters,
                              final List<? extends org.xtuml.masl.metamodel.expression.Expression> maslArguments,
                              final Scope scope) {
        arguments = new ArrayList<Expression>(parameters.size());
        tempVariableDefinitions = new StatementGroup();
        outParameterAssignments = new StatementGroup();

        for (int i = 0; i < parameters.size(); ++i) {
            final ParameterDefinition param = parameters.get(i);
            final org.xtuml.masl.metamodel.expression.Expression arg = maslArguments.get(i);

            final BasicType paramType = param.getType();
            final ExpressionTranslator
                    argumentTranslator =
                    ExpressionTranslator.createTranslator(arg, scope, paramType);

            // The only time an event parameter is passed as an argument is for calls
            // to SM_TIMER_set_Timer. The use of event types as parameters are deprecated
            // now that MASL has first-class timers, but left here to support legacy models.
            if (argumentTranslator instanceof EventTranslator eventTranslator) {
                eventTranslator.setParameters(parameters.get(parameters.size() - 1),
                                              maslArguments.get(parameters.size() - 1),
                                              parameters.get(0),
                                              maslArguments.get(0));
            }

            if (param.getMode() == ParameterDefinition.Mode.IN) {
                arguments.add(argumentTranslator.getReadExpression());
            } else {
                if (argumentTranslator.getWriteFunction() != null) {
                    final Variable
                            temp =
                            new Variable(Types.getInstance().getType(paramType),
                                         "arg_" + i,
                                         argumentTranslator.getReadExpression());
                    tempVariableDefinitions.appendStatement(new VariableDefinitionStatement(temp));
                    arguments.add(temp.asExpression());
                    outParameterAssignments.appendStatement(new ExpressionStatement(argumentTranslator.getWriteExpression(
                            temp.asExpression())));
                } else {
                    arguments.add(argumentTranslator.getWriteableExpression());
                }
            }
        }

    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public StatementGroup getOutParameterAssignments() {
        return outParameterAssignments;
    }

    public StatementGroup getTempVariableDefinitions() {
        return tempVariableDefinitions;
    }

    private final List<Expression> arguments;
    private final StatementGroup outParameterAssignments;
    private final StatementGroup tempVariableDefinitions;

}
