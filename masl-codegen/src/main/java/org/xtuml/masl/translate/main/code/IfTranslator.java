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

import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.IfStatement;
import org.xtuml.masl.cppgen.Statement;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;

public class IfTranslator extends CodeTranslator {

    protected IfTranslator(final org.xtuml.masl.metamodel.code.IfStatement maslIf,
                           final Scope parentScope,
                           final CodeTranslator parentTranslator) {
        super(maslIf, parentScope, parentTranslator);
        IfStatement previousIf = null;

        for (final org.xtuml.masl.metamodel.code.IfStatement.Branch branch : maslIf.getBranches()) {
            final CodeBlock ifCode = new CodeBlock();
            for (final org.xtuml.masl.metamodel.code.Statement maslStatement : branch.getStatements()) {
                final CodeTranslator translator = createChildTranslator(maslStatement);
                final CodeBlock translation = translator.getFullCode();
                ifCode.appendStatement(translation);
            }

            Statement current;
            if (branch.getCondition() != null) {
                final Expression
                        condition =
                        ExpressionTranslator.createTranslator(branch.getCondition(), getScope()).getReadExpression();
                current = new IfStatement(condition, ifCode);
                if (previousIf == null) {
                    getCode().appendStatement(current);
                    previousIf = (IfStatement) current;
                } else {
                    previousIf.setElse(current);
                    previousIf = (IfStatement) current;
                }
            } else {
                previousIf.setElse(ifCode);
            }

        }
    }

}
