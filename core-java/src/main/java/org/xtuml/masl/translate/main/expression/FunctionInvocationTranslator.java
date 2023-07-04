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
package org.xtuml.masl.translate.main.expression;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.expression.DomainFunctionInvocation;
import org.xtuml.masl.metamodel.expression.InstanceFunctionInvocation;
import org.xtuml.masl.metamodel.expression.ObjectFunctionInvocation;
import org.xtuml.masl.metamodel.expression.TerminatorFunctionInvocation;
import org.xtuml.masl.metamodel.object.ObjectService;
import org.xtuml.masl.translate.main.DomainServiceTranslator;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.TerminatorServiceTranslator;
import org.xtuml.masl.translate.main.object.ObjectTranslator;

import java.util.ArrayList;
import java.util.List;

public class FunctionInvocationTranslator extends ExpressionTranslator {

    FunctionInvocationTranslator(final DomainFunctionInvocation invocation, final Scope scope) {
        final Function function = DomainServiceTranslator.getInstance(invocation.getService()).getFunction();

        setReadExpression(function.asFunctionCall(translateArguments(invocation.getArguments(), scope)));
        setWriteableExpression(getReadExpression());

    }

    FunctionInvocationTranslator(final TerminatorFunctionInvocation invocation, final Scope scope) {
        final Function function = TerminatorServiceTranslator.getInstance(invocation.getService()).getFunction();

        setReadExpression(function.asFunctionCall(translateArguments(invocation.getArguments(), scope)));
        setWriteableExpression(getReadExpression());

    }

    FunctionInvocationTranslator(final ObjectFunctionInvocation invocation, final Scope scope) {
        final ObjectService service = invocation.getService();

        final Function function = ObjectTranslator.getInstance(service.getParentObject()).getService(service);

        setReadExpression(function.asFunctionCall(translateArguments(invocation.getArguments(), scope)));
        setWriteableExpression(getReadExpression());

    }

    FunctionInvocationTranslator(final InstanceFunctionInvocation invocation, final Scope scope) {
        final ObjectService service = invocation.getService();

        final Function function = ObjectTranslator.getInstance(service.getParentObject()).getService(service);

        final Expression instance = createTranslator(invocation.getInstance(), scope).getReadExpression();
        setReadExpression(function.asFunctionCall(instance,
                                                  true,
                                                  translateArguments(invocation.getArguments(), scope)));
        setWriteableExpression(getReadExpression());

    }

    private List<Expression> translateArguments(final List<? extends org.xtuml.masl.metamodel.expression.Expression> maslArguments,
                                                final Scope scope) {
        final List<Expression> arguments = new ArrayList<Expression>(maslArguments.size());

        for (final org.xtuml.masl.metamodel.expression.Expression maslArgument : maslArguments) {
            arguments.add(createTranslator(maslArgument, scope).getReadExpression());
        }
        return arguments;
    }

}
