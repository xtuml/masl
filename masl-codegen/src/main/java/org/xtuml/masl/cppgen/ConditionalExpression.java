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

import java.util.Set;

/**
 * Writes code for the C++ contitional operator <code>a?b:c</code>
 */
public class ConditionalExpression extends Expression {

    /**
     * Creates a conditional expression of the form <code>a?b:c</code>
     * <p>
     * <p>
     * The condition expression
     * <p>
     * The expression to evaluate if the condition is true
     * <p>
     * The expression to evaluate if the condition is false
     */
    public ConditionalExpression(final Expression conditionExpression,
                                 final Expression trueExpression,
                                 final Expression falseExpression) {
        this.conditionExpression = conditionExpression;
        this.trueExpression = trueExpression;
        this.falseExpression = falseExpression;
    }

    @Override
    boolean isTemplateType() {
        return trueExpression.isTemplateType() || falseExpression.isTemplateType();
    }

    @Override
    /**
     * {@inheritDoc}
     *
     * If the sub expressions are also condition expressions (as determined by the
     * precedence) then parenthesise them anyway, even though it may not be strictly
     * necessary, as the resulting code is virtually impossible for a human to parse
     * otherwise!
     */
    String getCode(final Namespace currentNamespace, final String alignment) {
        return (getPrecedence() <= conditionExpression.getPrecedence() ? "(" : "") +
               conditionExpression.getCode(currentNamespace, alignment) +
               (getPrecedence() <= conditionExpression.getPrecedence() ? ")" : "") +
               "\t? " +
               (getPrecedence() <= trueExpression.getPrecedence() ? "(" : "") +
               trueExpression.getCode(currentNamespace, alignment + "\t") +
               (getPrecedence() <= trueExpression.getPrecedence() ? ")" : "") +
               "\n" +
               alignment +
               "\t: " +
               (getPrecedence() <= falseExpression.getPrecedence() ? "(" : "") +
               falseExpression.getCode(currentNamespace, alignment + "\t") +
               (getPrecedence() <= falseExpression.getPrecedence() ? ")" : "");
    }

    @Override
    Set<Declaration> getForwardDeclarations() {
        final Set<Declaration> result = super.getForwardDeclarations();
        result.addAll(conditionExpression.getForwardDeclarations());
        result.addAll(trueExpression.getForwardDeclarations());
        result.addAll(falseExpression.getForwardDeclarations());
        return result;
    }

    @Override
    Set<CodeFile> getIncludes() {
        final Set<CodeFile> result = super.getIncludes();
        result.addAll(conditionExpression.getIncludes());
        result.addAll(trueExpression.getIncludes());
        result.addAll(falseExpression.getIncludes());
        return result;
    }

    @Override
    int getPrecedence() {
        return 15;
    }

    /**
     * The condition expression
     */
    private final Expression conditionExpression;
    /**
     * The expression to evaluate if the condition is false
     */
    private final Expression falseExpression;
    /**
     * The expression to evaluate if the condition is true
     */
    private final Expression trueExpression;

}
