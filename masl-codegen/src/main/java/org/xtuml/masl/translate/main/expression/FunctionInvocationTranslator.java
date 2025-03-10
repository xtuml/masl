/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
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
        final List<Expression> arguments = new ArrayList<>(maslArguments.size());

        for (final org.xtuml.masl.metamodel.expression.Expression maslArgument : maslArguments) {
            arguments.add(createTranslator(maslArgument, scope).getReadExpression());
        }
        return arguments;
    }

}
