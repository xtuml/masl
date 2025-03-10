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

import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.expression.*;
import org.xtuml.masl.metamodel.type.BasicType;
import org.xtuml.masl.translate.main.Scope;

public abstract class ExpressionTranslator {

    private static class SimpleTranslator extends ExpressionTranslator {

        SimpleTranslator(final org.xtuml.masl.cppgen.Expression expression) {
            setReadExpression(expression);
            setWriteableExpression(expression);
        }
    }

    public static ExpressionTranslator createTranslator(final Expression expression, final Scope scope) {
        return createTranslator(expression, scope, null, null);
    }

    public static ExpressionTranslator createTranslator(final Expression expression,
                                                        final Scope scope,
                                                        final BasicType requiredType) {
        return createTranslator(expression, scope, requiredType, null);
    }

    public static ExpressionTranslator createTranslator(final Expression expression,
                                                        final Scope scope,
                                                        final Expression assignedTo) {
        return createTranslator(expression, scope, assignedTo.getType(), assignedTo);
    }

    public static ExpressionTranslator createTranslator(final Expression expression,
                                                        final Scope scope,
                                                        final BasicType requiredType,
                                                        final Expression assignedTo) {
        if (expression instanceof BinaryExpression) {
            return new BinaryExpressionTranslator(((BinaryExpression) expression), scope, assignedTo);
        } else if (expression instanceof CastExpression) {
            return new CastExpressionTranslator((CastExpression) expression, scope);
        } else if (expression instanceof FindParameterExpression) {
            return new SimpleTranslator(scope.resolveFindParameter((FindParameterExpression) expression));
        } else if (expression instanceof ElementsExpression) {
            return new ElementsTranslator((ElementsExpression) expression, scope);
        } else if (expression instanceof DictionaryKeysExpression) {
            return new ElementsTranslator((DictionaryKeysExpression) expression, scope);
        } else if (expression instanceof DictionaryValuesExpression) {
            return new ElementsTranslator((DictionaryValuesExpression) expression, scope);
        } else if (expression instanceof AnyExpression) {
            return new AnyTranslator((AnyExpression) expression, scope);
        } else if (expression instanceof CharacteristicExpression) {
            return new CharacteristicTranslator((CharacteristicExpression) expression, scope);
        } else if (expression instanceof CorrelatedNavExpression) {
            return new NavigationTranslator((CorrelatedNavExpression) expression, scope);
        } else if (expression instanceof CreateExpression) {
            return new CreateExpressionTranslator((CreateExpression) expression, scope);
        } else if (expression instanceof DomainFunctionInvocation) {
            return new FunctionInvocationTranslator((DomainFunctionInvocation) expression, scope);
        } else if (expression instanceof TerminatorFunctionInvocation) {
            return new FunctionInvocationTranslator((TerminatorFunctionInvocation) expression, scope);
        } else if (expression instanceof EventExpression) {
            return new EventTranslator((EventExpression) expression, scope);
        } else if (expression instanceof FindExpression) {
            return new FindTranslator((FindExpression) expression, scope);
        } else if (expression instanceof InstanceFunctionInvocation) {
            return new FunctionInvocationTranslator((InstanceFunctionInvocation) expression, scope);
        } else if (expression instanceof InstanceOrderingExpression) {
            return new OrderingTranslator((InstanceOrderingExpression) expression, scope);
        }
        if (expression instanceof StructureOrderingExpression) {
            return new OrderingTranslator((StructureOrderingExpression) expression, scope);
        } else if (expression instanceof LiteralExpression) {
            return new LiteralTranslator((LiteralExpression) expression);
        } else if (expression instanceof NavigationExpression) {
            return new NavigationTranslator((NavigationExpression) expression, scope);
        } else if (expression instanceof ObjectFunctionInvocation) {
            return new FunctionInvocationTranslator((ObjectFunctionInvocation) expression, scope);
        } else if (expression instanceof StructureAggregate) {
            return new StructureAggregateTranslator((StructureAggregate) expression, scope, requiredType);
        } else if (expression instanceof UnaryExpression) {
            return new UnaryExpressionTranslator(((UnaryExpression) expression), scope);
        } else if (expression instanceof FindAttributeNameExpression) {
            return new AttributeTranslator((FindAttributeNameExpression) expression, scope);
        } else if (expression instanceof IndexedNameExpression) {
            return new IndexedNameTranslator((IndexedNameExpression) expression, scope);
        } else if (expression instanceof DictionaryAccessExpression) {
            return new DictionaryAccessTranslator((DictionaryAccessExpression) expression, scope);
        } else if (expression instanceof DictionaryContainsExpression) {
            return new DictionaryContainsTranslator((DictionaryContainsExpression) expression, scope);
        } else if (expression instanceof ParameterNameExpression) {
            return new SimpleTranslator(scope.resolveParameter(((ParameterNameExpression) expression).getParameter()));
        } else if (expression instanceof SelectedAttributeExpression) {
            return new AttributeTranslator((SelectedAttributeExpression) expression, scope);
        } else if (expression instanceof SelectedComponentExpression) {
            return new SelectedComponentTranslator((SelectedComponentExpression) expression, scope);
        } else if (expression instanceof SliceExpression) {
            return new SliceTranslator((SliceExpression) expression, scope);
        } else if (expression instanceof VariableNameExpression) {
            return new SimpleTranslator(scope.resolveVariable(((VariableNameExpression) expression).getVariable()));
        } else if (expression instanceof EofExpression) {
            return new EofExpressionTranslator(((EofExpression) expression), scope);
        } else if (expression instanceof SplitExpression) {
            return new SplitTranslator((SplitExpression) expression, scope);
        } else if (expression instanceof CreateDurationExpression) {
            return new CreateDurationTranslator((CreateDurationExpression) expression, scope);
        } else if (expression instanceof TimeFieldExpression) {
            return new TimeFieldTranslator((TimeFieldExpression) expression, scope);
        } else if (expression instanceof TimerFieldExpression) {
            return new TimerFieldTranslator((TimerFieldExpression) expression, scope);
        } else if (expression instanceof TimestampDeltaExpression) {
            return new TimestampDeltaTranslator((TimestampDeltaExpression) expression, scope);
        } else if (expression instanceof RangeExpression) {
            return new RangeTranslator((RangeExpression) expression, scope);
        } else if (expression instanceof ParseExpression) {
            return new ParseExpressionTranslator((ParseExpression) expression, scope);
        } else if (expression instanceof LinkUnlinkExpression) {
            return new LinkUnlinkExpressionTranslator((LinkUnlinkExpression) expression, scope);
        }

        throw new IllegalArgumentException("Unrecognised Expression '" +
                                           expression +
                                           "' of " +
                                           expression.getClass().toString());
    }

