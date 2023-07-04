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
import org.xtuml.masl.metamodel.expression.EnumerateLiteral;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.EnumerationTranslator;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.Types;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;

import java.util.ArrayList;
import java.util.List;

public class CaseTranslator extends CodeTranslator {

    protected CaseTranslator(final org.xtuml.masl.metamodel.code.CaseStatement maslCase,
                             final Scope parentScope,
                             final CodeTranslator parentTranslator) {
        super(maslCase, parentScope, parentTranslator);

        final List<SwitchStatement.CaseCondition> alternatives = new ArrayList<SwitchStatement.CaseCondition>();

        Statement
                defaultStatement =
                new ThrowStatement(Architecture.programError.callConstructor(Literal.createStringLiteral(
                        "Invalid case condition")));

        EnumerationTranslator enumTranslator = null;

        for (final org.xtuml.masl.metamodel.code.CaseStatement.Alternative branch : maslCase.getAlternatives()) {

            final CodeBlock caseCode = new CodeBlock();
            for (final org.xtuml.masl.metamodel.code.Statement maslStatement : branch.getStatements()) {
                final CodeTranslator translator = createChildTranslator(maslStatement);
                final CodeBlock translation = translator.getFullCode();
                caseCode.appendStatement(translation);
            }

            if (branch.getConditions() == null) {
                defaultStatement = caseCode;
            } else {
                for (final org.xtuml.masl.metamodel.expression.Expression maslCondition : branch.getConditions()) {
                    // Special case for enumerate literals, as we can't select on a class,
                    // so need to peek inside and get the actual enumerate, which is not
                    // what we want to do anywhere else.
                    Expression condition;
                    if (enumTranslator != null || maslCondition instanceof EnumerateLiteral) {
                        final EnumerateLiteral enumerator = (EnumerateLiteral) maslCondition;

                        if (enumTranslator == null) {
                            // Force creation of the enumerate type, as it might not have been
                            // used yet, eg if the literal is just used in a function/service
                            // call.
                            Types.getInstance().getType(enumerator.getType());

                            enumTranslator =
                                    Types.getInstance().getEnumerateTranslator(enumerator.getType().getTypeDeclaration());
                        }
                        condition = enumTranslator.getEnumeratorIndex(enumerator.getValue());
                    } else {
                        condition =
                                ExpressionTranslator.createTranslator(maslCondition, getScope()).getReadExpression();
                    }
                    if (maslCondition != branch.getConditions().get(branch.getConditions().size() - 1)) {
                        final SwitchStatement.CaseCondition
                                alternative =
                                new SwitchStatement.CaseCondition(condition, null);
                        alternatives.add(alternative);
                    } else {
                        caseCode.appendStatement(new BreakStatement());
                        final SwitchStatement.CaseCondition
                                alternative =
                                new SwitchStatement.CaseCondition(condition, caseCode);
                        alternatives.add(alternative);
                    }
                }
            }
        }

        Expression
                discriminator =
                ExpressionTranslator.createTranslator(maslCase.getDiscriminator(), getScope()).getReadExpression();

        if (enumTranslator != null) {
            discriminator = enumTranslator.getGetIndex().asFunctionCall(discriminator, false);
        }

        getCode().appendStatement(new SwitchStatement(discriminator, alternatives, defaultStatement));
    }
}
