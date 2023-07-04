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

import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;

public class WhileTranslator extends CodeTranslator {

    protected WhileTranslator(final org.xtuml.masl.metamodel.code.WhileStatement maslWhile,
                              final Scope parentScope,
                              final CodeTranslator parentTranslator) {
        super(maslWhile, parentScope, parentTranslator);
        final CodeBlock whileCode = new CodeBlock();

        final Expression
                condition =
                ExpressionTranslator.createTranslator(maslWhile.getCondition(), getScope()).getReadExpression();

        getCode().appendStatement(new WhileStatement(condition, whileCode));

        for (final org.xtuml.masl.metamodel.code.Statement maslStatement : maslWhile.getStatements()) {
            final CodeTranslator translator = createChildTranslator(maslStatement);
            final CodeBlock translation = translator.getFullCode();
            whileCode.appendStatement(translation);
        }

    }

    Label getEndOfLoopLabel() {
        if (label == null) {
            // Need a label at the end of the loop so that a masl Exit statement will
            // know where to go to. See ExitStatement for why we can't just use break
            // to
            // do this;
            label = new Label();
            getCode().appendStatement(new LabelStatement(label));
        }
        return label;
    }

    private Label label;

}