    public org.xtuml.masl.cppgen.Expression getReadExpression() {
        return readExpression;
    }

    public org.xtuml.masl.cppgen.Expression getWriteableExpression() {
        // Return a value event if writeableExpression is not set, as we always
        // calculate a writeable expression without caring if it is writable or not,
        // so handing out nulls is not good!
        return writeExpression == null ? readExpression : writeExpression;
    }

    public Function getWriteFunction() {
        return writeFunction;
    }

    public org.xtuml.masl.cppgen.Expression getWriteExpression(final org.xtuml.masl.cppgen.Expression rhs) {
        if (writeExpression != null) {
            return new org.xtuml.masl.cppgen.BinaryExpression(writeExpression,
                                                              org.xtuml.masl.cppgen.BinaryOperator.ASSIGN,
                                                              rhs);
        } else {
            if (writeBase != null) {
                return writeFunction.asFunctionCall(writeBase, writePointer, rhs);
            } else {
                return writeFunction.asFunctionCall(rhs);
            }
        }
    }

    public void setReadExpression(final org.xtuml.masl.cppgen.Expression expression) {
        this.readExpression = expression;
    }

    public void setWriteableExpression(final org.xtuml.masl.cppgen.Expression expression) {
        this.writeExpression = expression;
    }

    public void setWriteFunction(final Function function,
                                 final org.xtuml.masl.cppgen.Expression base,
                                 final boolean pointer) {
        this.writeFunction = function;
        this.writeBase = base;
        this.writePointer = pointer;
    }

    public void setWriteFunction(final Function function) {
        this.writeFunction = function;
    }

    private org.xtuml.masl.cppgen.Expression readExpression;
    private org.xtuml.masl.cppgen.Expression writeExpression = null;
    private org.xtuml.masl.cppgen.Expression writeBase = null;
    private boolean writePointer = false;
    private Function writeFunction = null;

}
