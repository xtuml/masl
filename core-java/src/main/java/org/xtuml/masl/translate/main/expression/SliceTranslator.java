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
package org.xtuml.masl.translate.main.expression;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.expression.SliceExpression;
import org.xtuml.masl.translate.main.Scope;

public class SliceTranslator extends ExpressionTranslator {

    SliceTranslator(final SliceExpression slice, final Scope scope) {
        final org.xtuml.masl.metamodel.expression.Expression prefix = slice.getPrefix();

        final ExpressionTranslator prefixTrans = createTranslator(prefix, scope);

        final Expression maslStartIndex = createTranslator(slice.getRange().getMin(), scope).getReadExpression();
        final Expression maslEndIndex = createTranslator(slice.getRange().getMax(), scope).getReadExpression();

        setReadExpression(SLICE.asFunctionCall(prefixTrans.getReadExpression(), false, maslStartIndex, maslEndIndex));
        setWriteableExpression(SLICE.asFunctionCall(prefixTrans.getWriteableExpression(),
                                                    false,
                                                    maslStartIndex,
                                                    maslEndIndex));

    }

    private final static Function SLICE = new Function("slice");

    public Function getGetter() {
        return this.getter;
    }

    public Function getSetter() {
        return this.setter;
    }

    private Function getter;
    private Function setter;

}
