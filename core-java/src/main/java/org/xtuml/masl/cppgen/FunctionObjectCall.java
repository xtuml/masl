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

public class FunctionObjectCall extends Expression {

    private final Expression funObj;
    private final List<Expression> arguments;

    public FunctionObjectCall(final Expression funObj, final List<Expression> arguments) {
        this.funObj = funObj;
        this.arguments = arguments;
    }

    public FunctionObjectCall(final Expression funObj, final Expression... expressions) {
        this.funObj = funObj;
        this.arguments = Arrays.asList(expressions);
    }

    @Override
    String getCode(final Namespace currentNamespace, final String alignment) {
        final StringBuffer buf = new StringBuffer();
        buf.append(funObj.getCode(currentNamespace));
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

        result.addAll(funObj.getIncludes());
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
