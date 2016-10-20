//
// File: NameExpressionTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.expression;

import java.util.ArrayList;
import java.util.List;

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



public class FunctionInvocationTranslator extends ExpressionTranslator
{

  FunctionInvocationTranslator ( final DomainFunctionInvocation invocation, final Scope scope )
  {
    final Function function = DomainServiceTranslator.getInstance(invocation.getService()).getFunction();

    setReadExpression(function.asFunctionCall(translateArguments(invocation.getArguments(), scope)));
    setWriteableExpression(getReadExpression());

  }

  FunctionInvocationTranslator ( final TerminatorFunctionInvocation invocation, final Scope scope )
  {
    final Function function = TerminatorServiceTranslator.getInstance(invocation.getService()).getFunction();

    setReadExpression(function.asFunctionCall(translateArguments(invocation.getArguments(), scope)));
    setWriteableExpression(getReadExpression());

  }

  FunctionInvocationTranslator ( final ObjectFunctionInvocation invocation, final Scope scope )
  {
    final ObjectService service = invocation.getService();

    final Function function = ObjectTranslator.getInstance(service.getParentObject()).getService(service);

    setReadExpression(function.asFunctionCall(translateArguments(invocation.getArguments(), scope)));
    setWriteableExpression(getReadExpression());

  }

  FunctionInvocationTranslator ( final InstanceFunctionInvocation invocation, final Scope scope )
  {
    final ObjectService service = invocation.getService();

    final Function function = ObjectTranslator.getInstance(service.getParentObject()).getService(service);

    final Expression instance = createTranslator(invocation.getInstance(), scope).getReadExpression();
    setReadExpression(function.asFunctionCall(instance, true, translateArguments(invocation.getArguments(), scope)));
    setWriteableExpression(getReadExpression());

  }

  private List<Expression> translateArguments ( final List<? extends org.xtuml.masl.metamodel.expression.Expression> maslArguments,
                                                final Scope scope )
  {
    final List<Expression> arguments = new ArrayList<Expression>(maslArguments.size());

    for ( final org.xtuml.masl.metamodel.expression.Expression maslArgument : maslArguments )
    {
      arguments.add(createTranslator(maslArgument, scope).getReadExpression());
    }
    return arguments;
  }


}
