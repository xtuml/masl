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
