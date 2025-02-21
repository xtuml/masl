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

/**
 * Represents a C++ array access. An array access is an expression that returns
 * the value of an element of an array at a particular index. For example
 * {@code a[4]}will return the fourth element of array {@code a}.
 */
public class ArrayAccess extends Expression {

    /**
     * Creates an array access object.
     * <p>
     * <p>
     * the array to access
     * <p>
     * the index of the required element
     */
    public ArrayAccess(final Expression name, final Expression index) {
        this.nameExpression = name;
        this.indexExpression = index;
    }

    @Override
    String getCode(final Namespace currentNamespace, final String alignment) {
        String name = nameExpression.getCode(currentNamespace, alignment);
        final String index = indexExpression.getCode(currentNamespace, alignment);

        if (getPrecedence() < nameExpression.getPrecedence()) {
            name = "(" + name + ")";
        }

        return name + "[" + index + "]";
    }

    @Override
    int getPrecedence() {
        return 2;
    }

    @Override
    boolean isTemplateType() {
        return nameExpression.isTemplateType();
    }

    /**
     * The index or the required element
     */
    private final Expression indexExpression;

    /**
     * The array to index into
     */
    private final Expression nameExpression;

}
