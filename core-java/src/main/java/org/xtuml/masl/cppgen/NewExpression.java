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

import org.xtuml.masl.utils.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class NewExpression extends Expression {

    /**
     * Creates a new expression of the form <code>new type[arraySize]</code>
     * <p>
     * <p>
     * the type to create a new instance of
     * <p>
     * the size of the array to create
     */
    public NewExpression(final TypeUsage type, final Expression arraySize) {
        this.type = type;
        this.args = null;
        this.arraySize = arraySize;
        this.placement = null;
    }

    /**
     * Creates a new expression of the form
     * <code>new (placement) type[arraySize]</code>
     * <p>
     * <p>
     * the type to create a new instance of
     * <p>
     * the size of the array to create
     * <p>
     * the arguments to pass to the placement new
     */
    public NewExpression(final TypeUsage type, final Expression arraySize, final List<Expression> placement) {
        this.type = type;
        this.args = null;
        this.arraySize = arraySize;
        this.placement = placement;
    }

    /**
     * Creates a new expression of the form <code>new type(args...)</code>
     * <p>
     * <p>
     * the type to create a new instance of
     * <p>
     * the arguments to pass to the types constructor
     */
    public NewExpression(final TypeUsage type, final Expression... args) {
        this.type = type;
        this.args = Arrays.asList(args);
        this.arraySize = null;
        this.placement = null;
    }

    /**
     * Creates a new expression of the form <code>new type(args...)</code>
     * <p>
     * <p>
     * the type to create a new instance of
     * <p>
     * the arguments to pass to the types constructor
     */
    public NewExpression(final TypeUsage type, final List<Expression> args) {
        this.type = type;
        this.args = args;
        this.arraySize = null;
        this.placement = null;
    }

    /**
     * Creates a new expression of the form
     * <code>new (placement) type(args...)</code>
     * <p>
     * <p>
     * the type to create a new instance of
     * <p>
     * the arguments to pass to the types constructor
     * <p>
     * the arguments to pass to the placement new
     */
    public NewExpression(final TypeUsage type, final List<Expression> args, final List<Expression> placement) {
        this.type = type;
        this.args = args;
        this.arraySize = null;
        this.placement = placement;
    }

    @Override
    String getCode(final Namespace currentNamespace, final String alignment) {
        final List<String> argCode = new ArrayList<>();
        if (args != null) {
            for (final Expression arg : args) {
                final String code = arg.getCode(currentNamespace, alignment + "\t");
                argCode.add(code);
            }
        }

        final List<String> placementCode = new ArrayList<>();
        if (placement != null) {
            for (final Expression arg : placement) {
                final String code = arg.getCode(currentNamespace, alignment);
                placementCode.add(code);
            }
        }

        return "new " +
               (placement == null ? "" : TextUtils.formatList(placementCode, "(", ", ", ") ")) +
               type.getQualifiedName(currentNamespace) +
               (arraySize == null ? "" : "[" + arraySize.getCode(currentNamespace) + "]") +
               (args == null ? "" : "(" + TextUtils.formatList(argCode, " ", "\t", "", ",\n" + alignment, " ") + ")");
    }

    @Override
    Set<Declaration> getForwardDeclarations() {
        final Set<Declaration> result = super.getForwardDeclarations();
        result.addAll(type.getDirectUsageForwardDeclarations());
        for (final Expression exp : args) {
            result.addAll(exp.getForwardDeclarations());
        }
        if (arraySize != null) {
            result.addAll(arraySize.getForwardDeclarations());
        }
        if (placement != null) {
            for (final Expression exp : placement) {
                result.addAll(exp.getForwardDeclarations());
            }
        }
        return result;
    }

    @Override
    Set<CodeFile> getIncludes() {
        final Set<CodeFile> result = super.getIncludes();

        result.addAll(type.getDirectUsageIncludes());
        for (final Expression exp : args) {
            result.addAll(exp.getIncludes());
        }
        if (arraySize != null) {
            result.addAll(arraySize.getIncludes());
        }
        if (placement != null) {
            for (final Expression exp : placement) {
                result.addAll(exp.getIncludes());
            }
        }
        return result;
    }

    @Override
    int getPrecedence() {
        return 3;
    }

    private final TypeUsage type;

    private final List<Expression> args;

    private final Expression arraySize;

    private final List<Expression> placement;

}
