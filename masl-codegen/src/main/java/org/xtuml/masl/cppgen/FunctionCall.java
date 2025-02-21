/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.cppgen;

import org.xtuml.masl.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * An expression representing a C++ function call.
 */
public class FunctionCall extends Expression {

    private final Function function;
    private final List<Expression> arguments;

    /**
     * Creates a function call of the specified function, passing the specified
     * arguments
     */
    public FunctionCall(final Function function, final List<Expression> arguments) {
        this.function = function;
        this.arguments = arguments;

        // Check for null - better to find it now when we still have the stack of
        // whatever created the call
        for (final Expression arg : arguments) {
            if (arg == null) {
                throw new NullPointerException();
            }
        }

    }

    @Override
    boolean isTemplateType() {
        return function.getReturnType().isTemplateType();
    }

    @Override
    String getCode(final Namespace currentNamespace, final String alignment) {
        final StringBuilder buf = new StringBuilder();
        buf.append(function.getCallName(currentNamespace));
        final List<String> argCode = new ArrayList<>();
        for (final Expression arg : arguments) {
            String code = arg.getCode(currentNamespace);
            // Need to parenthesise any comma operator to ensure correct parsing
            if (arg.getPrecedence() >= BinaryOperator.COMMA.getPrecedence()) {
                code = "(" + code + ")";
            }
            argCode.add(code);
        }

        buf.append("(" + TextUtils.formatList(argCode, " ", ", ", " ") + ")");

        return buf.toString();
    }

    @Override
    Set<Declaration> getForwardDeclarations() {
        final Set<Declaration> result = super.getForwardDeclarations();
        for (final Expression arg : arguments) {
            result.addAll(arg.getForwardDeclarations());
        }
        return result;
    }

    @Override
    Set<CodeFile> getIncludes() {
        final Set<CodeFile> result = super.getIncludes();

        result.addAll(function.getCallIncludes());
        for (final Expression arg : arguments) {
            result.addAll(arg.getIncludes());
        }

        return result;
    }

    @Override
    int getPrecedence() {
        return 0;
    }

}
