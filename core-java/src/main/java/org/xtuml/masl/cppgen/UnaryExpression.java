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

import java.util.Set;

public class UnaryExpression extends Expression {

    private final UnaryOperator operator;
    private final Expression expression;

    public UnaryExpression(final UnaryOperator operator, final Expression expression) {
        this.operator = operator;
        this.expression = expression;
    }

    @Override
    String getCode(final Namespace currentNamespace, final String alignment) {
        return (operator.isPrefix() ? operator.getCode() : "") +
               (getPrecedence() < expression.getPrecedence() ? "(" : "") +
               expression.getCode(currentNamespace, alignment) +
               (getPrecedence() < expression.getPrecedence() ? ")" : "") +
               (operator.isPrefix() ? "" : operator.getCode());
    }

    @Override
    Set<Declaration> getForwardDeclarations() {
        final Set<Declaration> result = super.getForwardDeclarations();
        result.addAll(expression.getForwardDeclarations());
        return result;
    }

    @Override
    Set<CodeFile> getIncludes() {
        final Set<CodeFile> result = super.getIncludes();
        result.addAll(expression.getIncludes());
        return result;
    }

    @Override
    int getPrecedence() {
        return operator.getPrecedence();
    }

    @Override
    boolean isTemplateType() {
        return expression.isTemplateType();
    }

}
