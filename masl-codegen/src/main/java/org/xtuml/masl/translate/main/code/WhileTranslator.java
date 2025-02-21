/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
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
