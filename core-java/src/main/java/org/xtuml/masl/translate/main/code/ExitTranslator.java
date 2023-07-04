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

public class ExitTranslator extends CodeTranslator {

    protected ExitTranslator(final org.xtuml.masl.metamodel.code.ExitStatement exit,
                             final Scope parentScope,
                             final CodeTranslator parentTranslator) {
        super(exit, parentScope, parentTranslator);

        // Need to break out of the enclosing loop if the condition is true. Ideally
        // we'd just use a C++ break to do this, but this won't work if the exit is
        // inside a case statement nested inside the loop we want to exit, because
        // C++ will then only jump out of the innermost case statement. Therefore,
        // unfortunately, we must use a goto. Aaaaarrrrggghhh....

        boolean useGoto = false;
        CodeTranslator parent = getParentTranslator();
        while (!(parent instanceof ForTranslator || parent instanceof WhileTranslator)) {
            if (parent instanceof CaseTranslator) {
                // Can't use break, so must use goto
                useGoto = true;
                break;
            }
            parent = parent.getParentTranslator();
        }

        Statement exitStatement = null;
        if (useGoto) {
            if (parent instanceof ForTranslator) {
                exitStatement = new GotoStatement(((ForTranslator) parent).getEndOfLoopLabel());
            } else if (parent instanceof WhileTranslator) {
                exitStatement = new GotoStatement(((WhileTranslator) parent).getEndOfLoopLabel());
            }
        } else {
            exitStatement = new BreakStatement();
        }

        if (exit.getCondition() == null) {
            getCode().appendStatement(exitStatement);
        } else {
            final Expression
                    condition =
                    ExpressionTranslator.createTranslator(exit.getCondition(), getScope()).getReadExpression();
            getCode().appendStatement(new IfStatement(condition, exitStatement));

        }
    }

}
