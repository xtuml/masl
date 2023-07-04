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
import org.xtuml.masl.metamodel.expression.IndexedNameExpression;
import org.xtuml.masl.translate.main.Scope;

public class IndexedNameTranslator extends ExpressionTranslator {

    IndexedNameTranslator(final IndexedNameExpression indexedName, final Scope scope) {
        final ExpressionTranslator prefixTrans = createTranslator(indexedName.getPrefix(), scope);

        final Expression maslIndex = createTranslator(indexedName.getIndex(), scope).getReadExpression();

        setReadExpression(ACCESS.asFunctionCall(prefixTrans.getReadExpression(), false, maslIndex));
        setWriteableExpression(ACCESS.asFunctionCall(prefixTrans.getWriteableExpression(), false, maslIndex));

    }

    private final static Function ACCESS = new Function("access");

    public Function getGetter() {
        return this.getter;
    }

    public Function getSetter() {
        return this.setter;
    }

    private Function getter;
    private Function setter;

}
