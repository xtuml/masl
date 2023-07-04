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
package org.xtuml.masl.translate.main.code;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.ExpressionStatement;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.code.EraseStatement;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;

public class EraseTranslator extends CodeTranslator {

    protected EraseTranslator(final EraseStatement eraseStatement,
                              final Scope parentScope,
                              final CodeTranslator parentTranslator) {
        super(eraseStatement, parentScope, parentTranslator);

        final Expression
                dictionary =
                ExpressionTranslator.createTranslator(eraseStatement.getDictionary(),
                                                      getScope()).getWriteableExpression();
        final Expression
                key =
                ExpressionTranslator.createTranslator(eraseStatement.getKey(), getScope()).getReadExpression();
        getCode().appendStatement(new ExpressionStatement(new Function("eraseValue").asFunctionCall(dictionary,
                                                                                                    false,
                                                                                                    key)));

    }

}
